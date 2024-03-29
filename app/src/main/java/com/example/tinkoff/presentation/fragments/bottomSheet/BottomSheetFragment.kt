package com.example.tinkoff.presentation.fragments.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.tinkoff.databinding.FragmentBottomSheetBinding
import com.example.tinkoff.presentation.activities.ReactionsViewModel
import com.example.tinkoff.presentation.classes.ReactionsData
import com.example.tinkoff.presentation.recyclerFeatures.adapters.ReactionsRecyclerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: ReactionsViewModel by activityViewModels()
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding: FragmentBottomSheetBinding
        get() = _binding!!
    private lateinit var reactionsRecyclerAdapter: ReactionsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        reactionsRecyclerAdapter =
            ReactionsRecyclerAdapter(
                onPositionChanged,
                ReactionsData.reactionsStringList.map { it.second }
            )
        binding.recyclerView.adapter = reactionsRecyclerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): BottomSheetFragment {
            return BottomSheetFragment()
        }
    }
}
