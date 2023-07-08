package com.jansellopez.cubambe.ui.player

import android.Manifest
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jansellopez.cubambe.core.PlayerManager
import com.jansellopez.cubambe.core.deleteSong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PlayerScreen() {
    val context = LocalContext.current
    val downloadsFolder =
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    var songs = remember{
        mutableStateListOf<String>()
    }
    val scope = rememberCoroutineScope()
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    if(!(permissionState.status.isGranted)){
            Toast.makeText(
                context,
                context.resources.getString(R.string.grant_the_permissions),
                Toast.LENGTH_SHORT
            ).show()
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
    }
    LaunchedEffect(Unit) {
        if(permissionState.status.isGranted) songs.addAll(getDownloadsSongs(context))
    }
    val mediaPlayer = PlayerManager.mediaPlayer
    var indexPlaying by remember {
        mutableStateOf(PlayerManager.indexPlaying)
    }
    var fileNameToDelete by rememberSaveable {
        mutableStateOf("")
    }
    var isShowDeleteDialog by remember {
        mutableStateOf(false)
    }
    val animateAlertDialogScale by animateFloatAsState(targetValue = if(isShowDeleteDialog)1f else 0f,
        animationSpec = if(isShowDeleteDialog)spring(dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow)else tween(durationMillis = 150, easing = FastOutLinearInEasing))

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
            LazyColumn(modifier = Modifier.animateContentSize()) {
            items(songs, key = { it }) { fileName ->
                SongPlayerCard(
                    fileName.split("-")[0],
                    {
                        try {
                            indexPlaying = songs.indexOf(fileName)
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource("$downloadsFolder/$fileName")
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
                    onPausePress ={
                               if(mediaPlayer.isPlaying) {
                                   mediaPlayer.pause()
                               }else{
                                   mediaPlayer.start()
                               }
                    },
                    onDeletePress={
                        isShowDeleteDialog = true
                        fileNameToDelete =fileName
                    },
                    indexPlaying == songs.indexOf(fileName),
                    if (indexPlaying == songs.indexOf(fileName)) progress else 0f,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        AnimatedVisibility(visible = isShowDeleteDialog) {
            AlertDialog(modifier=Modifier.scale(animateAlertDialogScale),onDismissRequest = {
                isShowDeleteDialog = false
                fileNameToDelete =""
            },
                title = {
                    Text(
                        text = stringResource(id = R.string.warning),
                        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                        fontSize = 20.sp
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.are_you_sure_you_want_to_delete_this_song),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            indexPlaying = -1
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                            deleteSong(context,fileNameToDelete)
                            songs.remove(fileNameToDelete)
                            isShowDeleteDialog = false
                            fileNameToDelete =""
                        }) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }, dismissButton = {
                    TextButton(
                        onClick = {
                            isShowDeleteDialog = false
                            fileNameToDelete =""
                        }) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                })
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
    val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

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

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun SongPlayerCard(
    song: String,
    onPlayPress: () -> Unit,
    onStopPress: () -> Unit,
    onPausePress: () -> Unit,
    onDeletePress:() -> Unit,
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
){
    var isPlayed by rememberSaveable { mutableStateOf(isPlaying) }
    isPlayed = isPlaying
    var currentProgress by rememberSaveable { mutableStateOf(progress) }
    currentProgress = progress
    val animatedProgress by animateFloatAsState(targetValue = currentProgress)
    val songWithoutExt = song.substringBefore(".")
    val input = songWithoutExt.split("_")
    val author = input[1]
    val title = input[2]
    var isPaused by rememberSaveable{ mutableStateOf(false) }

    val animatedTintColor by animateColorAsState(targetValue = if(isPlayed) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground)

    LaunchedEffect(PlayerManager.mediaPlayer.isPlaying){
        isPaused = !(PlayerManager.mediaPlayer.isPlaying)
    }

    var isShowOptions by rememberSaveable{ mutableStateOf(false) }

    Card(onClick={isShowOptions=!isShowOptions}, elevation = 0.dp,
        modifier = modifier
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
                        Text(text = title, fontFamily = FontFamily(Font(R.font.montserrat_bold)), maxLines = 1, overflow = TextOverflow.Ellipsis, color = animatedTintColor)
                        Text(text = author, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Row(modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )) {
                        if(isShowOptions){
                            IconButton(onClick = { isShowOptions=false},modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(
                                    id = R.string.back
                                ), tint = MaterialTheme.colors.onBackground)
                            }
                        }
                        if(!isShowOptions){
                            IconButton(onClick = {
                                isPlayed = !isPlayed
                                if(isPlayed) onPlayPress() else onStopPress()
                            }, modifier = Modifier.align(alignment = Alignment.CenterVertically)) {
                                Icon(
                                    imageVector = if(isPlayed) Icons.Outlined.Close else Icons.Outlined.PlayArrow, contentDescription = stringResource(
                                        id = R.string.play
                                    ), tint = animatedTintColor
                                )
                            }
                            if(isPlayed){
                                IconButton(onClick = {
                                    onPausePress()
                                }) {
                                    Icon(
                                        if(isPaused) painterResource(id = R.drawable.baseline_play_arrow_24)  else painterResource(id = R.drawable.baseline_pause_24),
                                        contentDescription = stringResource(
                                            id =R.string.pause
                                        ),
                                        tint=MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = isShowOptions) {
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
                            TextButton(onClick = onDeletePress) {
                                Text(text = stringResource(id = R.string.delete), color = MaterialTheme.colors.primary, fontFamily = FontFamily(Font(R.font.montserrat_bold)))
                            }
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = animatedProgress.let { if(it.isNaN()) 0f else it },
                    backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )

            }
    }
}
