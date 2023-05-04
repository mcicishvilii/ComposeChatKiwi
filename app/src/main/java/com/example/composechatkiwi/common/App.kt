package com.example.composechatkiwi.common

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.composechatkiwi.R
import com.example.composechatkiwi.presentation.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "$token gaumarjoooooooooooooos")
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })

    }
}