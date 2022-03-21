package com.example.tinkoff.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.adapters.MessageRecyclerAdapter
import com.example.tinkoff.data.ReactionsData
import com.example.tinkoff.data.Reaction
import com.example.tinkoff.data.MessageContent
import com.example.tinkoff.data.SenderType
import com.example.tinkoff.data.MessageContentInterface
import com.example.tinkoff.data.Date
import com.example.tinkoff.databinding.ActivityMainBinding
import com.example.tinkoff.decorations.MessageItemDecoration
import com.example.tinkoff.ui.bottomSheetFragment.BottomSheetFragment

class MainActivity : AppCompatActivity() {


    companion object {
        private const val FRAGMENT_TAG = "TAG"
        const val MY_ID = 1
    }


    private val viewModel: ReactionsViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private lateinit var recyclerAdapter: MessageRecyclerAdapter
    private val bottomSheetDialog = BottomSheetFragment.newInstance()
    private var messageIndex: Int = -1

    private val decorator: MessageItemDecoration by lazy {
        MessageItemDecoration(
            resources.getDimensionPixelSize(R.dimen.message_content_small_recycler_distance),
            resources.getDimensionPixelSize(R.dimen.message_content_big_recycler_distance)
        )
    }
    private lateinit var layoutManager: LinearLayoutManager
    private var messagesList: MutableList<MessageContentInterface> = mutableListOf()
    private var copiedMessagesList: MutableList<MessageContentInterface> = mutableListOf()

    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setChangeTextListener()
        setButtonSendClickListener()
        initializeRecyclerView()
        observeViewModels()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun generateData(): MutableList<MessageContentInterface> {
        val list: MutableList<MessageContentInterface> = mutableListOf()
        list.add(Date(counter++, "1 Feb"))
        list.add(
            MessageContent(
                counter++,
                "Hello, my friend!",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "How are you?", mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Why are you ignoring me???",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "I am tired....\nGoing bad now",
                mutableListOf(Reaction(ReactionsData.reactionsStringList[0], mutableListOf(-1))),
                SenderType.OTHER
            ),

            )
        list.add(
            MessageContent(
                counter++,
                "Eoteogsdfkjsdfkcbvcnb hahahaha",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Hello, dude",
                mutableListOf(),
                SenderType.OWN
            )
        )
        list.add(Date(counter++, "2 Feb"))
        list.add(MessageContent(counter++, "abobaaboba", mutableListOf(), SenderType.OTHER))
        return list
    }


    private fun setButtonSendClickListener() {

        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "").trim()
            binding.messageContentTextView.text = SpannableStringBuilder("")
            messagesList.add(
                MessageContent(
                    counter++,
                    text.toString(),
                    mutableListOf(),
                    SenderType.OWN
                )
            )
            updateAdapter()
        }
    }


    private fun setChangeTextListener() {
        binding.messageContentTextView.addTextChangedListener {
            when (it?.trim().isNullOrEmpty()) {
                true -> {
                    binding.sendButton.background = ContextCompat.getDrawable(
                        baseContext,
                        R.drawable.ic_add_content
                    )
                    binding.sendButton.isEnabled = false
                }
                false -> {
                    binding.sendButton.background = ContextCompat.getDrawable(
                        baseContext, R.drawable.ic_send
                    )
                    binding.sendButton.isEnabled = true
                }
            }
        }
    }


    private fun initializeRecyclerView() {
        messagesList = generateData()
        layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, true)
        recyclerAdapter = MessageRecyclerAdapter(
            onMessageIndexChanged,
            {
                messagesList = copiedMessagesList
            },
            updateElementCallBack,
            baseContext
        )
        updateAdapter()
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
    }


    private fun observeViewModels() {

        viewModel.reactionIndex.observe(this) { reactionIndexValue ->
            val messageIndexValue = messageIndex
            val reactionCondition = reactionIndexValue >= 0 &&
                    reactionIndexValue < ReactionsData.reactionsStringList.size
            val messageCondition = messageIndexValue >= 0 && messageIndexValue < messagesList.size
            if (reactionCondition && messageCondition) {
                val currentReactions =
                    (messagesList[messagesList.size - 1 - messageIndexValue] as MessageContent)
                        .reactions
                val pressedReactionIndex =
                    currentReactions.indexOfFirst { reaction ->
                        reaction.emoji == ReactionsData.reactionsStringList[reactionIndexValue]
                    }
                if (pressedReactionIndex == -1) {
                    currentReactions.add(
                        Reaction(
                            ReactionsData.reactionsStringList[reactionIndexValue],
                            mutableListOf(MY_ID)
                        )
                    )

                } else if (currentReactions[pressedReactionIndex].usersId.firstOrNull
                    { id -> id == MY_ID } == null
                ) {
                    currentReactions[pressedReactionIndex].usersId.add(MY_ID)
                }
                updateAdapter()
            }
        }
    }

    // ручное копирование
    private fun prepareList(): MutableList<MessageContentInterface> {
        val result: MutableList<MessageContentInterface> = mutableListOf()
        for (element in messagesList) {
            if (element is Date)
                result.add(Date(element.id, element.date))
            else if (element is MessageContent) {
                val reactionsContent = mutableListOf<Reaction>()
                for (reaction in element.reactions) {
                    val usersId = mutableListOf<Int>()
                    usersId.addAll(reaction.usersId)
                    reactionsContent.add(Reaction(reaction.emoji, usersId))
                }
                result.add(
                    MessageContent(
                        element.id,
                        element.content,
                        reactionsContent,
                        element.type
                    )
                )
            }
        }
        return result
    }


    private val onMessageIndexChanged: (Int) -> Unit = { position ->
        messageIndex = position
        if(!bottomSheetDialog.isAdded){
            bottomSheetDialog.show(supportFragmentManager, FRAGMENT_TAG)
        }
    }


    private val updateElementCallBack: (Int, Int, Boolean) -> Unit =
        { invertedAdapterPosition, reactionPosition, isAdd ->
            val adapterPosition = messagesList.size - 1 - invertedAdapterPosition
            val currentReaction =
                (messagesList[adapterPosition] as MessageContent).reactions[reactionPosition]
            if (isAdd) {
                if (currentReaction.usersId.find { it == MY_ID } == null)
                    currentReaction.usersId.add(MY_ID)
            } else {
                currentReaction.usersId.removeIf { it == MY_ID }
            }
            if (currentReaction.usersId.size == 0)
                (messagesList[adapterPosition] as MessageContent).reactions.remove(currentReaction)
            updateAdapter()
        }


    private fun updateAdapter() {
        copiedMessagesList = prepareList()
        recyclerAdapter.updateList(copiedMessagesList)
    }

}
