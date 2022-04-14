package com.example.tinkoff.ui.fragments.messages

import android.os.Bundle
import android.text.SpannableStringBuilder
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.data.states.LoadingData
import com.example.tinkoff.data.states.MessageState
import com.example.tinkoff.databinding.FragmentMessageBinding
import com.example.tinkoff.recyclerFeatures.adapters.MessageRecyclerAdapter
import com.example.tinkoff.recyclerFeatures.decorations.MessageItemDecoration
import com.example.tinkoff.ui.activities.ReactionsViewModel
import com.example.tinkoff.ui.fragments.bottomSheet.BottomSheetFragment

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding: FragmentMessageBinding
        get() = _binding!!
    private val args: MessageFragmentArgs by navArgs()
    private val messagesViewModel: MessagesViewModel by viewModels()
    private val reactionsViewModel: ReactionsViewModel by activityViewModels()
    private lateinit var adapter: MessageRecyclerAdapter
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
            resources.getString(R.string.stream_header, args.appBarHeader)
        setChangeTextListener()
        setButtonSendClickListener()
        initializeRecyclerView()
        initializeLiveDataObservers()
    }

    private fun setButtonSendClickListener() {
        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "").trim()
            messagesViewModel.addMessage(text)
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
            ::onSelectedPositionChanged,
            ::updateElementCallBack
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun onSelectedPositionChanged(id: Int) {
        messagesViewModel.setMessageId(id)
        if (!bottomSheetDialog.isAdded) {
            bottomSheetDialog.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }

    private fun updateElementCallBack(id: Int, reactionPosition: Int, isAdd: Boolean) {
        messagesViewModel.setMessageId(id)
        messagesViewModel.reactionClickedCallBack(reactionPosition, isAdd)
    }

    private fun initializeReactionsViewModelLiveData() {
        reactionsViewModel.reactionIndex.observe(viewLifecycleOwner) { reactionIndexValue ->
            messagesViewModel.tryAddReaction(reactionIndexValue)
        }
    }

    private fun initializeMessagesViewModelLiveData() {
        initializeFragmentViewLifeData()
        initializeRecyclerLiveData()
    }

    private fun initializeFragmentViewLifeData() {
        messagesViewModel.messageState.observe(viewLifecycleOwner) {
            when (it) {
                MessageState.FAILED -> {
                    Toast.makeText(
                        requireContext(),
                        "Error sending message to server",
                        Toast.LENGTH_SHORT
                    ).show()
                    messagesViewModel.initializeDisplaySubject()
                }
                MessageState.SUCCESSFUL -> {
                    binding.messageContentTextView.text = SpannableStringBuilder("")
                }
                MessageState.SENDING -> {
                }
                else -> throw NotImplementedError()
            }
        }
        messagesViewModel.loadingDataState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingData.LOADING -> {
                    searchItem?.isVisible = false
                    refreshItem?.isVisible = false
                    binding.progressBarIndicator.visibility = View.VISIBLE
                    binding.bottomConstraintLayout.visibility = View.INVISIBLE
                }
                LoadingData.FINISHED -> {
                    searchItem?.isVisible = true
                    refreshItem?.isVisible = false
                    binding.progressBarIndicator.visibility = View.INVISIBLE
                    binding.bottomConstraintLayout.visibility = View.VISIBLE
                    messagesViewModel.searchMessages("")
                }
                LoadingData.ERROR -> {
                    searchItem?.isVisible = false
                    refreshItem?.isVisible = true
                    binding.progressBarIndicator.visibility = View.INVISIBLE
                    binding.bottomConstraintLayout.visibility = View.INVISIBLE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_messages_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> throw NotImplementedError()
            }
        }
    }

    private fun initializeRecyclerLiveData() {
        messagesViewModel.displayedMessagesList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
        messagesViewModel.needToScroll.observe(viewLifecycleOwner) {
            when (it) {
                true -> adapter.setChangedPositionCallBack {
                    binding.recyclerView.smoothScrollToPosition(
                        0
                    )
                }
                false -> adapter.setChangedPositionCallBack(null)
            }
        }
    }

    private fun initializeLiveDataObservers() {
        initializeReactionsViewModelLiveData()
        initializeMessagesViewModelLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                binding.bottomConstraintLayout.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                binding.bottomConstraintLayout.visibility = View.VISIBLE
                return true
            }
        })
        val searchView = searchItem?.actionView as SearchView
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                messagesViewModel.searchMessages(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                messagesViewModel.searchMessages(newText)
                return false
            }
        })
        refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem?.setOnMenuItemClickListener {
            messagesViewModel.refreshMessages(requireContext())
            true
        }
        messagesViewModel.refreshMessages(requireContext())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val FRAGMENT_TAG = "TAG"
        const val MY_ID = 1
    }
}
