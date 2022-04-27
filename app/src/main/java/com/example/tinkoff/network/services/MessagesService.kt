package com.example.tinkoff.network.services

import com.example.tinkoff.data.api.MessagesListJson
import com.example.tinkoff.data.api.SendCallBackJson
import com.example.tinkoff.data.api.StatusJson
import com.example.tinkoff.data.api.StreamsJson
import com.example.tinkoff.data.api.TopicsJson
import com.example.tinkoff.data.api.UpdateReactionCallBackJson
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.api.UsersJson
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessagesService {
    @GET("messages")
    fun getMessagesOfTheStream(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: String
    ): Single<MessagesListJson>

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("type") type: String = "stream",
        @Field("to") streamHeader: String,
        @Field("topic") topicHeader: String,
        @Field("content") content: String
    ): Single<SendCallBackJson>

    @FormUrlEncoded
    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Int,
        @Field("emoji_name") emojiName: String
    ): Single<UpdateReactionCallBackJson>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "messages/{message_id}/reactions", hasBody = true)
    fun removeReaction(
        @Path("message_id") messageId: Int,
        @Field("emoji_name") emojiName: String
    ): Single<UpdateReactionCallBackJson>
}
