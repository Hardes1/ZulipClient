package com.example.tinkoff.presentation.recyclerFeatures.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.R
import com.example.tinkoff.databinding.StreamRecyclerItemBinding
import com.example.tinkoff.databinding.TopicItemBinding
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.StreamsInterface
import com.example.tinkoff.presentation.classes.TopicHeader
import com.example.tinkoff.presentation.recyclerFeatures.diffUtils.StreamsDiffUtil

class StreamsRecyclerAdapter(
    private val selectCallBack: (Int, Boolean) -> Unit,
    private val navigateToMessageFragmentCallBack: (String, String) -> Unit,
) :
    RecyclerView.Adapter<StreamsRecyclerAdapter.StreamsInterfaceViewHolder>() {

    abstract class StreamsInterfaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(content: StreamsInterface)
    }

    class StreamViewHolder(
        private val context: Context,
        private val selectCallBack: (Int, Boolean) -> Unit,
        private val binding: StreamRecyclerItemBinding
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

    private val differ = AsyncListDiffer(this, StreamsDiffUtil())
    private var list: List<StreamsInterface>
        private set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamsInterfaceViewHolder {
        return when (viewType) {
            STREAM -> StreamViewHolder(
                parent.context,
                selectCallBack,
                StreamRecyclerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TOPIC -> TopicViewHolder(
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
            else -> throw NotImplementedError()
        }
    }

    override fun onBindViewHolder(holder: StreamsInterfaceViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is StreamHeader) {
            STREAM
        } else {
            TOPIC
        }
    }

    fun updateList(otherList: List<StreamsInterface>) {
        list = otherList
    }

    companion object {
        const val STREAM = 1
        const val TOPIC = 2
    }
}
