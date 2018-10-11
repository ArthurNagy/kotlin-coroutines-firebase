package me.arthurnagy.kotlincoroutines.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Source
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//region GET
suspend inline fun <reified T> CollectionReference.awaitGet(source: Source = Source.DEFAULT): List<T> = this.get(source).awaitGet()

suspend inline fun <reified T> CollectionReference.awaitGetResult(source: Source = Source.DEFAULT): me.arthurnagy.kotlincoroutines.firebasecore.Result<List<T>> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitGet<T>(source) }
//endregion

//region ADD
suspend inline fun <T : Any> CollectionReference.awaitAdd(data: T): DocumentReference =
    suspendCancellableCoroutine { continuation ->
    this.add(data).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            task.result?.let { continuation.resume(it) } ?: continuation.resumeWithException(Exception("Couldn't add $data"))
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to add $data to collection: $this"))
        }
    }
}

suspend inline fun <T : Any> CollectionReference.awaitAddResult(data: T): me.arthurnagy.kotlincoroutines.firebasecore.Result<DocumentReference> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitAdd(data) }
//endregion