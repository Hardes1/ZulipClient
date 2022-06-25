package com.example.tinkoff.presentation.recyclerFeatures.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tinkoff.R
import com.example.tinkoff.databinding.DateItemBinding
import com.example.tinkoff.databinding.MessageOtherItemBinding
import com.example.tinkoff.databinding.MessageOwnItemBinding
import com.example.tinkoff.model.network.repositories.RepositoryInformation.MY_ID
import com.example.tinkoff.model.states.SenderType
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface
import com.example.tinkoff.presentation.classes.MessageDate
import com.example.tinkoff.presentation.recyclerFeatures.diffUtils.MessagesDiffUtil
import com.example.tinkoff.presentation.views.FlexBoxLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class MessageRecyclerAdapter(
    private val onSelectedPositionChanged: (messageId: Int) -> Unit,
    private val updateElementCallBack: (messageId: MessageContent, reactionPosition: Int) -> Unit,
) :
    RecyclerView.Adapter<MessageRecyclerAdapter.MessageContentViewHolder>() {

    abstract class MessageContentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(content: MessageContentInterface)
    }

    class MessageOtherViewHolder(
        private val onPositionChanged: (Int) -> Unit,
        private val context: Context,
        private val updateElementCallBack: (message: MessageContent, reactionPosition: Int) -> Unit,
        private val binding: MessageOtherItemBinding
    ) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is MessageContent)
            val text =
                binding.messageViewGroup.findViewById<MaterialTextView>(R.id.message_textview)
            val name =
                binding.messageViewGroup.findViewById<MaterialTextView>(R.id.nickname_textview)
            val avatar = binding.messageViewGroup.findViewById<ShapeableImageView>(R.id.avatar_icon)
            Glide
                .with(context)
                .load(content.avatarUrl)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(avatar)
            binding.messageViewGroup.setOnClickListener {
                onPositionChanged(content.id)
            }
            val flexBoxLayout =
                binding.messageViewGroup.findViewById<FlexBoxLayout>(R.id.flex_box_layout)
            while (flexBoxLayout.childCount > 1) {
                flexBoxLayout.removeViewAt(0)
            }
            flexBoxLayout.requestLayout()
            for (element in content.reactions) {
                val state = element.usersId.indexOfFirst { it == MY_ID } == -1
                flexBoxLayout.addOrUpdateReaction(
                    context,
                    element.emojiCode,
                    element.usersId.size,
                    !state
                )
                val index = flexBoxLayout.childCount - 2
                flexBoxLayout.getChildAt(index).isEnabled = element.isEnabled
                flexBoxLayout.getChildAt(index).setOnClickListener {
                    updateElementCallBack(content, index)
                }
            }
            flexBoxLayout.getChildAt(flexBoxLayout.childCount - 1)
                .setOnClickListener {
                    onPositionChanged(content.id)
                }
            text.text =
                HtmlCompat.fromHtml(content.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    .trim()
            name.text = content.senderName
        }
    }

    class MessageOwnViewHolder(
        private val onPositionChanged: (messageId: Int) -> Unit,
        private val context: Context,
        private val updateElementCallBack: (message: MessageContent, reactionPosition: Int) -> Unit,
        private val binding: MessageOwnItemBinding
    ) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is MessageContent)
            binding.messageTextview.text =
                HtmlCompat.fromHtml(content.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    .trim()
            binding.messageTextview.setOnClickListener {
                onPositionChanged(content.id)
            }
            val flexBoxLayout = binding.flexBoxLayout
            while (flexBoxLayout.childCount > 1) {
                flexBoxLayout.removeViewAt(0)
            }
            flexBoxLayout.requestLayout()
            for (element in content.reactions) {
                flexBoxLayout.addOrUpdateReaction(
                    context,
                    element.emojiCode,
                    element.usersId.size,
                    element.usersId.indexOfFirst { it == MY_ID } != -1
                )
                val index = flexBoxLayout.childCount - 2
                flexBoxLayout.getChildAt(index).isEnabled = element.isEnabled
                flexBoxLayout.getChildAt(index).setOnClickListener {
                    updateElementCallBack(content, index)
                }
            }
            flexBoxLayout.getChildAt(flexBoxLayout.childCount - 1)
                .setOnClickListener {
                    onPositionChanged(content.id)
                }
        }
    }

    class DateViewHolder(private val binding: DateItemBinding) :
        MessageContentViewHolder(binding.root) {
        override fun bind(content: MessageContentInterface) {
            require(content is MessageDate)
            binding.dateText.text = content.date
        }
    }

    private var listChangedCallBack: (() -> Unit)? = null
    private val differ = AsyncListDiffer(this, MessagesDiffUtil())
    private var list: List<MessageContentInterface>
        private set(value) {
            differ.submitList(value.reversed()) {
                listChangedCallBack?.invoke()
            }
        }
        get() = differ.currentList

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
                    onSelectedPositionChanged,
                    parent.context,
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
                    onSelectedPositionChanged,
                    parent.context,
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
        return if (item is MessageDate) {
            DATE
        } else {
            val newItem = item as MessageContent
            if (newItem.type == SenderType.OWN) {
                MESSAGE_OWN
            } else {
                MESSAGE_OTHER
            }
        }
    }

    fun updateList(otherList: List<MessageContentInterface>) {
        list = otherList
    }

    fun setChangedPositionCallBack(callBack: (() -> Unit)?) {
        listChangedCallBack = callBack
    }

    companion object {
        const val DATE: Int = 1
        const val MESSAGE_OTHER: Int = 2
        const val MESSAGE_OWN: Int = 3
    }
}
