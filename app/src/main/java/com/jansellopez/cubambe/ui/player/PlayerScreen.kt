package com.jansellopez.cubambe.ui.player

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jansellopez.cubambe.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jansellopez.cubambe.core.PlayerManager
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(){
    val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val context = LocalContext.current
    var songs by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }
    val scope = rememberCoroutineScope()
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            songs = getDownloadsSongs(context)
        } else {
            Toast.makeText(context, context.resources.getString(R.string.grant_the_permissions), Toast.LENGTH_SHORT).show()
        }
    }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        songs = getDownloadsSongs(context)
    } else {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    val mediaPlayer = PlayerManager.mediaPlayer
    var indexPlaying by remember {
        mutableStateOf(PlayerManager.indexPlaying)
    }

    var progress by rememberSaveable {
        mutableStateOf(0f)
    }
    LaunchedEffect(indexPlaying){
        PlayerManager.indexPlaying = indexPlaying
    }
    LaunchedEffect(indexPlaying!=-1 ){
        scope.launch {
            do {
                Log.d("percentage", progress.toString())
                // Obtener la posición actual de reproducción
                val currentPosition = mediaPlayer.currentPosition ?:0f

                // Obtener la duración total de la canción
                val totalDuration = mediaPlayer.duration ?:0f

                // Calcular el porcentaje de la canción que ha transcurrido
                progress = (currentPosition.toFloat() / totalDuration.toFloat())
                delay(500)
            }while (progress!=1f && totalDuration.toFloat()!=0f)
        }
    }
    if(songs.isNotEmpty()) {
        LazyColumn {
            items(songs, key = { it }) {
                SongPlayerCard(
                    it.split("-")[0],
                    {
                        try {
                            indexPlaying = songs.indexOf(it)
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource("$downloadsFolder/$it")
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.failure_to_play_song),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    {
                        indexPlaying = -1
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                    },
                    indexPlaying == songs.indexOf(it),
                    if (indexPlaying == songs.indexOf(it)) progress else 0f
                )
            }
        }
    }else{
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(stringResource(id = R.string.download_some_song), textAlign = TextAlign.Center, modifier = Modifier
            .alpha(0.4f))
        }
    }
}

private fun getDownloadsSongs(context: Context): MutableList<String> {
    val songs = mutableListOf<String>()
    val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (downloadsFolder != null && downloadsFolder.exists() && downloadsFolder.isDirectory) {
        val files = downloadsFolder.listFiles()

        if (files != null) {
            for (file in files) {
                val fileName = file.name
                val extension = fileName.substringAfterLast(".", "")
                if (fileName.startsWith(context.resources.getString(R.string.app_name)) && extension == "mp3") {
                    songs.add(fileName)
                }
            }
        }
    }
    return songs
}

@Composable
fun SongPlayerCard(song:String, onPlayPress : () -> Unit, onStopPress: ()->Unit, isPlaying:Boolean, progress:Float){
    var isPlayed by rememberSaveable { mutableStateOf(isPlaying) }
    isPlayed = isPlaying
    var currentProgress by rememberSaveable { mutableStateOf(progress) }
    currentProgress = progress
    val songWithoutExt = song.substringBefore(".")
    val input = songWithoutExt.split("_")
    val author = input[1]
    val title = input[2]
    Box(modifier = Modifier
        .fillMaxWidth()
        ) {

        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = title, fontFamily = FontFamily(Font(R.font.montserrat_bold)), color = if(isPlayed) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground)
                    Text(text = author, fontSize = 12.sp)
                }
                IconButton(onClick = {
                    isPlayed = !isPlayed
                    if(isPlayed) onPlayPress() else onStopPress()
                }, modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                        Icon(
                            imageVector = if(isPlayed) Icons.Outlined.Close else Icons.Outlined.PlayArrow, contentDescription = stringResource(
                                id = R.string.play
                            ), tint = if(isPlayed) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
                        )
                }
            }
            LinearProgressIndicator(
                progress = currentProgress.let { if(it.isNaN()) 0f else it },
                backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
