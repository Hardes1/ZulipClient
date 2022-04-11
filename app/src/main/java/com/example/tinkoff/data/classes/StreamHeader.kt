package com.example.tinkoff.data.classes

data class StreamHeader(val id: Int, val name: String, var isSelected: Boolean = false) :
    StreamsInterface
