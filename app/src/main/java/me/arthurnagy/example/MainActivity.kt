package me.arthurnagy.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = setBindingContentView<MainBinding>(R.layout.activity_main)
        binding.setLifecycleOwner(this)

        viewModel = provideViewModel()
        binding.viewModel = viewModel

        val usersAdapter = UsersAdapter()
        binding.usersRecycler.apply {
            adapter = usersAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val signInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
        binding.signIn.setOnClickListener {
            startActivityForResult(signInClient.signInIntent, RC_SIGN_IN)
        }

        viewModel.loggedInUser.observe(this) { user ->
            if (user != null) {
                viewModel.getUsers()
            }
        }
        viewModel.users.observe(this) {
            usersAdapter.submitList(it)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                viewModel.signIn(GoogleAuthProvider.getCredential(account.idToken, null))
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 42
    }

}
