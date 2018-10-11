package me.arthurnagy.kotlincoroutines.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//region GET
/**
 * @param document
 * @param type
 * @param source
 * @return
 */
private suspend fun <T> awaitDocumentValue(document: DocumentReference, type: Class<T>, source: Source = Source.DEFAULT): T =
    suspendCancellableCoroutine { continuation ->
        document.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                try {
                    val data: T? = task.result?.toObject(type)
                    data?.let { continuation.resume(it) } ?: continuation.resumeWithException(Exception("Failed to get document from $document of type: $type"))
                } catch (exception: Exception) {
                    continuation.resumeWithException(exception)
                }
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Failed to get document from $document of type: $type"))
            }
        }
    }

/**
 * @param type
 * @param source
 * @return
 * @receiver
 */
suspend fun <T> DocumentReference.awaitGet(type: Class<T>, source: Source = Source.DEFAULT): T =
    me.arthurnagy.kotlincoroutines.firestore.awaitDocumentValue(this, type, source)

/**
 * @param source
 * @return
 * @receiver
 */
suspend inline fun <reified T> DocumentReference.awaitGet(source: Source = Source.DEFAULT): T = this.awaitGet(T::class.java, source)


suspend inline fun <reified T> DocumentReference.awaitGetResult(source: Source = Source.DEFAULT): me.arthurnagy.kotlincoroutines.firebasecore.Result<T> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitGet<T>(source) }
//endregion

//region SET
/**
 *
 */
suspend inline fun <T : Any> DocumentReference.awaitSet(data: T, setOptions: SetOptions? = null): Unit = suspendCoroutine { continuation ->
    (if (setOptions == null) this.set(data) else this.set(data, setOptions)).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to set $data to document $this"))
        }
    }
}

suspend inline fun <T : Any> DocumentReference.awaitSetResult(
    data: T,
    setOptions: SetOptions? = null
): me.arthurnagy.kotlincoroutines.firebasecore.Result<Unit> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitSet(data, setOptions) }

/**
 *
 */
suspend inline fun DocumentReference.awaitSetMap(data: Map<String, Any>, setOptions: SetOptions? = null): Unit = this.awaitSet(data, setOptions)

suspend inline fun DocumentReference.awaitSetMapResult(
    data: Map<String, Any>,
    setOptions: SetOptions? = null
): me.arthurnagy.kotlincoroutines.firebasecore.Result<Unit> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitSetMap(data, setOptions) }
//endregion

//region UPDATE
/**
 *
 */
suspend inline fun DocumentReference.awaitUpdate(data: Map<String, Any>): Unit = suspendCoroutine { continuation ->
    this.update(data).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to update document $this with value $data"))
        }
    }
}

suspend inline fun DocumentReference.awaitUpdateResult(data: Map<String, Any>): me.arthurnagy.kotlincoroutines.firebasecore.Result<Unit> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitUpdate(data) }

/**
 *
 */
suspend fun DocumentReference.awaitUpdate(vararg fields: Pair<String, Any>): Unit = this.awaitUpdate(fields.toMap())

suspend fun DocumentReference.awaitUpdateResult(vararg fields: Pair<String, Any>): me.arthurnagy.kotlincoroutines.firebasecore.Result<Unit> =
    this.awaitUpdateResult(fields.toMap())
//endregion

//region DELETE
/**
 *
 */
suspend inline fun DocumentReference.awaitDelete(): Unit = suspendCoroutine { continuation ->
    this.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Failed to delete document $this"))
        }
    }
}

suspend inline fun DocumentReference.awaitDeleteResult(): me.arthurnagy.kotlincoroutines.firebasecore.Result<Unit> =
    me.arthurnagy.kotlincoroutines.firebasecore.wrapIntoResult { this.awaitDelete() }
//endregion