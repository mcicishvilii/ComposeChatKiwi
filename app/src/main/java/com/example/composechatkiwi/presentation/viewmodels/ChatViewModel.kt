package com.example.composechatkiwi.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.composechatkiwi.Destination
import com.example.composechatkiwi.data.Messages
import com.example.composechatkiwi.data.User
import com.example.composekiwi.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ChatViewModel() : ViewModel() {

    val db = Firebase.database.reference
    val auth = Firebase.auth

    val listofUsers = mutableListOf<User>()
    val listOfMessages = mutableListOf<Messages>()

    val senderUid = FirebaseAuth.getInstance().currentUser?.uid

    private var _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private var _messages = MutableStateFlow<List<Messages>>(emptyList())
    val message = _messages.asStateFlow()


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
        viewModelScope.launch {
            db.child("Chats").child(senderRoom).child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        listOfMessages.clear()
                        for (ds in dataSnapshot.children) {
                            val message: Messages? = ds.getValue(Messages::class.java)
                            listOfMessages.add(message!!)
                        }
                        val imutableList = listOfMessages.toList()
                        _messages.value = imutableList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG, "Failed to read value.", error.toException())
                    }
                })
        }
    }

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
}