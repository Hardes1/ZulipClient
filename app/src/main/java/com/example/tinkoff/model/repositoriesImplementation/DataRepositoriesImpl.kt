package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.network.repositories.ApiRepository
import com.example.tinkoff.model.room.client.RoomClient
import com.example.tinkoff.model.room.repositories.RoomRepository

object DataRepositoriesImpl {
    val api: ApiRepository by lazy {
        ApiRepository()
    }

    val room: RoomRepository by lazy {
        RoomRepository(
            RoomClient.getStreamsDao(),
            RoomClient.getTopicsDao(),
            RoomClient.getMessagesDao()
        )
    }
}
