package me.arthurnagy.kotlincoroutines

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
                    val data: List<T> = task.result.toObjects(type)
                    continuation.resume(data)
                } catch (exception: Exception) {
                    continuation.resumeWithException(exception)
                }
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Failed to read task list: $task of type: $type"))
            }
        }
    }

suspend fun <T> Task<QuerySnapshot>.awaitGet(type: Class<T>): List<T> = awaitTaskQueryList(this, type)

suspend inline fun <reified T> Task<QuerySnapshot>.awaitGet(): List<T> = this.awaitGet(T::class.java)

suspend inline fun <reified T> Task<QuerySnapshot>.awaitGetResult(): Result<List<T>> =
    wrapIntoResult { this.awaitGet<T>() }