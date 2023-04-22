package com.jansellopez.cubambe.core

import android.content.Context
import android.net.ConnectivityManager
import androidx.activity.ComponentActivity

object CheckConnect {
    operator fun invoke(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val actNetInfo = connectivityManager.activeNetworkInfo
        return actNetInfo != null && actNetInfo.isConnectedOrConnecting
    }
}