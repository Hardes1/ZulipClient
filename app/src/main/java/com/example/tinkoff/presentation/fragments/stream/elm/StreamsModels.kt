package com.example.tinkoff.presentation.fragments.stream.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.classes.StreamsInterface

data class StreamsState(
    val streamsList: List<StreamsInterface> = emptyList(),
    val status: LoadingData = LoadingData.LOADING,
    val needToSearch: Boolean = false
)

sealed class StreamsEvent {

    sealed class UI : StreamsEvent() {
        data class LoadStreams(val type: StreamType) : UI()

        data class Refresh(val type: StreamType) : UI()

        data class FilterStreams(val type: StreamType, val word: String) : UI()

        data class SelectStream(val type: StreamType, val id: Int, val isSelected: Boolean) : UI()
    }

    sealed class Internal : StreamsEvent() {
        data class StreamsLoaded(
            val type: StreamType,
            val streams: List<StreamsInterface>,
            val dataSource: DataSource
        ) :
            Internal()

        data class StreamsFiltered(val streams: List<StreamsInterface>) : Internal()

        data class ErrorLoading(val error: Throwable) : Internal()

        data class ErrorSaving(val error: Throwable) : Internal()
    }
}

sealed class StreamsEffect {
    data class StreamsLoadError(val error: Throwable) : StreamsEffect()

    data class StreamsSaveError(val error: Throwable) : StreamsEffect()
}

sealed class StreamsCommand {
    data class LoadStreams(val type: StreamType, val dataSource: DataSource) : StreamsCommand()

    data class FilterStreams(val type: StreamType, val word: String) : StreamsCommand()

    data class Refresh(val type: StreamType) : StreamsCommand()

    data class SelectStream(val type: StreamType, val id: Int, val isSelected: Boolean) :
        StreamsCommand()
}
