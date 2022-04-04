package com.example.tinkoff.ui.fragments.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.databinding.FragmentStreamsBinding
import com.example.tinkoff.network.Repository
import com.example.tinkoff.recyclerFeatures.adapters.StreamsRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.StreamItemDecoration
import com.example.tinkoff.ui.fragments.streamTabs.StreamTabsFragmentDirections
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class StreamFragment : Fragment() {


    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding
        get() = _binding!!

    private val viewModel: StreamViewModel by viewModels()


    private val changeStateCallBack: (Int, Boolean) -> Unit = { id, isSelected ->
        viewModel.list?.find { it.streamHeader.id == id }?.streamHeader?.isSelected = isSelected
        adapter.updateList(prepareListForAdapter(viewModel.list ?: listOf()))
    }

    private val navigateToMessageFragmentCallBack: (String, String) -> Unit =
        { appBarHeader, topicHeader ->
            val action =
                StreamTabsFragmentDirections.actionNavigationStreamTabsToMessageFragment(
                    appBarHeader,
                    topicHeader
                )
            findNavController().navigate(action)
        }

    private val adapter: StreamsRecyclerAdapter by lazy {
        StreamsRecyclerAdapter(changeStateCallBack, navigateToMessageFragmentCallBack)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        if (viewModel.type == null)
            viewModel.type = StreamsType.values()[requireArguments().getInt(STREAMS_TYPE, 0)]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        Timber.d("TYPE is ${viewModel.type}")
        initializeRecyclerView()
    }


    private fun prepareListForAdapter(streams: List<Stream>): List<StreamsInterface> {
        val list: MutableList<StreamsInterface> = mutableListOf()
        streams.forEach { stream ->
            list.add(stream.streamHeader.copy())
            if (stream.streamHeader.isSelected) {
                stream.topics.forEach { topicHeader ->
                    list.add(topicHeader.copy())
                }
            }
        }
        return list
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val item: MenuItem = menu.findItem(R.id.action_search)
        item.isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeRecyclerView() {
        binding.streamsRecyclerView.adapter = adapter
        val topicDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.topic_item_decoration)
        val streamDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.stream_item_decoration)
        require(streamDrawable != null && topicDrawable != null)
        binding.streamsRecyclerView.addItemDecoration(
            StreamItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.streams_small_spacing_decoration
                ),
                resources.getDimensionPixelSize(
                    R.dimen.streams_big_spacing_decoration
                ),
                topicDrawable,
                streamDrawable
            )
        )
        if (viewModel.list == null)
            initializeDataList()
        else
            adapter.updateList(prepareListForAdapter(viewModel.list ?: listOf()))
    }


    private fun initializeDataList() {
        Single.create<List<Stream>> { emitter ->
            emitter.onSuccess(
                Repository.generateStreamsData(
                    viewModel.type ?: StreamsType.SUBSCRIBED
                )
            )
        }
            .subscribeOn(
                Schedulers.computation()
            ).flatMap {
                viewModel.list = it
                Single.just(prepareListForAdapter(it))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<StreamsInterface>> {
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onSuccess(value: List<StreamsInterface>) {
                    adapter.updateList(value)
                }

                override fun onError(e: Throwable?) {
                }
            })
    }


    companion object {

        private const val STREAMS_TYPE = "STREAM_TYPE"

        fun newInstance(type: StreamsType): StreamFragment {
            return StreamFragment().apply {
                arguments = Bundle().apply {
                    putInt(STREAMS_TYPE, type.ordinal)
                }
            }
        }
    }

}
