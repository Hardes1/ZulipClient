package com.example.tinkoff.ui.fragments.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentMessageBinding


class MessageFragment : Fragment() {


    private var _binding: FragmentMessageBinding? = null
    private val binding: FragmentMessageBinding
        get() = _binding!!
    private val args: MessageFragmentArgs by navArgs()
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
        require(activity is AppCompatActivity)
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.stream_header, args.appBarHeader)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
