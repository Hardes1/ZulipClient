package com.example.tinkoff.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.adapters.MessageRecyclerAdapter
import com.example.tinkoff.data.*
import com.example.tinkoff.databinding.ActivityMainBinding
import com.example.tinkoff.decorations.MessageItemDecoration
import com.example.tinkoff.ui.bottomSheetFragment.BottomSheetFragment
import com.example.tinkoff.ui.views.EmojiView
import com.example.tinkoff.ui.views.FlexBoxLayout
import timber.log.Timber

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
    private val messageIndex: MutableLiveData<Int> = MutableLiveData(-1)
    private val bottomSheetDialog = BottomSheetFragment.newInstance()


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
        setButtonClickListener()
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
                 counter++, "Why are you ignorin me???",
                 mutableListOf(),
                 SenderType.OTHER
             )
         )
         list.add(
             MessageContent(
                 counter++, "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda",
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


    private fun setButtonClickListener() {

        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "")
            binding.messageContentTextView.text = SpannableStringBuilder("")
            messagesList.add(
                MessageContent(
                    counter++,
                    text.toString(),
                    mutableListOf(),
                    SenderType.OWN
                )
            )
            Timber.d("list of Data: $messagesList")
            updateAdapter()
        }
    }


    private fun setChangeTextListener() {
        binding.messageContentTextView.addTextChangedListener {
            when (it.isNullOrEmpty()) {
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
            messageIndex,
            imageButtonClickListener,
            {
                messagesList = copiedMessagesList
                binding.recyclerView.smoothScrollToPosition(0)
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
        messageIndex.observe(this) {
            if (it >= 0 && it < messagesList.size) {
                if (!bottomSheetDialog.isAdded)
                    bottomSheetDialog.show(supportFragmentManager, FRAGMENT_TAG)
            }
        }
        viewModel.reactionIndex.observe(this) { reactionIndexValue ->
            val messageIndexValue = messageIndex.value ?: -1
            Timber.d("MainActivityReaction: reactId - ${reactionIndexValue}, messageId: $messageIndexValue")
            if (reactionIndexValue >= 0 && reactionIndexValue < ReactionsData.reactionsStringList.size && messageIndexValue >= 0 && messageIndexValue < messagesList.size) {
                Timber.d("MainActivityReaction: react - ${ReactionsData.reactionsStringList[reactionIndexValue]}, messageId: $messageIndexValue")
                val view =
                    binding.recyclerView.findViewHolderForAdapterPosition(messageIndexValue)!!.itemView.findViewById<FlexBoxLayout>(
                        R.id.flex_box_layout
                    )

                val currentReactions =
                    (messagesList[messagesList.size - 1 - messageIndexValue] as MessageContent).reactions
                val pressedReactionIndex =
                    currentReactions.indexOfFirst { reaction -> reaction.emoji == ReactionsData.reactionsStringList[reactionIndexValue] }

                if (pressedReactionIndex == -1) {
                    currentReactions.add(
                        Reaction(
                            ReactionsData.reactionsStringList[reactionIndexValue],
                            mutableListOf(MY_ID)
                        )
                    )

                } else if (currentReactions[pressedReactionIndex].users_id.firstOrNull { id -> id == MY_ID } == null) {
                    currentReactions[pressedReactionIndex].users_id.add(MY_ID)
                }
                Timber.d("Reaction click list called: $messagesList")
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
                    usersId.addAll(reaction.users_id)
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


    private val updateElementCallBack: (Int, Int, Boolean) -> Unit =
        { invertedAdapterPosition, reactionPosition, isAdd ->
            val adapterPosition = messagesList.size - 1 - invertedAdapterPosition
            val currentReaction =
                (messagesList[adapterPosition] as MessageContent).reactions[reactionPosition]
            if (isAdd) {
                if (currentReaction.users_id.find { it == MY_ID } == null)
                    currentReaction.users_id.add(MY_ID)
            } else {
                currentReaction.users_id.removeIf { it == MY_ID }
            }
            if(currentReaction.users_id.size == 0)
            (messagesList[adapterPosition] as MessageContent).reactions.remove(currentReaction)
            updateAdapter()
        }

    private val imageButtonClickListener: (View) -> Unit =
        {
            if (!bottomSheetDialog.isAdded)
                bottomSheetDialog.show(supportFragmentManager, FRAGMENT_TAG)
        }

    private fun updateAdapter() {
        copiedMessagesList = prepareList()
        recyclerAdapter.list = copiedMessagesList
    }


}
