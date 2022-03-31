package com.example.tinkoff.recyclerFeatures.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.StreamHeader
import com.example.tinkoff.data.classes.StreamsInterface
import com.example.tinkoff.data.classes.TopicHeader
import com.example.tinkoff.databinding.StreamItemBinding
import com.example.tinkoff.databinding.TopicItemBinding
import com.example.tinkoff.recyclerFeatures.diffUtils.StreamsDiffUtil

class StreamsRecyclerAdapter(
    private val selectCallBack: (Int, Boolean) -> Unit,
    private val navigateToMessageFragmentCallBack: (String, String) -> Unit
) :
    RecyclerView.Adapter<StreamsRecyclerAdapter.StreamsInterfaceViewHolder>() {


    private val differ = AsyncListDiffer(this, StreamsDiffUtil())
    private var list: List<StreamsInterface>
        private set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList


    abstract class StreamsInterfaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(content: StreamsInterface)
    }

    class StreamViewHolder(
        private val context: Context,
        private val selectCallBack: (Int, Boolean) -> Unit,
        private val binding: StreamItemBinding
    ) :
        StreamsInterfaceViewHolder(binding.root) {
        override fun bind(content: StreamsInterface) {
            require(content is StreamHeader)
            binding.streamNameTextView.text =
                context.resources.getString(R.string.stream_header, content.name)
            binding.selectedImageView.isSelected = content.isSelected
            binding.root.isSelected = content.isSelected
            binding.root.setOnClickListener {
                binding.selectedImageView.isSelected = !binding.selectedImageView.isSelected
                binding.root.isSelected = binding.selectedImageView.isSelected
                selectCallBack(content.id, binding.selectedImageView.isSelected)
            }
        }
    }

    class TopicViewHolder(
        private val findStreamByTopic: (Int) -> StreamsInterface?,
        private val navigateToMessageFragmentCallBack: (String, String) -> Unit,
        private val binding: TopicItemBinding
    ) :
        StreamsInterfaceViewHolder(binding.root) {
        override fun bind(content: StreamsInterface) {
            require(content is TopicHeader)
            binding.topicNameTextView.text = content.name
            val stream = findStreamByTopic(content.streamId)
            require(stream is StreamHeader)
            binding.root.setOnClickListener {
                navigateToMessageFragmentCallBack(stream.name, content.name)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamsInterfaceViewHolder {
        return when (viewType) {
            STREAM -> StreamViewHolder(
                parent.context,
                selectCallBack,
                StreamItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> TopicViewHolder(
                { index ->
                    list.find {
                        it is StreamHeader && it.id == index
                    }
                },
                navigateToMessageFragmentCallBack,
                TopicItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: StreamsInterfaceViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is StreamHeader)
            STREAM
        else
            TOPIC
    }

    fun updateList(otherList: List<StreamsInterface>) {
        list = otherList
    }

    companion object {
        const val STREAM = 1
        const val TOPIC = 2
    }


}