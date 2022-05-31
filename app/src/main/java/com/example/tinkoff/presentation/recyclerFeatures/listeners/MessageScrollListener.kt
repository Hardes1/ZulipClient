package com.example.tinkoff.presentation.recyclerFeatures.listeners

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tinkoff.presentation.recyclerFeatures.adapters.MessageRecyclerAdapter

class MessageScrollListener(
    private val manager: LinearLayoutManager,
    private val adapter: MessageRecyclerAdapter,
    private val downloadMessagesCallBack: () -> Unit
) : RecyclerView.OnScrollListener() {
    private var isDownloading = true

    fun setIsDownloading(value: Boolean) {
        isDownloading = value
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!isDownloading && manager.findLastVisibleItemPosition() == adapter.itemCount - START_DOWNLOAD) {
            isDownloading = true
            downloadMessagesCallBack()
        }
    }

    companion object {
        private const val START_DOWNLOAD = 5
    }
}
