package com.example.tinkoff.data.classes

data class Date(val id: Int, val date : String) : MessageContentInterface {
    override fun equals(other: Any?): Boolean {
        val oldItem = this
        return if(other !is Date)
            false
        else{
            oldItem.id == other.id && oldItem.date == other.date
        }

    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + date.hashCode()
        return result
    }
}

