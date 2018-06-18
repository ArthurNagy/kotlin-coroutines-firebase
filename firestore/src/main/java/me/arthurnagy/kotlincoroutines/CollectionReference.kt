package me.arthurnagy.kotlincoroutines

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Source
import kotlin.coroutines.experimental.suspendCoroutine

//region GET
suspend inline fun <reified T> CollectionReference.awaitGet(source: Source = Source.DEFAULT): List<T> = this.get(source).awaitGet()

suspend inline fun <reified T> CollectionReference.awaitGetResult(source: Source = Source.DEFAULT): Result<List<T>> =
    wrapIntoResult { this.awaitGet<T>(source) }
//endregion

//region ADD
suspend inline fun <T : Any> CollectionReference.awaitAdd(data: T): DocumentReference = suspendCoroutine { continuation ->
    this.add(data).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to add $data to collection: $this"))
        }
    }
}

suspend inline fun <T : Any> CollectionReference.awaitAddResult(data: T): Result<DocumentReference> =
    wrapIntoResult { this.awaitAdd(data) }
//endregion