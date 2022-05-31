package com.example.tinkoff.presentation.fragments.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentMessageBinding
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.presentation.activities.ReactionsViewModel
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.fragments.bottomSheet.BottomSheetFragment
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesEffect
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesEvent
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesState
import com.example.tinkoff.presentation.fragments.messages.elm.MessagesStoreFactory
import com.example.tinkoff.presentation.recyclerFeatures.adapters.MessageRecyclerAdapter
import com.example.tinkoff.presentation.recyclerFeatures.decorations.MessageItemDecoration
import com.example.tinkoff.presentation.recyclerFeatures.listeners.MessageScrollListener
import timber.log.Timber
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class MessagesFragment : ElmFragment<MessagesEvent, MessagesEffect, MessagesState>() {
    private var _binding: FragmentMessageBinding? = null
    private val binding: FragmentMessageBinding
        get() = _binding!!
    private val args: MessagesFragmentArgs by navArgs()
    private val reactionsViewModel: ReactionsViewModel by activityViewModels()
    private lateinit var adapter: MessageRecyclerAdapter
    private lateinit var toastSendingMessageError: Toast
    private lateinit var toastUpdatingReactionError: Toast
    private lateinit var toastErrorLoading: Toast
    private val bottomSheetDialog = BottomSheetFragment.newInstance()
    private var searchItem: MenuItem? = null
    private var refreshItem: MenuItem? = null
    private val decorator: MessageItemDecoration by lazy {
        MessageItemDecoration(
            resources.getDimensionPixelSize(R.dimen.message_content_small_recycler_distance),
            resources.getDimensionPixelSize(R.dimen.message_content_big_recycler_distance)
        )
    }
    private lateinit var layoutManager: LinearLayoutManager
    private val scrollListener: MessageScrollListener by lazy {
        MessageScrollListener(layoutManager, adapter, ::getNewMessages)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topicNameTextView.text =
            resources.getString(R.string.message_topic_header, args.topicHeader)
        binding.sendButton.isEnabled = false
        require(activity is AppCompatActivity)
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.stream_header, args.streamHeader)
        toastErrorLoading = Toast.makeText(
            requireContext(),
            R.string.error_messages_loading,
            Toast.LENGTH_SHORT
        )
        toastSendingMessageError =
            Toast.makeText(
                requireContext(),
                R.string.error_message_sending,
                Toast.LENGTH_SHORT
            )
        toastUpdatingReactionError =
            Toast.makeText(
                requireContext(),
                R.string.error_reaction_updating,
                Toast.LENGTH_SHORT
            )
        setChangeTextListener()
        setButtonSendClickListener()
        initializeRecyclerView()
        initializeReactionsViewModelLiveData()
    }

    private fun setButtonSendClickListener() {
        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "").trim()
            store.accept(
                MessagesEvent.UI.SendMessage(
                    args.streamHeader,
                    args.topicHeader,
                    text.toString()
                )
            )
        }
    }

    private fun initializeReactionsViewModelLiveData() {
        reactionsViewModel.reactionIndex.observe(viewLifecycleOwner) { reactionIndexValue ->
            if (reactionIndexValue != -1) {
                store.accept(MessagesEvent.UI.UpdateReactionByBottomSheet(reactionIndexValue))
            }
        }
    }

    private fun setChangeTextListener() {
        binding.messageContentTextView.addTextChangedListener {
            store.accept(MessagesEvent.UI.TextChanged(it.toString()))
        }
    }

    private fun initializeRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        adapter = MessageRecyclerAdapter(
            ::onSelectedPositionChanged,
            ::updateElementCallBack
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun onSelectedPositionChanged(id: Int) {
        store.accept(MessagesEvent.UI.SetLastClickedMessageId(id))
        if (!bottomSheetDialog.isAdded) {
            bottomSheetDialog.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }

    private fun updateElementCallBack(message: MessageContent, reactionPosition: Int) {
        store.accept(MessagesEvent.UI.SetLastClickedMessageId(message.id))
        store.accept(MessagesEvent.UI.UpdateReactionByClick(message, reactionPosition))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)
        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                store.accept(MessagesEvent.UI.ActionExpanded(true))
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                store.accept(MessagesEvent.UI.ActionExpanded(false))
                return true
            }
        })
        val searchView = searchItem?.actionView as SearchView
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                store.accept(MessagesEvent.UI.FilterMessages(query))
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                store.accept(MessagesEvent.UI.FilterMessages(newText))
                return false
            }
        })
        refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setOnMenuItemClickListener {
            store.accept(MessagesEvent.UI.LoadStreams(args.streamHeader, args.topicHeader))
            true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getNewMessages() {
        store.accept(MessagesEvent.UI.Paginate(args.streamHeader, args.topicHeader))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        reactionsViewModel.setReactionIndex(-1)
    }

    override fun createStore(): Store<MessagesEvent, MessagesEffect, MessagesState> {
        return MessagesStoreFactory().provide()
    }

    override val initEvent: MessagesEvent
        get() = MessagesEvent.UI.LoadStreams(args.streamHeader, args.topicHeader)

    override fun render(state: MessagesState) {
        if (state.isMessageSending)
            binding.sendButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.progress_image
            )
        if (state.needToScroll) {
            adapter.setChangedPositionCallBack {
                binding.recyclerView.smoothScrollToPosition(0)
                binding.messageContentTextView.setText("")
            }
        } else {
            adapter.setChangedPositionCallBack { }
        }
        if (state.isTextEmpty) {
            binding.sendButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_add_content
            )
            binding.sendButton.isEnabled = false
        } else {
            binding.sendButton.background = ContextCompat.getDrawable(
                requireContext(), R.drawable.ic_send
            )
            binding.sendButton.isEnabled = true
        }
        adapter.updateList(state.messagesList)
        val status = when (state.status) {
            LoadingData.LOADING -> SHOW_PROGRESS
            LoadingData.FINISHED -> HIDE_PROGRESS
            LoadingData.ERROR -> HIDE_PROGRESS
        }
        if (status == HIDE_PROGRESS && !state.isActionExpanded)
            scrollListener.setIsDownloading(false)
        else {
            scrollListener.setIsDownloading(true)
        }
        binding.progressBarIndicator.visibility =
            if (status == SHOW_PROGRESS) View.VISIBLE else View.INVISIBLE
        binding.bottomConstraintLayout.visibility =
            if (state.isInputVisible) View.VISIBLE else View.GONE
        searchItem?.isVisible = state.isSearchVisible
        refreshItem?.isVisible = state.isRefreshVisible
    }

    override fun handleEffect(effect: MessagesEffect) {
        when (effect) {
            is MessagesEffect.MessagesLoadError -> {
                toastErrorLoading.cancel()
                toastErrorLoading.show()
            }
            is MessagesEffect.MessagesSaveError -> {
                toastSendingMessageError.cancel()
                toastSendingMessageError.show()
            }
            is MessagesEffect.MessagesSendError -> {
                toastSendingMessageError.cancel()
                toastSendingMessageError.show()
            }
            is MessagesEffect.MessagesReactionError -> {
                toastUpdatingReactionError.cancel()
                toastUpdatingReactionError.show()
            }
        }
    }

    companion object {
        private const val SHOW_PROGRESS = 0
        private const val HIDE_PROGRESS = 1
        private const val FRAGMENT_TAG = "TAG"
    }
}
