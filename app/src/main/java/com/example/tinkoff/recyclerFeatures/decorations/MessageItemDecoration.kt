package com.example.tinkoff.recyclerFeatures.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.recyclerFeatures.adapters.MessageRecyclerAdapter

class MessageItemDecoration(private val smallSpacing: Int, private val bigSpacing: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            val position = parent.getChildAdapterPosition(view)

            if (position > 0) {
                val currentType = adapter.getItemViewType(position)
                val previousType = adapter.getItemViewType(position - 1)
                val checkCurrentType = currentType == MessageRecyclerAdapter.MESSAGE_OTHER ||
                        currentType == MessageRecyclerAdapter.MESSAGE_OWN
                val checkPreviousType = previousType == MessageRecyclerAdapter.MESSAGE_OTHER ||
                        previousType == MessageRecyclerAdapter.MESSAGE_OWN
                if (checkCurrentType && checkPreviousType)
                    outRect.bottom = bigSpacing
                else
                    outRect.bottom = smallSpacing
            }
            when (position) {
                0 -> {
                    outRect.bottom = smallSpacing
                }
                adapter.itemCount - 1 -> {
                    outRect.top = smallSpacing
                }
                else -> return
            }
        }

    }
}
