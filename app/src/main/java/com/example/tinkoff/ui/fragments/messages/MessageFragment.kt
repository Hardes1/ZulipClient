package com.example.tinkoff.ui.fragments.messages

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.Date
import com.example.tinkoff.data.classes.MessageContent
import com.example.tinkoff.data.classes.MessageContentInterface
import com.example.tinkoff.data.classes.Reaction
import com.example.tinkoff.data.classes.ReactionsData
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.databinding.FragmentMessageBinding
import com.example.tinkoff.network.Repository
import com.example.tinkoff.recyclerFeatures.adapters.MessageRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.MessageItemDecoration
import com.example.tinkoff.ui.activities.ReactionsViewModel
import com.example.tinkoff.ui.fragments.bottomSheet.BottomSheetFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MessageFragment : Fragment() {


    private var _binding: FragmentMessageBinding? = null
    private val binding: FragmentMessageBinding
        get() = _binding!!
    private val args: MessageFragmentArgs by navArgs()
    private val messagesViewModel: MessagesViewModel by viewModels()
    private val reactionsViewModel: ReactionsViewModel by activityViewModels()
    private lateinit var adapter: MessageRecyclerAdapter
    private val bottomSheetDialog = BottomSheetFragment.newInstance()
    private var messageIndex: Int = -1
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val decorator: MessageItemDecoration by lazy {
        MessageItemDecoration(
            resources.getDimensionPixelSize(R.dimen.message_content_small_recycler_distance),
            resources.getDimensionPixelSize(R.dimen.message_content_big_recycler_distance)
        )
    }
    private lateinit var layoutManager: LinearLayoutManager

    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topicNameTextView.text =
            resources.getString(R.string.message_topic_header, args.topicHeader)
        binding.sendButton.isEnabled = false
        require(activity is AppCompatActivity)
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.stream_header, args.appBarHeader)
        setChangeTextListener()
        setButtonSendClickListener()
        initializeRecyclerView()
        observeViewModels()
    }


    private fun setButtonSendClickListener() {

        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "").trim()
            binding.messageContentTextView.text = SpannableStringBuilder("")
            updateAdapter(
                MessageContent(
                    counter++,
                    text.toString(),
                    mutableListOf(),
                    SenderType.OWN
                )
            )
        }
    }


    private fun setChangeTextListener() {
        binding.messageContentTextView.addTextChangedListener {
            when (it?.trim().isNullOrEmpty() || it?.trim().isNullOrBlank()) {
                true -> {
                    binding.sendButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_add_content
                    )
                    binding.sendButton.isEnabled = false
                }
                false -> {
                    binding.sendButton.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_send
                    )
                    binding.sendButton.isEnabled = true
                }
            }
        }
    }


    private fun initializeRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        adapter = MessageRecyclerAdapter(
            onMessageIndexChanged,
            { updatedList ->
                val newList = copyList(updatedList.toMutableList())
                if (newList.size != messagesViewModel.messagesList?.size ?: 0)
                    binding.recyclerView.scrollToPosition(0)
                messagesViewModel.messagesList = newList
            },
            updateElementCallBack
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
        if (messagesViewModel.messagesList == null) {
            initializeDataList()
        } else {
            updateAdapter()
        }
    }


    private fun initializeDataList() {
        Single.create<MutableList<MessageContentInterface>> { emitter ->
            emitter.onSuccess(
                Repository.generateMessagesData()
            )
        }.delay(DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribeOn(
                Schedulers.computation()
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : SingleObserver<MutableList<MessageContentInterface>> {
                    override fun onSubscribe(d: Disposable?) {
                        compositeDisposable.dispose()
                        binding.progressBarIndicator.visibility = View.VISIBLE
                    }

                    override fun onSuccess(value: MutableList<MessageContentInterface>) {
                        messagesViewModel.messagesList = value
                        binding.progressBarIndicator.visibility = View.INVISIBLE
                        updateAdapter()
                    }

                    override fun onError(e: Throwable?) {
                        binding.progressBarIndicator.visibility = View.INVISIBLE
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_messages_loading),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })

    }


    private fun copyList(messagesList: MutableList<MessageContentInterface>): MutableList<MessageContentInterface> {
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
        if (!bottomSheetDialog.isAdded) {
            bottomSheetDialog.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }


    private val updateElementCallBack: (Int, Int, Boolean) -> Unit =
        { invertedAdapterPosition, reactionPosition, isAdd ->
            val messagesList = messagesViewModel.messagesList
            require(messagesList != null)
            val adapterPosition =
                messagesList.size - 1 - invertedAdapterPosition
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


    private fun updateAdapter(message: MessageContentInterface? = null) {
        val newList = copyList(messagesViewModel.messagesList ?: mutableListOf())
        if (message != null)
            newList.add(message)
        adapter.updateList(newList)
    }


    private fun observeViewModels() {
        reactionsViewModel.reactionIndex.observe(viewLifecycleOwner) { reactionIndexValue ->

            val messageIndexValue = messageIndex
            val reactionCondition = reactionIndexValue >= 0 &&
                    reactionIndexValue < ReactionsData.reactionsStringList.size
            val messageCondition =
                messageIndexValue >= 0 && messageIndexValue < messagesViewModel.messagesList?.size ?: 0
            if (reactionCondition && messageCondition) {
                val size = messagesViewModel.messagesList?.size ?: 0
                val currentReactions =
                    (messagesViewModel.messagesList?.get(size - 1 - messageIndexValue) as MessageContent)
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        compositeDisposable.dispose()

    }

    companion object {
        private const val DELAY_TIME: Long = 1000
        private const val FRAGMENT_TAG = "TAG"
        const val MY_ID = 1
    }

}
