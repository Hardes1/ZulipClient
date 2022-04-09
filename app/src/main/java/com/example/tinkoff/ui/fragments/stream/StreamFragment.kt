package com.example.tinkoff.ui.fragments.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.databinding.FragmentStreamBinding
import com.example.tinkoff.recyclerFeatures.adapters.StreamsRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.StreamItemDecoration
import com.example.tinkoff.ui.fragments.streamTabs.StreamTabsFragmentDirections
import timber.log.Timber


class StreamFragment : Fragment() {


    private var _binding: FragmentStreamBinding? = null
    private val binding: FragmentStreamBinding
        get() = _binding!!

    private var searchItem: MenuItem? = null
    private val viewModel: StreamViewModel by viewModels()

    private val changeStateCallBack: (Int, Boolean) -> Unit = { id, isSelected ->
        viewModel.selectItem(id, isSelected)
    }

    private val updateStreamsCallBack: () -> Unit = {
        if (viewModel.state.value != LoadingData.FINISHED)
            viewModel.state.value = LoadingData.FINISHED
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
        StreamsRecyclerAdapter(
            changeStateCallBack,
            navigateToMessageFragmentCallBack,
            updateStreamsCallBack
        )
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
        _binding = FragmentStreamBinding.inflate(inflater, container, false)
        if (viewModel.type == null)
            viewModel.type = StreamsType.values()[requireArguments().getInt(STREAMS_TYPE, 0)]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(getString(R.string.debug_view_recreated))
        initializeRecyclerView()
        viewModel.displayedStreamsList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                LoadingData.LOADING, LoadingData.FINISHED -> {
                    binding.root.displayedChild = it.ordinal

                }
                LoadingData.ERROR -> {
                    binding.root.displayedChild = 1
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_streams_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> throw NotImplementedError()
            }
        }
        viewModel.isDownloaded.observe(viewLifecycleOwner) { isDownloaded ->
            if (isDownloaded) {
                val query = (searchItem?.actionView as SearchView?)?.query?.toString() ?: ""
                viewModel.searchStreamsAndTopics(query)
            } else
                viewModel.refresh()
        }
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
                viewModel.searchStreamsAndTopics(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchStreamsAndTopics(newText)
                return false
            }
        })
        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.isVisible = true
        refreshItem.setOnMenuItemClickListener {
            viewModel.isDownloaded.value = false
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
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
