package com.example.tinkoff.data.classes

import android.os.Parcelable
import androidx.annotation.Nullable
import com.example.tinkoff.data.states.UserStatus
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Parcelize
@Serializable
data class User(
    @SerialName("user_id")
    val id: Int,
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String,
    @Transient
    val status: UserStatus = UserStatus.ACTIVE,
    @SerialName("avatar_url")
    @Nullable
    val avatarUrl: String?,
    @SerialName("is_bot")
    val isBot: Boolean
) :
    Parcelable
