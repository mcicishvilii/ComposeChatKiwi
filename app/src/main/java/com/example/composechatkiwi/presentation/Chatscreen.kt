package com.example.composekiwi

import android.annotation.SuppressLint
import android.os.Message
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composechatkiwi.Destination
import com.example.composechatkiwi.data.Messages
import com.example.composechatkiwi.presentation.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(userId: String, email: String, vm: ChatViewModel = viewModel()) {

    var senderRoom: String? = null
    var receiverRoom: String? = null

    val senderUid = FirebaseAuth.getInstance().currentUser?.uid

    senderRoom = userId + senderUid
    receiverRoom = senderUid + userId

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text(text = email) })
        },
        bottomBar = {
            SendMessageBar(senderRoom = senderRoom, receiverRoom = receiverRoom)
        }
    ) {
        ChatMessages(userId = userId, senderRoom = senderRoom)
        it
    }
}

@Composable
fun ChatMessages(
    vm: ChatViewModel = viewModel(),
    userId: String? = null,
    senderRoom: String,

    ) {
    val messages = vm.message.collectAsState()
    val cs: CoroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentPadding = PaddingValues(8.dp),
    ) {


        cs.launch {
            vm.getMessages(senderRoom)
        }

        items(messages.value) { message ->
            ChatBubble(
                message = message,
                userId = userId!!
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ChatBubble(message: Messages, userId: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Green),
        horizontalArrangement =
        if (message.id == userId) {
            Arrangement.Start
        } else Arrangement.End
    ) {
        Card(
            modifier = Modifier.padding(1.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SendMessageBar(
    vm: ChatViewModel = viewModel(),
    senderRoom: String,
    receiverRoom: String
) {

    var textValue by remember { mutableStateOf(TextFieldValue("")) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text(text = "Type your message...") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                vm.sendMessage(textValue.text, senderRoom, receiverRoom)
                textValue = TextFieldValue("")
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Send Message"
            )
        }
    }
}

