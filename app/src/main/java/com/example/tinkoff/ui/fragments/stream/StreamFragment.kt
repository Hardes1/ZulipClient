package com.example.tinkoff.ui.fragments.stream

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.tinkoff.R


class StreamFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streams, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.action_search)
        item.isVisible = true
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StreamFragment()
    }
}