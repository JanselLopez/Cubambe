package com.jansellopez.cubambe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jansellopez.cubambe.core.CheckConnect
import com.jansellopez.cubambe.ui.home.SongsViewModel
import com.jansellopez.cubambe.ui.main.CubambeApp
import com.jansellopez.cubambe.ui.theme.CubambeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val songsViewModel:SongsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        screenSplash.setKeepOnScreenCondition { false }
        setContent {
            val songs by songsViewModel.songs.observeAsState(emptyList())

            if(CheckConnect(this))
                songsViewModel.onCreate()
            else
                Toast.makeText(this, stringResource(id = R.string.check_your_connection), Toast.LENGTH_SHORT).show()

            CubambeTheme {
                CubambeApp(songs =songs) {
                    if (CheckConnect(this)) {
                        songsViewModel.clear()
                        songsViewModel.onCreate()
                    }else
                        Toast.makeText(
                            this,
                            resources.getString(R.string.check_your_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }
//    fun createChannel(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_ID,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//    fun launchNotification(){
//        val builder = NotificationCompat.Builder(this, "$CHANNEL_ID")
//            .setContentTitle("Cubambe")
//            .setContentText("noti noti")
//            .setSmallIcon(R.drawable.ic_logo_red_without_background)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        with(NotificationManagerCompat.from(this)){
//            notify(1,builder.build())
//        }
//    }
}