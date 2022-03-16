package com.example.tinkoff.ui.bottomSheetFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tinkoff.adapters.ReactionsRecyclerAdapter
import com.example.tinkoff.data.EmotionsList
import com.example.tinkoff.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {


    private var _binding : FragmentBottomSheetBinding? = null
    private val binding : FragmentBottomSheetBinding
        get() = _binding!!
    private lateinit var reactionsRecyclerAdapter: ReactionsRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reactionsRecyclerAdapter = ReactionsRecyclerAdapter(EmotionsList.list)
        binding.recyclerView.adapter = reactionsRecyclerAdapter
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}