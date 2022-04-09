package com.example.tinkoff.ui.fragments.messages

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.R
import com.example.tinkoff.data.states.LoadingData
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

    private val decorator: MessageItemDecoration by lazy {
        MessageItemDecoration(
            resources.getDimensionPixelSize(R.dimen.message_content_small_recycler_distance),
            resources.getDimensionPixelSize(R.dimen.message_content_big_recycler_distance)
        )
    }
    private lateinit var layoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        observeLiveData()
        messagesViewModel.refreshMessages()
    }


    private fun setButtonSendClickListener() {
        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "").trim()
            binding.messageContentTextView.text = SpannableStringBuilder("")
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
            onSelectedPositionChanged,
            listChangedCallBack,
            updateElementCallBack
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
    }


    private val onSelectedPositionChanged: (Int) -> Unit = { id ->
        messagesViewModel.setMessageId(id)
        if (!bottomSheetDialog.isAdded) {
            bottomSheetDialog.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }


    private val updateElementCallBack: (Int, Int, Boolean) -> Unit =
        { id, reactionPosition, isAdd ->
            messagesViewModel.updateElementCallBack(id, reactionPosition, isAdd)
        }

    private val listChangedCallBack: () -> Unit =
        {
            if (messagesViewModel.needToScroll) {
                messagesViewModel.needToScroll = false
                binding.recyclerView.smoothScrollToPosition(0)
            }
        }


    private fun observeLiveData() {
        reactionsViewModel.reactionIndex.observe(viewLifecycleOwner) { reactionIndexValue ->
            messagesViewModel.updateReactions(reactionIndexValue)
        }
        messagesViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                LoadingData.LOADING, LoadingData.NONE -> {
                    binding.progressBarIndicator.visibility = View.VISIBLE
                    binding.bottomConstraintLayout.visibility = View.INVISIBLE
                }
                LoadingData.FINISHED -> {
                    searchItem?.isVisible = true
                    binding.progressBarIndicator.visibility = View.INVISIBLE
                    binding.bottomConstraintLayout.visibility = View.VISIBLE
                }
                else -> throw NotImplementedError()
            }
        }
        messagesViewModel.displayedMessagesList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
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
