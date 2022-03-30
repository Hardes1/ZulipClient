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
import androidx.navigation.fragment.findNavController
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamHeader
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.classes.TopicHeader
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.databinding.FragmentStreamsBinding
import com.example.tinkoff.recyclerFeatures.adapters.StreamsRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.StreamItemDecoration
import com.example.tinkoff.ui.fragments.streamTabs.StreamsTabsFragmentDirections
import timber.log.Timber


class StreamFragment : Fragment() {


    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding
        get() = _binding!!

    private lateinit var list: List<Stream>
    private var counter = 0
    private lateinit var type: StreamsType

    private val changeStateCallBack: (Int, Boolean) -> Unit = { id, isSelected ->
        list.find { it.streamHeader.id == id }?.streamHeader?.isSelected = isSelected
        adapter.updateList(prepareListForAdapter(list))
    }

    private val navigateToMessageFragmentCallBack: (String, String) -> Unit =
        { appBarHeader, topicHeader ->
            val action =
                StreamsTabsFragmentDirections.actionNavigationStreamTabsToMessageFragment(
                    appBarHeader,
                    topicHeader
                )
            findNavController().navigate(action)
        }

    private val adapter: StreamsRecyclerAdapter by lazy {
        StreamsRecyclerAdapter(changeStateCallBack, navigateToMessageFragmentCallBack)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        type = StreamsType.values()[requireArguments().getInt(STREAMS_TYPE, 0)]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        counter = 0
        Timber.d("Stream type is $type")
        if (type == StreamsType.ALL_STREAMS)
            counter = ALL_STREAMS_MIN_ID
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


    private fun generateData(): List<Stream> {
        val list: MutableList<Stream> = mutableListOf()
        repeat(REPEAT_COUNT) {
            val header = generateStreamHeader()
            list.add(Stream(header, generateTopics(header.id)))
        }
        return list
    }

    private fun generateStreamHeader(): StreamHeader {
        Timber.d("current header counter is $counter")
        return StreamHeader(counter++, "$counter")
    }

    private fun generateTopics(parentId: Int): List<TopicHeader> {
        val list: MutableList<TopicHeader> = mutableListOf()
        val n = counter
        repeat(n) {
            list.add(TopicHeader(counter++, parentId, "$counter"))
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
        list = generateData()
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
        adapter.updateList(prepareListForAdapter(list))
    }


    companion object {

        private const val STREAMS_TYPE = "type"

        private const val ALL_STREAMS_MIN_ID = 50
        private const val REPEAT_COUNT = 3

        fun newInstance(type: StreamsType): StreamFragment {
            return StreamFragment().apply {
                arguments = Bundle().apply {
                    putInt(STREAMS_TYPE, type.ordinal)
                }
            }
        }
    }

}
