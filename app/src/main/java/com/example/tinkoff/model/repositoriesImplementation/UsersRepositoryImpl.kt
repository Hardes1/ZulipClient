package com.example.tinkoff.model.repositoriesImplementation

import com.example.tinkoff.model.network.repositories.UsersApiRepository
import com.example.tinkoff.model.network.repositoriesImplementation.RepositoryInformation
import com.example.tinkoff.model.network.repositoriesImplementation.zipSingles
import com.example.tinkoff.model.repositories.UsersRepository
import com.example.tinkoff.model.storages.UsersStorage
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor() : UsersRepository {
    @Inject
    lateinit var api: UsersApiRepository
    @Inject
    lateinit var storage: UsersStorage

    override fun init(): Single<List<User>> {
        return storage.needToDownload().flatMap { needToDownload ->
            if (needToDownload)
                getUsersFromInternet()
            else
                getUsersFromCache()
        }
    }

    override fun getUsersFromCache(): Single<List<User>> {
        return storage.getAllUsers()
    }

    override fun getUsersFromInternet(): Single<List<User>> {
        return api.getAllUsers().flatMap { data ->
            data.users.filter { !it.isBot && it.id != RepositoryInformation.MY_ID }.map { user ->
                api.getOnlineUserStatus(user.id).map {
                    User(
                        user.id,
                        user.name,
                        user.email,
                        it.presence.aggregated.status,
                        user.avatarUrl,
                        user.isBot
                    )
                }
            }.zipSingles().flatMapCompletable { newList ->
                storage.updateList(newList)
            }.andThen(storage.getAllUsers())
        }
    }

    override fun filterUsersByString(word: String): Single<List<User>> {
        return storage.filterListByName(word)
    }
}
