package com.example.tinkoff.ui.fragments.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.tinkoff.adapters.ReactionsRecyclerAdapter
import com.example.tinkoff.data.ReactionsData
import com.example.tinkoff.databinding.FragmentBottomSheetBinding
import com.example.tinkoff.ui.activities.ReactionsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): BottomSheetFragment {
            return BottomSheetFragment()
        }
    }

    private val viewModel: ReactionsViewModel by activityViewModels()

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding: FragmentBottomSheetBinding
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
        val onPositionChanged: (Int) -> Unit = {
            viewModel.setReactionIndex(it)
            dismiss()
        }
        reactionsRecyclerAdapter = ReactionsRecyclerAdapter(onPositionChanged, ReactionsData.reactionsStringList)
        binding.recyclerView.adapter = reactionsRecyclerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
