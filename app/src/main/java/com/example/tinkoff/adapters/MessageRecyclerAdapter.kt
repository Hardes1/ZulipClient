package com.example.tinkoff.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.R
import com.example.tinkoff.data.Date
import com.example.tinkoff.data.MessageContent
import com.example.tinkoff.data.MessageContentInterface
import com.example.tinkoff.data.SenderType
import com.example.tinkoff.databinding.DateItemBinding
import com.example.tinkoff.databinding.MessageOtherItemBinding
import com.example.tinkoff.databinding.MessageOwnItemBinding
import com.example.tinkoff.ui.activities.MainActivity
import com.example.tinkoff.ui.views.FlexBoxLayout
import com.google.android.material.textview.MaterialTextView

class MessageRecyclerAdapter(
    private val onPositionChanged : (Int) -> Unit,
    private val listChanged: () -> Unit,
    private val updateElementCallBack: (invertedAdapterPosition: Int, reactionPosition: Int, Boolean) -> Unit,
    private val context: Context
) :
    RecyclerView.Adapter<MessageRecyclerAdapter.MessageContentViewHolder>() {


    private val differ = AsyncListDiffer(this, CustomDiffUtil())
    private var list: List<MessageContentInterface>
        set(value) {
            differ.submitList(value.reversed()) { listChanged }
        }
        get() = differ.currentList

    abstract class MessageContentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(content: MessageContentInterface)
    }


    class MessageOtherViewHolder(

        private val onPositionChanged : (Int) -> Unit,
        private val context: Context,
        private val updateElementCallBack: (invertedAdapterPosition: Int, reactionPosition: Int, Boolean) -> Unit,
        private val binding: MessageOtherItemBinding
    ) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is MessageContent)
            val text =
                binding.messageViewGroup.findViewById<MaterialTextView>(R.id.message_textview)
            binding.messageViewGroup.setOnClickListener {
               onPositionChanged(adapterPosition)
            }
            val flexBoxLayout =
                binding.messageViewGroup.findViewById<FlexBoxLayout>(R.id.flex_box_layout)

            while (flexBoxLayout.childCount > 1) {
                flexBoxLayout.removeViewAt(0)
            }
            flexBoxLayout.requestLayout()
            // TODO : fix user (remove 1)
            for (element in content.reactions) {
                val state = element.usersId.indexOfFirst { it == 1 } == -1
                flexBoxLayout.addOrUpdateReaction(
                    context,
                    element.emoji,
                    element.usersId.size,
                    !state
                )
                flexBoxLayout.requestLayout()
                val index = flexBoxLayout.childCount - 2

                flexBoxLayout.getChildAt(index).setOnClickListener {
                    it.isSelected = !it.isSelected
                    updateElementCallBack(adapterPosition, index, it.isSelected)
                }
            }
            flexBoxLayout.requestLayout()

            flexBoxLayout.getChildAt(flexBoxLayout.childCount - 1)
                .setOnClickListener{
                    onPositionChanged(adapterPosition)
                }


            text.text = content.content
        }
    }

    class MessageOwnViewHolder(
        private val onPositionChanged: (Int) -> Unit,
        private val context: Context,
        private val updateElementCallBack: (invertedAdapterPosition: Int, reactionPosition: Int, Boolean) -> Unit,
        private val binding: MessageOwnItemBinding
    ) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is MessageContent)
            binding.messageTextview.text = content.content
            binding.messageTextview.setOnClickListener {
                onPositionChanged(adapterPosition)
            }
            val flexBoxLayout = binding.flexBoxLayout
            while (flexBoxLayout.childCount > 1) {
                flexBoxLayout.removeViewAt(0)
            }
            flexBoxLayout.requestLayout()
            // TODO : fix user (remove 1)
            for (element in content.reactions) {
                flexBoxLayout.addOrUpdateReaction(
                    context,
                    element.emoji,
                    element.usersId.size,
                    element.usersId.indexOfFirst { it == 1 } != -1
                )
                val index = flexBoxLayout.childCount - 2
                flexBoxLayout.getChildAt(index).setOnClickListener {
                    it.isSelected = !it.isSelected
                    updateElementCallBack(adapterPosition, index, it.isSelected)
                }
            }

            flexBoxLayout.getChildAt(flexBoxLayout.childCount - 1)
                .setOnClickListener{
                    onPositionChanged(adapterPosition)
                }
        }
    }


    class DateViewHolder(private val binding: DateItemBinding) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is Date)
            binding.dateText.text = content.date
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
                    onPositionChanged,
                    context,
                    updateElementCallBack,
                    MessageOtherItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            MESSAGE_OWN -> {
                MessageOwnViewHolder(
                    onPositionChanged,
                    context,
                    updateElementCallBack,
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
        contentViewHolder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size


    override fun getItemViewType(position: Int): Int {
        val item = list[position]
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


    fun updateList(otherList: List<MessageContentInterface>) {
        list = otherList
    }

    companion object {
        const val DATE: Int = 1
        const val MESSAGE_OTHER: Int = 2
        const val MESSAGE_OWN: Int = 3
    }

}
