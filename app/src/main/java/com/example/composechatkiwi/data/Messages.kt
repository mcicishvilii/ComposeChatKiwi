package com.example.composechatkiwi.data

import androidx.annotation.DrawableRes

data class Messages(
    val id:String = "",
    val text:String = ""
){
    fun doesTextMatch(query:String):Boolean{
        val matchingStrings = listOf(
            "$text",
            "${text.first()}",
        )
        return matchingStrings.any{
            it.contains(query, ignoreCase = true)
        }
    }
}