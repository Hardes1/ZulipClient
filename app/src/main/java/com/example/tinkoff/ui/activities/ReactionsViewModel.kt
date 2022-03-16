package com.example.tinkoff.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReactionsViewModel : ViewModel() {
    private val mutableReactionIndex = MutableLiveData(-1)
    val reactionIndex: LiveData<Int> get() = mutableReactionIndex

    fun setReactionIndex(index: Int) {
        mutableReactionIndex.value = index
    }
}