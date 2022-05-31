package com.example.tinkoff.model.network.serializers

import com.example.tinkoff.model.states.UserStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class StatusSerializer : KSerializer<UserStatus> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(STATUS, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UserStatus {
        return when (decoder.decodeString()) {
            ACTIVE -> {
                UserStatus.ACTIVE
            }
            IDLE -> {
                UserStatus.IDLE
            }
            OFFLINE -> {
                UserStatus.OFFLINE
            }
            else -> {
                throw NotImplementedError()
            }
        }
    }

    override fun serialize(encoder: Encoder, value: UserStatus) {
        when (value) {
            UserStatus.ACTIVE -> {
                encoder.encodeString(ACTIVE)
            }
            UserStatus.IDLE -> {
                encoder.encodeString(IDLE)
            }
            UserStatus.OFFLINE -> {
                encoder.encodeString(OFFLINE)
            }
        }
    }

    companion object {
        private const val STATUS = "status"
        private const val ACTIVE = "active"
        private const val IDLE = "idle"
        private const val OFFLINE = "offline"
    }
}
