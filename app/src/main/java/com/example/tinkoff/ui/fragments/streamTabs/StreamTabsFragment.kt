package com.example.tinkoff.ui.fragments.streamTabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tinkoff.recyclerFeatures.adapters.StreamsViewPagerAdapter
import com.example.tinkoff.databinding.FragmentStreamsTabsBinding
import com.google.android.material.tabs.TabLayoutMediator

class StreamsTabsFragment : Fragment() {


    private var _binding: FragmentStreamsTabsBinding? = null
    private val binding: FragmentStreamsTabsBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val streamsAdapter = StreamsViewPagerAdapter(lifecycle, childFragmentManager)
        binding.viewPager2.adapter = streamsAdapter
        initializeTabLayout()
    }


    private fun initializeTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Subscribed"
                1 -> "All streams"
                else -> throw NotImplementedError("Error Tab layout")
            }
        }.attach()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}