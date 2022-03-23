package com.example.tinkoff.recyclerFeatures.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class UserItemDecoration(private val smallSpacing: Int, private val bigSpacing: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = bigSpacing
            } else if (position == it.itemCount - 1)
                outRect.bottom = smallSpacing
            if (position != 0){
                outRect.top = smallSpacing
            }
        }
    }
}