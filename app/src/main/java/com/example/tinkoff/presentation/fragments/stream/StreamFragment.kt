package com.example.tinkoff.presentation.fragments.stream

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentStreamBinding
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.activities.MainActivity
import com.example.tinkoff.presentation.fragments.stream.di.DaggerStreamsComponent
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsEffect
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsEvent
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsState
import com.example.tinkoff.presentation.fragments.stream.elm.StreamsStoreFactory
import com.example.tinkoff.presentation.fragments.streamTabs.StreamTabsFragmentDirections
import com.example.tinkoff.presentation.recyclerFeatures.adapters.StreamsRecyclerAdapter
import com.example.tinkoff.presentation.recyclerFeatures.decorations.StreamItemDecoration
import timber.log.Timber
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import javax.inject.Inject

class StreamFragment : ElmFragment<StreamsEvent, StreamsEffect, StreamsState>() {
    private var _binding: FragmentStreamBinding? = null
    private val binding: FragmentStreamBinding
        get() = _binding!!
    private var searchItem: MenuItem? = null
    private var refreshItem: MenuItem? = null
    private lateinit var streamLoadingErrorToast: Toast

    @Inject
    lateinit var factory: StreamsStoreFactory

    private val streamType: StreamType by lazy {
        StreamType.values()[
            requireArguments().getInt(
                STREAMS_TYPE,
                0
            )
        ]
    }

    private fun changeStateCallBack(id: Int, isSelected: Boolean) {
        store.accept(StreamsEvent.UI.SelectStream(streamType, id, isSelected))
    }

    private fun navigateToMessageFragmentCallBack(appBarHeader: String, topicHeader: String) {
        val action =
            StreamTabsFragmentDirections.actionNavigationStreamTabsToMessageFragment(
                appBarHeader,
                topicHeader
            )
        findNavController().navigate(action)
    }

    private val adapter: StreamsRecyclerAdapter by lazy {
        StreamsRecyclerAdapter(
            ::changeStateCallBack,
            ::navigateToMessageFragmentCallBack,
        )
    }

    override fun onAttach(context: Context) {
        DaggerStreamsComponent.factory()
            .create(
                (requireActivity() as MainActivity)
                    .getMainActivityComponent()
            )
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentStreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        streamLoadingErrorToast = Toast.makeText(
            requireContext(),
            getString(R.string.error_streams_loading),
            Toast.LENGTH_SHORT
        )
        initializeRecyclerView()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)
        searchItem?.isVisible = true
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                store.accept(StreamsEvent.UI.FilterStreams(streamType, query))
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                store.accept(StreamsEvent.UI.FilterStreams(streamType, newText))
                return false
            }
        })
        refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setOnMenuItemClickListener {
            store.accept(StreamsEvent.UI.Refresh(streamType))
            true
        }
        refreshItem?.isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override val initEvent: StreamsEvent
        get() = StreamsEvent.UI.LoadStreams(streamType)

    override fun render(state: StreamsState) {
        adapter.updateList(state.streamsList)
        val status = when (state.status) {
            LoadingData.LOADING -> SHOW_SHIMMER
            LoadingData.FINISHED -> HIDE_SHIMMER
            LoadingData.ERROR -> HIDE_SHIMMER
        }
        if (status != binding.root.displayedChild)
            binding.root.displayedChild = status
        if (state.needToSearch) {
            store.accept(StreamsEvent.UI.FilterStreams(streamType, getSearchString()))
        }
    }

    override fun createStore(): Store<StreamsEvent, StreamsEffect, StreamsState> {
        return factory.provide()
    }

    override fun handleEffect(effect: StreamsEffect) {
        when (effect) {
            is StreamsEffect.StreamsSaveError -> {
                Timber.e(effect.error, "error during saving streams")
            }
            is StreamsEffect.StreamsLoadError -> {
                Timber.e(effect.error, "error during loading streams")
                streamLoadingErrorToast.cancel()
                streamLoadingErrorToast.show()
            }
        }
    }

    private fun getSearchString(): String {
        return (searchItem?.actionView as SearchView?)?.query?.toString() ?: ""
    }

    companion object {
        private const val SHOW_SHIMMER = 0
        private const val HIDE_SHIMMER = 1
        private const val STREAMS_TYPE = "STREAM_TYPE"
        fun newInstance(type: StreamType): StreamFragment {
            return StreamFragment().apply {
                arguments = Bundle().apply {
                    putInt(STREAMS_TYPE, type.ordinal)
                }
            }
        }
    }
}
