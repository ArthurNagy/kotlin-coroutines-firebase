package me.arthurnagy.example

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.arthurnagy.kotlincoroutines.Result
import me.arthurnagy.kotlincoroutines.awaitGetResult
import me.arthurnagy.kotlincoroutines.awaitResult
import me.arthurnagy.kotlincoroutines.awaitSetResult
import kotlin.coroutines.experimental.CoroutineContext

class MainViewModel : ViewModel() {

    private val job = Job()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = firebaseFirestore.collection(User.REFERENCE)
    val loggedInUser = MutableLiveData<User>()
    val isUserLoggedIn = MutableLiveData<Boolean>()
    val users = MutableLiveData<List<User>>()

    init {
        loggedInUser.observeForever { isUserLoggedIn.value = it != null }
    }

    fun signIn(authCredential: AuthCredential) {
        launchWithParent(UI) {
            val firebaseUser: FirebaseUser
            /* Firestore with Kotlin coroutines .awaitX() approach
                In this case .await method can throw an exception, so we need to wrap them in a try catch block
            */
//            try {
//                val authenticatedUser = firebaseAuth.signInWithCredential(authCredential).await()
//                Log.d("MainViewModel", "signIn: successfully authenticated: ${authenticatedUser.user}")
//                firebaseUser = authenticatedUser.user
//                val user = User.create(firebaseUser)
//
//                userCollection.document(user.id).awaitSet(user)
//                Log.d("MainViewModel", "signIn: successfully created: $user")
//                loggedInUser.value = user
//            } catch (exception: Exception) {
//                Log.d("MainViewModel", "signIn: something went wrong: $exception")
//            }

            /* Firestore with Kotlin coroutines .awaitXResult() approach
                Here we get a Result<T> object which wraps the Success or Error of our coroutine call
             */
            val authenticatedUserResult = firebaseAuth.signInWithCredential(authCredential).awaitResult()
            when (authenticatedUserResult) {
                is Result.Success -> {
                    firebaseUser = authenticatedUserResult.value.user
                    val user = User.create(firebaseUser)
                    val userResult = userCollection.document(user.id).awaitSetResult(user)
                    when (userResult) {
                        is Result.Success -> loggedInUser.value = user
                        is Result.Error -> Log.d("MainViewModel", "signIn: create user failed: ${userResult.exception}")
                    }
                }
                is Result.Error -> Log.d("MainViewModel", "signIn: signInWithCredential failed: ${authenticatedUserResult.exception}")
            }
        }
    }

    fun getUsers() {
        launchWithParent(UI) {
            //            try {
//                val userList: List<User> = userCollection.get().awaitGet()
//                users.value = userList
//            } catch (exception: Exception) {
//                Log.d("MainViewModel", "loadUsers: failed to get users: $exception")
//            }
            val usersResult: Result<List<User>> = userCollection.get().awaitGetResult()
            when (usersResult) {
                is Result.Success -> users.value = usersResult.value
                is Result.Error -> Log.d("MainViewModel", "loadUsers: failed to get users: ${usersResult.exception}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun launchWithParent(
        context: CoroutineContext = DefaultDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(context = context, parent = job, block = block)

}