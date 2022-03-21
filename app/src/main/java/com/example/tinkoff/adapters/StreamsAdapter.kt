package com.example.tinkoff.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tinkoff.ui.fragments.stream.StreamFragment

class StreamsAdapter(lifecycle: Lifecycle, supportFragmentManager: FragmentManager) :
    FragmentStateAdapter(supportFragmentManager, lifecycle) {
    override fun getItemCount(): Int = SIZE

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StreamFragment()
            1 -> StreamFragment()
            else -> throw NotImplementedError("Error hapenned")
        }
    }

    companion object {
        const val SIZE = 2
    }
}