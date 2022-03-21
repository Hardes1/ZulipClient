package com.example.tinkoff.data

data class MessageContent(
    val id: Int, val content: String,
    val reactions: MutableList<Reaction>,
    val type: SenderType
) :
    MessageContentInterface {
    override fun equals(other: Any?): Boolean {
        val oldItem = this
        return if (other !is MessageContent)
            false
        else {
            var flag =
                oldItem.id == other.id &&
                        oldItem.type == other.type &&
                        oldItem.content == other.content &&
                        oldItem.reactions.size == other.reactions.size
            if (flag) {
                for (i in 0 until oldItem.reactions.size) {
                    flag = flag and (oldItem.reactions[i] == other.reactions[i])
                }
            }
            flag
        }
    }
}



