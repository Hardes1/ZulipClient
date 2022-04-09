package com.example.tinkoff.network

import android.content.Context
import com.example.tinkoff.R
import com.example.tinkoff.data.classes.*
import com.example.tinkoff.data.states.SenderType
import com.example.tinkoff.data.states.StreamsType
import com.example.tinkoff.data.states.UserStatus
import com.example.tinkoff.ui.fragments.messages.MessageFragment
import io.reactivex.Single
import timber.log.Timber
import kotlin.random.Random

object Repository {
    private var counter = 0
    private const val REPEAT_COUNT = 3
    private val random = Random(2)
    fun generateUsersData(): Single<List<User>> {
        counter = 0

        return Single.create { emitter ->
            if (random.nextBoolean()) {
                emitter.onSuccess(
                    listOf(
                        User(
                            counter++,
                            "Ustinov George",
                            "mannarts@gmail.com",
                            UserStatus.ONLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "Filatov Maxim",
                            "abobaMail@mail.ru",
                            UserStatus.ONLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "Ustinova Anna",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "Here you are",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "What a shot",
                            "abobaMail@mail.ru",
                            UserStatus.ONLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "Melnikov Igor",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "I want jump",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "What is this",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "ABOBA spirs",
                            "abobaMail@mail.ru",
                            UserStatus.ONLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "ABOBA pirs",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        ),
                        User(
                            counter++,
                            "ABOBA poso",
                            "abobaMail@mail.ru",
                            UserStatus.OFFLINE,
                            R.drawable.union
                        )
                    )
                )
            } else {
                emitter.onError(Throwable())
            }
        }
    }

    fun generateStreamsData(type: StreamsType): Single<List<Stream>> {
        counter = if (type == StreamsType.SUBSCRIBED) 0 else 50
        return Single.create { emitter ->
            val list: MutableList<Stream> = mutableListOf()
            repeat(REPEAT_COUNT) {
                val header = generateStreamHeader()
                list.add(Stream(header, generateTopics(header.id)))
            }
            if (random.nextBoolean()) {
                emitter.onSuccess(list)
            } else {
                emitter.onError(Throwable())
            }
        }
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


    fun generateMessagesData(): Single<MutableList<MessageContentInterface>> {
        return Single.create { emitter ->
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
                    mutableListOf(
                        Reaction(
                            ReactionsData.reactionsStringList[0],
                            mutableListOf(-1)
                        )
                    ),
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
            if(random.nextBoolean()) {
                emitter.onSuccess(list)
            }
            else{
                emitter.onError(Throwable())
            }
        }
    }

    fun generatePersonalUserData(context: Context): Single<User> {
        return Single.create { emitter ->
            if (random.nextBoolean()) {
                emitter.onSuccess(
                    User(
                        MessageFragment.MY_ID,
                        context.resources.getString(R.string.profile_name_text),
                        context.resources.getString(R.string.profile_email_text),
                        UserStatus.ONLINE,
                        R.drawable.union
                    )
                )
            } else {
                emitter.onError(Throwable())
            }
        }
    }

}