package com.example.tinkoff.network

import com.example.tinkoff.data.classes.*
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.data.states.UserStatus
import timber.log.Timber

object Repository {
    private var counter = 0
    private const val REPEAT_COUNT = 3
    fun generateUsersData(): List<User> {
        counter = 0
        return listOf(
            User(counter++, "Устинов Георгий", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "Устинова Алёна", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Привет, как дела", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Проверяю текст", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "Мельников Игорь", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Как же хочется прыгать", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "Откуда я знаю", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA spirs", "abobaMail@mail.ru", UserStatus.ONLINE),
            User(counter++, "ABOBA pirs", "abobaMail@mail.ru", UserStatus.OFFLINE),
            User(counter++, "ABOBA poso", "abobaMail@mail.ru", UserStatus.OFFLINE)
        )
    }

    fun generateStreamsData(): List<Stream> {
        counter = 0
        val list: MutableList<Stream> = mutableListOf()
        repeat(REPEAT_COUNT) {
            val header = generateStreamHeader()
            list.add(Stream(header, generateTopics(header.id)))
        }
        return list
    }

    private fun generateStreamHeader(): StreamHeader {
        Timber.d("current header counter is $counter")
        return StreamHeader(counter++, "$counter")
    }

    private fun generateTopics(parentId: Int): List<TopicHeader> {
        val list: MutableList<TopicHeader> = mutableListOf()
        val n = counter
        repeat(n) {
            list.add(TopicHeader(counter++, parentId, "$counter"))
        }
        return list
    }


    fun generateMessagesData(): MutableList<MessageContentInterface> {
        counter = 0
        val list: MutableList<MessageContentInterface> = mutableListOf()
        list.add(Date(counter++, "1 Feb"))
        list.add(
            MessageContent(
                counter++,
                "Hello, my friend!",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "How are you?", mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Why are you ignoring me???",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "I am tired....\nGoing bad now",
                mutableListOf(Reaction(ReactionsData.reactionsStringList[0], mutableListOf(-1))),
                SenderType.OTHER
            ),

            )
        list.add(
            MessageContent(
                counter++,
                "Eoteogsdfkjsdfkcbvcnb hahahaha",
                mutableListOf(),
                SenderType.OTHER
            )
        )
        list.add(
            MessageContent(
                counter++, "Hello, dude",
                mutableListOf(),
                SenderType.OWN
            )
        )
        list.add(Date(counter++, "2 Feb"))
        list.add(MessageContent(counter++, "abobaaboba", mutableListOf(), SenderType.OTHER))
        return list
    }

}