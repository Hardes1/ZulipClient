package com.example.tinkoff.ui.fragments.stream

import androidx.lifecycle.ViewModel
import com.example.tinkoff.data.classes.Stream
import com.example.tinkoff.data.states.StreamsType

class StreamViewModel : ViewModel() {
    var list: List<Stream>? = null
    var type: StreamsType? = null
}