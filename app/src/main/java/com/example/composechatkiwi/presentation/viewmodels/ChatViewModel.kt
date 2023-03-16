package com.example.composechatkiwi.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatkiwi.data.Messages
import com.example.composechatkiwi.data.User
import com.example.composechatkiwi.presentation.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ChatViewModel() : ViewModel() {

    val db = Firebase.database.reference
    val auth = Firebase.auth

    val listofUsers = mutableListOf<User>()
    val listOfMessages = mutableListOf<Messages>()

    val senderUid = FirebaseAuth.getInstance().currentUser?.uid

    private var _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var _messagesForSearching = MutableStateFlow(listOf<Messages>())
    val messagesForSearching = searchText
        .combine(_messagesForSearching){text, messages ->
            if(text.isBlank()){
                messages
            }else{
                messages.filter{
                    it.doesTextMatch(text)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _messagesForSearching.value
        )


    fun onTextChange(text:String){
        _searchText.value = text
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
                        _messagesForSearching.value = imutableList
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