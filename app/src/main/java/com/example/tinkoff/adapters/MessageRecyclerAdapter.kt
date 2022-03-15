package com.example.tinkoff.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.R
import com.example.tinkoff.data.Date
import com.example.tinkoff.data.MessageContent
import com.example.tinkoff.data.MessageContentInterface
import com.example.tinkoff.data.SenderType
import com.example.tinkoff.databinding.DateItemBinding
import com.example.tinkoff.databinding.MessageOtherItemBinding
import com.example.tinkoff.databinding.MessageOwnItemBinding
import com.google.android.material.textview.MaterialTextView

class MessageRecyclerAdapter(private val list: List<MessageContentInterface>) :
    RecyclerView.Adapter<MessageRecyclerAdapter.MessageContentViewHolder>() {


    private val sizeList = list.size


    sealed class MessageContentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(content: MessageContentInterface)
    }


    class MessageOtherViewHolder(private val binding: MessageOtherItemBinding) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            val newContent = content as MessageContent
            val text =
                binding.messageViewGroup.findViewById<MaterialTextView>(R.id.message_textview)
            text.text = newContent.content
        }
    }

    class MessageOwnViewHolder(private val binding: MessageOwnItemBinding) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            val newContent = content as MessageContent
            binding.messageTextview.text = newContent.content
        }
    }


    class DateViewHolder(private val binding: DateItemBinding) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            val newContent = content as Date
            binding.dateText.text = newContent.date
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageContentViewHolder {
        return when (viewType) {
            DATE -> {
                DateViewHolder(
                    DateItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            MESSAGE_OTHER -> {
                MessageOtherViewHolder(
                    MessageOtherItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            MESSAGE_OWN -> {
                MessageOwnViewHolder(
                    MessageOwnItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw NotImplementedError("error")
        }
    }

    override fun onBindViewHolder(contentViewHolder: MessageContentViewHolder, position: Int) {
        contentViewHolder.bind(list[sizeList - position - 1])
    }

    override fun getItemCount(): Int = list.size


    override fun getItemViewType(position: Int): Int {
        val item = list[sizeList - position - 1]
        return if (item is Date) {
            DATE
        } else {
            val newItem = item as MessageContent
            if (newItem.type == SenderType.OWN)
                MESSAGE_OWN
            else
                MESSAGE_OTHER
        }
    }

    companion object {
        const val DATE: Int = 1
        const val MESSAGE_OTHER: Int = 2
        const val MESSAGE_OWN: Int = 3
    }

}