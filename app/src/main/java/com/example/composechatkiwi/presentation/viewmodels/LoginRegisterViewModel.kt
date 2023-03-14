package com.example.composechatkiwi.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.composechatkiwi.Destination
import com.example.composechatkiwi.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginRegisterViewModel() : ViewModel() {

    val db = Firebase.database.reference
    val auth = Firebase.auth

    fun registerUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    val user = FirebaseAuth.getInstance().currentUser
                    writeNewUser(username, email, auth.currentUser?.uid!!)
                } else {
                    Log.w("FirebaseAuth", "createUserWithEmail:failure", task.exception)
                }
            }
    }

    fun writeNewUser(name: String, email: String, uid: String) {
        val dbUser = User(uid, name, email)
        db.child("Users").child(uid).setValue(dbUser)
    }


    fun loginWithUser(email: String, password: String, navHostController: NavHostController) {
        if (email.isNotEmpty() && password.isNotEmpty() && isValidEmail(email)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                checkLoggedInState()
                navHostController.navigate(Destination.Main.route)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null) {
            Log.d("mcici", "not logged in")
        } else {
            Log.d("mcici", "logged in")
        }
    }
}