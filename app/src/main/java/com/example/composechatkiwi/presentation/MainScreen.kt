package com.example.composechatkiwi.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.composechatkiwi.Destination
import com.example.composechatkiwi.data.User
import com.example.composechatkiwi.presentation.viewmodels.ChatViewModel
import com.example.composechatkiwi.presentation.viewmodels.LoginRegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

const val TAG = "GESTAPO"

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Users(navHostController: NavHostController? = null, vm: ChatViewModel = viewModel(), vmLogin:LoginRegisterViewModel = viewModel()) {

    val db = Firebase.database.reference
    val auth = Firebase.auth
    val user = auth.currentUser

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0XFF020b1a))) {

        if (user != null) {
            Text(
                text = user.email!!, // aq minda ro meilis nacvlad user name gamovachino
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .padding(start = 26.dp, bottom = 15.dp, top = 15.dp)

            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(vm.users.value) { user ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clickable {
                            db
                                .child("Users")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (ds in dataSnapshot.children) {
                                            val useri: User? = ds.getValue(User::class.java)
                                            try {
                                                if (useri?.email == user.email) {
                                                    navHostController?.navigate("${Destination.Chat.route}/${user.uid}/${user.email}")
                                                }
                                            } catch (e: Exception) {
                                                Log.d(TAG, e.message.toString())
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.d(
                                            TAG,
                                            "Error reading data from database",
                                            error.toException()
                                        )
                                    }
                                })
                            Log.d(TAG, user.uid)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // generate random color for each user
                    val color = Color(kotlin.random.Random.nextLong(until = 0xFFFFFFFF))

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = color, shape = CircleShape)
                    ) {}

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = user.userName,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(color = Color.Gray, thickness = 0.7.dp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            onClick = {
                try {
                    if(user != null){
                        auth.signOut()
                        navHostController?.navigate(Destination.Register.route)
                    }
                }catch (e:Exception){
                    Log.d(TAG,e.message.toString())
                }
            }) {
            Text("log out")
        }
    }
}
