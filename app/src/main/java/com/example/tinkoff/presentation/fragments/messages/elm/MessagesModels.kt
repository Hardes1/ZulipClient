package com.example.tinkoff.presentation.fragments.messages.elm

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.LoadingData
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.MessageContentInterface

data class MessagesState(
    val messagesList: List<MessageContentInterface> = emptyList(),
    val status: LoadingData = LoadingData.LOADING,
    val isSearchVisible: Boolean = false,
    val isRefreshVisible: Boolean = false,
    val isInputVisible: Boolean = false,
    val isMessageSending: Boolean = false,
    val isActionExpanded: Boolean = false,
    val needToScroll: Boolean = false,
    val isTextEmpty: Boolean = true
)

sealed class MessagesEvent {
    sealed class UI : MessagesEvent() {
        data class LoadStreams(val streamHeader: String, val topicHeader: String) : UI()

        data class Refresh(val streamHeader: String, val topicHeader: String) : UI()

        data class Paginate(val streamHeader: String, val topicHeader: String) : UI()

        data class TextChanged(val text: String) : UI()

        data class FilterMessages(val word: String) : UI()

        data class SendMessage(
            val streamHeader: String,
            val topicHeader: String,
            val message: String
        ) : UI()

        data class SetLastClickedMessageId(val id: Int) : UI()

        data class UpdateReactionByBottomSheet(val reactionIndexValue: Int) : UI()

        data class UpdateReactionByClick(
            val message: MessageContent,
            val reactionPosition: Int
        ) : UI()

        data class ActionExpanded(
            val expanded: Boolean
        ) : UI()
    }

    sealed class Internal : MessagesEvent() {
        data class MessagesLoaded(
            val streamHeader: String,
            val topicHeader: String,
            val messages: List<MessageContentInterface>,
            val dataSource: DataSource
        ) :
            Internal()

        data class MessageSended(
            val messages: List<MessageContentInterface>
        ) : Internal()

        data class ReactionUpdated(
            val messages: List<MessageContentInterface>
        ) : Internal()

        data class MessagesFiltered(val messages: List<MessageContentInterface>) : Internal()

        data class ErrorLoading(val error: Throwable) : Internal()

        data class ErrorPaginating(val error: Throwable) : Internal()

        data class ErrorSaving(val error: Throwable) : Internal()

        data class ErrorSending(val error: Throwable) : Internal()

        data class ErrorUpdatingReaction(val error: Throwable) : Internal()

        data class ReactionStateUpdated(
            val messagesList: List<MessageContentInterface>
        ) : Internal()
    }
}

sealed class MessagesEffect {
    data class MessagesLoadError(val error: Throwable) : MessagesEffect()

    data class MessagesSaveError(val error: Throwable) : MessagesEffect()

    data class MessagesSendError(val error: Throwable) : MessagesEffect()

    data class MessagesReactionError(val error: Throwable) : MessagesEffect()
}

sealed class MessagesCommand {
    data class Paginate(
        val streamHeader: String,
        val topicHeader: String
    ) : MessagesCommand()

    data class LoadMessages(
        val streamHeader: String,
        val topicHeader: String,
        val dataSource: DataSource
    ) : MessagesCommand()

    data class FilterMessages(val word: String) : MessagesCommand()

    data class SendMessage(val streamHeader: String, val topicHeader: String, val message: String) :
        MessagesCommand()

    data class SetLastClickedMessageId(val id: Int) : MessagesCommand()

    data class UpdateReactionByBottomSheetFragment(val reactionIndexValue: Int) : MessagesCommand()

    data class UpdateReactionByClick(
        val message: MessageContent,
        val reactionPosition: Int
    ) : MessagesCommand()
}
