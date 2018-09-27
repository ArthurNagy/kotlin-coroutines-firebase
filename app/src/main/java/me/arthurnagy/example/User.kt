package me.arthurnagy.example

import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseUser

@Keep
class User() {

    var id: String = ""
    var displayName: String? = ""
    var email: String? = ""
    var profilePictureUrl: String = ""

    constructor(id: String, displayName: String?, email: String?, profilePictureUrl: String) : this() {
        this.id = id
        this.displayName = displayName
        this.email = email
        this.profilePictureUrl = profilePictureUrl
    }


    companion object {
        const val REFERENCE = "users"
        fun create(firebaseUser: FirebaseUser) = User(
            id = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            profilePictureUrl = firebaseUser.photoUrl.toString()
        )
    }

}