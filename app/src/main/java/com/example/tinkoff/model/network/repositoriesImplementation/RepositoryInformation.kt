package com.example.tinkoff.model.network.repositoriesImplementation

import io.reactivex.Single

object RepositoryInformation {
    const val MY_ID = 493568
    const val ERROR = "Error happened"
}

fun <T> List<Single<T>>.zipSingles(): Single<List<T>> {
    if (this.isEmpty()) return Single.just(emptyList())
    return Single.zip(this) {
        @Suppress("UNCHECKED_CAST")
        return@zip (it as Array<T>).toList()
    }
}
