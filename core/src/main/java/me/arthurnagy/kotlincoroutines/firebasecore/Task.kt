package me.arthurnagy.kotlincoroutines.firebasecore

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Coroutine support to Firebase Task interface
 *
 * This extension function allows you to interact with a Firebase [com.google.android.gms.tasks.Task]
 * using the `await()` method instead of the standard listeners.
 *
 * There is a sample code below comparing the two approaches. Assuming that
 * `auth` variable has the value returned from `FirebaseAuth.getInstance()`
 * method call then your code can be something like:
 *
 * ```
 * auth.getUserByEmail(email)
 *   .addOnSuccessListener { user -> println(user) }
 *   .addOnFailureListener { exception -> println(exception) }
 * ```
 *
 * When using the coroutine approach, it should be more like this:
 *
 * ```
 * try {
 *   val user = auth.getUserByEmail(email).await()
 *   println(user)
 * } catch (exception: Exception) {
 *   println(exception)
 * }
 * ```
 *
 * @param T The type of the value been returned
 * @throws Exception Thrown in case of network error or other reasons described in the Firebase docs
 * @return The value returned by the Firebase success callback
 */
suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    this.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            task.result?.let {
                continuation.resume(it)
            } ?: continuation.resumeWithException(Exception("Firebase Task returned null"))
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Firebase Task failed to execute"))
        }
    }
}

suspend inline fun <T> Task<T>.awaitResult(): Result<T> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.await() }