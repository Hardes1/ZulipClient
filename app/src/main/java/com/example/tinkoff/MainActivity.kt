package com.example.tinkoff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinkoff.adapters.MessageRecyclerAdapter
import com.example.tinkoff.data.Date
import com.example.tinkoff.data.MessageContent
import com.example.tinkoff.data.MessageContentInterface
import com.example.tinkoff.data.SenderType
import com.example.tinkoff.databinding.ActivityMainBinding
import com.example.tinkoff.decorations.MessageItemDecoration
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private lateinit var recyclerAdapter: MessageRecyclerAdapter

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
        messagesList = generateData()
        layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, true)
        recyclerAdapter = MessageRecyclerAdapter()
        recyclerAdapter.list = messagesList
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.addItemDecoration(decorator)
        binding.recyclerView.layoutManager = layoutManager

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
                "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda",
                emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda", emptyList(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "dasdasdhdjashdsjdjhsjfhasj\nsjadasjdhdjadsjda",
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
                counter++, "dasdasdhdjashdsjdjhsjfhasjsjadasjdhdjadsjda",
                emptyList(),
                SenderType.OWN
            )
        )
        list.add(Date(1, "2 Feb"))
        list.add(MessageContent(6, "abobaaboba", emptyList(), SenderType.OTHER))
        return list
    }


    private fun setButtonClickListener() {

        binding.sendButton.setOnClickListener {
            val text = (binding.messageContentTextView.text ?: "")
            binding.messageContentTextView.text = SpannableStringBuilder("")
            instanateNewList()
            messagesList.add(
                MessageContent(
                    counter++,
                    text.toString(),
                    emptyList(),
                    SenderType.OWN
                )
            )
            recyclerAdapter.list = messagesList
        }
    }

    // Создаём новую ссылку
    private fun instanateNewList() {
        val tmp : MutableList<MessageContentInterface> = mutableListOf()
        messagesList.forEach { tmp.add(it) }
        messagesList = tmp
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


}
