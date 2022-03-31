package com.example.tinkoff.ui.fragments.stream

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.classes.StreamHeader
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.classes.TopicHeader
import com.example.tinkoff.databinding.FragmentStreamsBinding
import com.example.tinkoff.recyclerFeatures.adapters.StreamsRecyclerAdapter


class StreamFragment : Fragment() {


    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding
        get() = _binding!!

    private val list = generateData()
    private var counter = 0

    private val changeStateCallBack: (Int, Boolean) -> Unit = { id, isSelected ->
        list.find { it.streamHeader.id == id }?.streamHeader?.isSelected = isSelected
        adapter.updateList(prepareListForAdapter(list))
    }

    private val adapter: StreamsRecyclerAdapter by lazy {
        StreamsRecyclerAdapter(changeStateCallBack)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding.streamsRecyclerView.adapter = adapter
        adapter.updateList(prepareListForAdapter(list))
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
        val sz = 3
        repeat(sz) {
            val header = generateStreamHeader()
            list.add(Stream(header, generateTopics(header.id)))
        }
        return list
    }

    private fun generateStreamHeader(): StreamHeader {
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


    companion object {
        @JvmStatic
        fun newInstance() =
            StreamFragment()
    }
}