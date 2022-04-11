package com.example.tinkoff.ui.fragments.streamTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tinkoff.R
import com.example.tinkoff.databinding.FragmentStreamsTabsBinding
import com.example.tinkoff.recyclerFeatures.adapters.StreamsViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class StreamTabsFragment : Fragment() {

    private var _binding: FragmentStreamsTabsBinding? = null
    private var searchItem: MenuItem? = null
    private val binding: FragmentStreamsTabsBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d(getString(R.string.debug_fragment_recreated))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        Timber.d(getString(R.string.debug_view_recreated))
        val streamsAdapter = StreamsViewPagerAdapter(lifecycle, childFragmentManager)
        binding.viewPager2.adapter = streamsAdapter
        initializeTabLayout()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.d(getString(R.string.tab_is_selected))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Timber.d(getString(R.string.tab_is_unselected))
                searchItem?.collapseActionView()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Timber.d(getString(R.string.tab_is_reselected))
            }
        })
    }

    private fun initializeTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.subscribed_header)
                1 -> getString(R.string.all_streams_header)
                else -> throw NotImplementedError(getString(R.string.error_tab_layout))
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        searchItem = menu.findItem(R.id.action_search)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
