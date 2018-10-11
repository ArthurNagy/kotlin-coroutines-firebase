package me.arthurnagy.kotlincoroutines.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private suspend fun <T> awaitTaskQueryList(task: Task<QuerySnapshot>, type: Class<T>): List<T> =
    suspendCancellableCoroutine { continuation ->
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                try {
                    val data: List<T> = task.result?.toObjects(type).orEmpty()
                    continuation.resume(data)
                } catch (exception: Exception) {
                    continuation.resumeWithException(exception)
                }
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Failed to read task list: $task of type: $type"))
            }
        }
    }

suspend fun <T> Task<QuerySnapshot>.awaitGet(type: Class<T>): List<T> = me.arthurnagy.kotlincoroutines.firestore.awaitTaskQueryList(this, type)

suspend inline fun <reified T> Task<QuerySnapshot>.awaitGet(): List<T> = this.awaitGet(T::class.java)

suspend inline fun <reified T> Task<QuerySnapshot>.awaitGetResult(): me.arthurnagy.kotlincoroutines.firebasecore.Result<List<T>> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitGet<T>() }