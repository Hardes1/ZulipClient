package com.example.tinkoff.data

data class Date(val id: Int, val date : String) : MessageContentInterface {
    override fun equals(other: Any?): Boolean {
        val oldItem = this
        return if(other !is Date)
            false
        else{
            oldItem.id == other.id && oldItem.date == other.date
        }

    }
}

