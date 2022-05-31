package com.example.tinkoff.presentation.recyclerFeatures.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.presentation.recyclerFeatures.adapters.StreamsRecyclerAdapter

class StreamItemDecoration(
    private val smallSpacing: Int,
    private val bigSpacing: Int,
    private val topicDrawable: Drawable,
    private val streamDrawable: Drawable
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            val position = parent.getChildAdapterPosition(view)
            setPaddings(position, adapter, outRect)
        }
    }

    private fun setPaddings(
        position: Int,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        outRect: Rect
    ) {
        if (position != RecyclerView.NO_POSITION) {
            val positionType = adapter.getItemViewType(position)
            if (positionType == StreamsRecyclerAdapter.STREAM) {
                if (position == adapter.itemCount - 1 ||
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.STREAM
                ) {
                    outRect.bottom = smallSpacing
                }
            } else if (positionType == StreamsRecyclerAdapter.TOPIC) {
                if (position < adapter.itemCount - 1 &&
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.TOPIC
                ) {
                    outRect.bottom = smallSpacing
                } else if (position < adapter.itemCount - 1 &&
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.STREAM
                ) {
                    outRect.bottom = bigSpacing
                }
            }
        }
    }

    private fun drawSeparators(
        position: Int,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        view: View,
        canvas: Canvas
    ) {
        if (position != RecyclerView.NO_POSITION) {
            val positionType = adapter.getItemViewType(position)
            if (positionType == StreamsRecyclerAdapter.STREAM) {
                if (position == adapter.itemCount - 1 ||
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.STREAM
                ) {
                    val left = view.left
                    val top = view.bottom
                    val right = view.right
                    val bottom = top + smallSpacing
                    streamDrawable.bounds = Rect(left, top, right, bottom)
                    streamDrawable.draw(canvas)
                }
            } else if (positionType == StreamsRecyclerAdapter.TOPIC) {
                if (position < adapter.itemCount - 1 &&
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.TOPIC
                ) {
                    val left = view.left
                    val top = view.bottom
                    val right = view.right
                    val bottom = top + smallSpacing
                    topicDrawable.bounds = Rect(left, top, right, bottom)
                    topicDrawable.draw(canvas)
                } else if (position < adapter.itemCount - 1 &&
                    adapter.getItemViewType(position + 1) == StreamsRecyclerAdapter.STREAM
                ) {
                    val left = view.left
                    val top = view.bottom
                    val right = view.right
                    val bottom = top + bigSpacing
                    topicDrawable.bounds = Rect(left, top, right, bottom)
                    topicDrawable.draw(canvas)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            parent.children.forEach { view ->
                val position = parent.getChildAdapterPosition(view)
                drawSeparators(position, adapter, view, canvas)
            }
        }
    }
}
