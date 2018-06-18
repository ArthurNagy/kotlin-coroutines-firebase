package me.arthurnagy.kotlincoroutines

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

suspend inline fun <reified T> Query.awaitGet(source: Source = Source.DEFAULT): List<T> = this.get(source).awaitGet()

suspend inline fun <reified T> Query.awaitGetResult(source: Source = Source.DEFAULT): Result<List<T>> =
    wrapIntoResult { this.awaitGet<T>(source) }