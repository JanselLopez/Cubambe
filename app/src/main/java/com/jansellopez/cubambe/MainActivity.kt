package com.jansellopez.cubambe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jansellopez.cubambe.core.CheckConnect
import com.jansellopez.cubambe.ui.home.SongsViewModel
import com.jansellopez.cubambe.ui.main.CubambeApp
import com.jansellopez.cubambe.ui.theme.CubambeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.SocketTimeoutException

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
                try {
                    songsViewModel.onCreate()
                }catch(timeOut: SocketTimeoutException){
                    Toast.makeText(this, resources.getString(R.string.failed_loading_songs), Toast.LENGTH_SHORT).show()
                }
            else
                Toast.makeText(this, stringResource(id = R.string.check_your_connection), Toast.LENGTH_SHORT).show()
            CubambeTheme {
                CubambeApp(songs)
            }
        }
    }
}