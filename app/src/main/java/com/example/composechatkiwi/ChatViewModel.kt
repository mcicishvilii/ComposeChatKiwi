package com.example.composekiwi

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.composechatkiwi.Destination
import com.example.kiwichatcompose.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatViewModel() : ViewModel() {

    val db = Firebase.database.reference
    val auth = Firebase.auth

    val listofUsers = mutableListOf<User>()
    val listOfMessages = mutableListOf<Messages>()

    val senderUid = FirebaseAuth.getInstance().currentUser?.uid


    private var _receiverUid = mutableStateOf<String>("")
    val receiverUid: State<String> = _receiverUid

    private var _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private var _messages = mutableStateOf<List<Messages>>(emptyList())
    val messages: State<List<Messages>> = _messages


    fun sendMessage(messageText: String, senderRoom: String, receiverRoom: String) {
        val message = Messages(senderUid!!, messageText)

        if (messageText.isNotEmpty()) {
            db.child("Chats").child(senderRoom).child("messages").push()
                .setValue(message).addOnSuccessListener {
                    db.child("Chats").child(receiverRoom).child("messages").push()
                        .setValue(message)
                }

        } else {

        }
    }

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

    init {
        getUsers()

    }

    private fun getUsers() {
        db.child("Users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listofUsers.clear()
                    for (ds in dataSnapshot.children) {
                        val user: User? = ds.getValue(User::class.java)

                        if (auth.currentUser?.uid != user?.uid) {
                            listofUsers.add(user!!)
                            _users.value = listofUsers
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("mcicishvilii", "Failed to read value.", error.toException())
                }
            }
        )
    }

    fun getMessages(senderRoom: String) {
        db.child("Chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    listOfMessages.clear()
                    for (ds in dataSnapshot.children) {
                        val message: Messages? = ds.getValue(Messages::class.java)
                        listOfMessages.add(message!!)
                        _messages.value = listOfMessages
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            })
    }
}