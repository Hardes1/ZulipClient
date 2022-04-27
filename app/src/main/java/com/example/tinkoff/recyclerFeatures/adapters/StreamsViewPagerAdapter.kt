package com.example.tinkoff.recyclerFeatures.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tinkoff.data.states.StreamType
import com.example.tinkoff.ui.fragments.stream.StreamFragment

class StreamsViewPagerAdapter(lifecycle: Lifecycle, supportFragmentManager: FragmentManager) :
    FragmentStateAdapter(supportFragmentManager, lifecycle) {
    override fun getItemCount(): Int = SIZE

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StreamFragment.newInstance(StreamType.SUBSCRIBED)
            1 -> StreamFragment.newInstance(StreamType.ALL_STREAMS)
            else -> throw NotImplementedError("Error hapenned")
        }
    }

    companion object {
        const val SIZE = 2
    }
}
