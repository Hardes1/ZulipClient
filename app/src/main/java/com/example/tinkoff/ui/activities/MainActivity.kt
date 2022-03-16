package com.example.tinkoff.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
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
import com.example.tinkoff.ui.views.FlexBoxLayout
import timber.log.Timber

class MainActivity : AppCompatActivity() {


    companion object {
        private const val FRAGMENT_TAG = "TAG"
    }


    private val viewModel: ReactionsViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private lateinit var recyclerAdapter: MessageRecyclerAdapter
    private val messageIndex: MutableLiveData<Int> = MutableLiveData(-1)


    private val decorator: MessageItemDecoration by lazy {
        MessageItemDecoration(
            resources.getDimensionPixelSize(R.dimen.message_content_small_recycler_distance),
            resources.getDimensionPixelSize(R.dimen.message_content_big_recycler_distance)
        )
    }
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var messagesList: MutableList<MessageContentInterface>
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setChangeTextListener()
        setButtonClickListener()
        initializeRecyclerView()
        val bottomSheetDialog = BottomSheetFragment.newInstance()
        messageIndex.observe(this) {
            if (it >= 0 && it < messagesList.size) {
                if (!bottomSheetDialog.isAdded)
                    bottomSheetDialog.show(supportFragmentManager, FRAGMENT_TAG)
            }
        }
        viewModel.reactionIndex.observe(this) {
            val messageIndexValue = messageIndex.value ?: -1
            Timber.d("MainActivityReaction: reactId - ${it}, messageId: $messageIndexValue")
            if (it >= 0 && it < EmotionsList.list.size && messageIndexValue >= 0 && messageIndexValue < messagesList.size) {
                Timber.d("MainActivityReaction: react - ${EmotionsList.list[it]}, messageId: $messageIndexValue")
                val view =
                    binding.recyclerView.findViewHolderForAdapterPosition(messageIndexValue)!!.
                    itemView.
                    findViewById<FlexBoxLayout>(R.id.flex_box_layout)
                view.addReaction(baseContext, EmotionsList.list[it])

            }
        }
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
                emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "How are you?", emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Why are you ignorin me???",
                emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda",
                emptyList(),
                SenderType.OTHER
            ),

            )
        list.add(
            MessageContent(
                counter++,
                "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda",
                emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Hello, dude",
                emptyList(),
                SenderType.OWN
            )
        )
        list.add(Date(counter++, "2 Feb"))
        list.add(MessageContent(counter++, "abobaaboba", emptyList(), SenderType.OTHER))
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
                    emptyList(),
                    SenderType.OWN
                )
            )
            Timber.d("list of Data: $messagesList")
            recyclerAdapter.list = messagesList
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
        recyclerAdapter = MessageRecyclerAdapter(messageIndex)
        recyclerAdapter.list = messagesList
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager
    }

}
