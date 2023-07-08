package com.jansellopez.cubambe.ui

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.single_song.SingleSongScreen
import com.jansellopez.cubambe.ui.single_song.downloadSong
import com.jansellopez.cubambe.ui.theme.CubambeTheme

class SingleSongActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")?:""
        val author = intent.getStringExtra("author")?:""
        val desc = intent.getStringExtra("desc")?:""
        val posterUrl = intent.getStringExtra("posterUrl")?:""
        val songUrl = intent.getStringExtra("songUrl")?:""
        val size = intent.getFloatExtra("size", 0f)

        setContent {
            CubambeTheme {
                SingleSongScreen(
                    Song(id,title,author,desc,songUrl,posterUrl,size)
                )
            }
        }
    }
}