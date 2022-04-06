package com.example.tinkoff.data.classes

import android.os.Parcelable
import com.example.tinkoff.R
import com.example.tinkoff.data.states.UserStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val status: UserStatus,
    val drawableId: Int
) :
    Parcelable
