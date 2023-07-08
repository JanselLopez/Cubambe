package com.jansellopez.cubambe.ui.single_song

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.core.DownloadIdManager
import com.jansellopez.cubambe.core.deleteSong
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.SingleSongActivity
import kotlinx.coroutines.*
import java.io.File


@Composable
fun SingleSongScreen(song: Song){
    val configuration = LocalConfiguration.current

    Scaffold(
        topBar = {
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                SimpleCoverBar(song)
            else
                CoverBar(song)
        },
        bottomBar = {
            BottomBar(song)
        }
    ) {innerPadding->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            item {
                Text(text = song.title, fontSize = 34.sp,fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                    Font(R.font.montserrat_bold)
                ), modifier = Modifier.padding(top = 20.dp, bottom = 0.dp, start = 20.dp, end = 20.dp)
                )
            }
            item{
                Text(text = song.desc, modifier = Modifier.padding(20.dp), textAlign = TextAlign.Justify)
            }
        }
    }
}

@OptIn( ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun BottomBar(song: Song) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var percentage by rememberSaveable { mutableStateOf(-1L) }
    val animatedPercentage by animateFloatAsState(targetValue = percentage/100f)
    var downloadId:Long? by rememberSaveable { mutableStateOf(DownloadIdManager.downloadIdsMap[song.id]) }
    LaunchedEffect(downloadId){
        DownloadIdManager.downloadIdsMap[song.id] = downloadId
        if(downloadId!=null) {
            setPercentage(context, scope, percentage, downloadId) { percent ->
                if(percent == 100L){
                    downloadId = null
                }
                percentage = percent
            }
        }
    }
    var showDialog by rememberSaveable{ mutableStateOf(false) }
    val animateAlertDialogScale by animateFloatAsState(targetValue = if(showDialog)1f else 0f,
        animationSpec = if(showDialog)spring(dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow)else tween(durationMillis = 150, easing = FastOutLinearInEasing)
    )
    if(showDialog) OverrideAlertDialog(onConfirm = {
        deleteSong(context,song)
        CoroutineScope(Dispatchers.IO).launch {
            downloadSong(song, context){newId->
                downloadId = newId
            }
        }
        showDialog = false
    }, onDismiss={
        showDialog = false
    },modifier=Modifier.scale(animateAlertDialogScale))
    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
        , horizontalArrangement = Arrangement.SpaceBetween) {
        if(percentage==-1L || percentage == 100L) {
            Card(
                onClick = {
                    if(permissionState.status.isGranted){
                        if(isDownloaded(context, song)){
                            showDialog = true
                        }else{
                            CoroutineScope(Dispatchers.IO).launch {
                                downloadSong(song, context){newId->
                                    downloadId = newId
                                }
                            }
                        }
                    }else{
                        permissionState.launchPermissionRequest()
                    }
                },
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.background,
                shape = CircleShape
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.download),
                        tint = MaterialTheme.colors.primary
                    )
                    Text(
                        text = stringResource(id = R.string.download),
                        color = MaterialTheme.colors.primary,
                        fontFamily = FontFamily(
                            Font(R.font.montserrat_bold)
                        )
                    )
                }
            }
        }else{
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = animatedPercentage, strokeWidth = 4.dp)
                Text(text = "${(animatedPercentage*100).toInt()}%", fontSize = 12.sp)
            }
        }
        IconButton(onClick = {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, "${song.title} - ${song.author}\n\n${song.desc}\n"+
                            "\n${context.resources.getString(R.string.download_the_app_to_listen_to_the_song)}: " +
                                    "https://www.apklis.cu/application/com.jansellopez.cubambe")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, context.resources.getString(R.string.share))
            context.startActivity(shareIntent)
        }) {
            Icon(imageVector = Icons.Filled.Share, contentDescription = stringResource(id = R.string.download), tint = MaterialTheme.colors.primary  )
        }
    }
}

@Composable
fun OverrideAlertDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, modifier: Modifier= Modifier) {

val context = LocalContext.current
    AlertDialog(modifier=modifier,
        onDismissRequest = onDismiss,
        title = {
                Text(text = stringResource(id = R.string.warning), fontFamily = FontFamily(Font(R.font.montserrat_bold)), fontSize = 20.sp)
        },
        text = {
               Text(text = stringResource(id = R.string.override_song), fontSize = 14.sp)
        },
    confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = stringResource(id = android.R.string.ok), fontSize = 16.sp)
        }
    },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel),fontSize = 16.sp)
            }
        },
        )
}


fun isDownloaded(context: Context, song: Song): Boolean {
    val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val songFile = File("$downloadsFolder/${context.resources.getString(R.string.app_name)}_${song.author}_${song.title}.mp3")
    return songFile.exists()
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SimpleCoverBar(song: Song){
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { (context as SingleSongActivity).finish() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
        }
        Row(modifier=Modifier.padding(end= 20.dp, top=15.dp),verticalAlignment = Alignment.CenterVertically){
            Text(text = "${song.author}/", fontFamily = FontFamily(Font(R.font.montserrat_bold)), fontSize = 14.sp)
            Text(text= "${song.size} MB", fontSize = 12.sp, modifier= Modifier.padding(end=20.dp))
            Surface(shape = CircleShape, modifier = Modifier.size(40.dp)) {
                GlideImage(
                    model = song.posterUrl,
                    contentDescription = "${song.title}-poster",
                    modifier = Modifier,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CoverBar(song: Song) {
    val context = LocalContext.current
    Column() {
        Box(modifier = Modifier.height(300.dp), contentAlignment = Alignment.BottomCenter) {
            GlideImage(
                model = song.posterUrl,
                contentDescription = "${song.title}-poster",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colors.background,
                                Color.Transparent,

                                ),
                            center = Offset(0f, 0f),
                            radius = 150f,
                            tileMode = TileMode.Clamp,
                        )
                    )
            )
            IconButton(onClick = { (context as SingleSongActivity).finish() }, modifier =  Modifier.align(Alignment.TopStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colors.background
                            ),
                            startY = 350f
                        )
                    )
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),Arrangement.SpaceBetween, Alignment.Bottom) {
                Column(verticalArrangement = Arrangement.Center, modifier= Modifier.weight(3f)) {
                    Text(text = song.author,fontFamily = FontFamily(
                        Font(R.font.montserrat_bold)))
                    Text(text = stringResource(id = R.string.author), fontSize = 12.sp)
                }
                Column(verticalArrangement = Arrangement.Center, modifier= Modifier.weight(1f).padding(start=5.dp)) {
                    Text(text = "${song.size} MB",fontFamily = FontFamily(
                        Font(R.font.montserrat_bold)))
                    Text(text = stringResource(id =R.string.size), fontSize = 12.sp)
                }
            }

        }

    }
}
fun setPercentage( context: Context, scope: CoroutineScope,percentage: Long, downloadId:Long?, onChangePercent:(percent:Long)->Unit){
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    //porciento de descarga
    val delayMillis = 400L
    scope.launch {
        var count = 0
        if(percentage>=100){
            cancel()
        }
        while(percentage<100 && downloadId !=null) {
            val query = DownloadManager.Query().apply {
                setFilterById(downloadId) // Filtra los resultados por ID de descarga
            }
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val downloadedBytes = cursor.getLong(
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR).let {
                        if(it<0)0 else it
                    }
                )
                val totalBytes = cursor.getLong(
                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES).let {
                        if(it<0) 0 else it
                    }
                )
                onChangePercent (downloadedBytes * 100 / totalBytes)
                // Actualiza la interfaz de usuario con el porcentaje de descarga completado
                if(count ==18000 && (downloadedBytes * 100 / totalBytes)==0L){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, context.getString(R.string.check_your_connection_or_disable_the_firewall), Toast.LENGTH_LONG).show()
                    }
                    count=0
                }
            }
            cursor.close()
            delay(delayMillis)
            count+=400
        }
    }
}

fun downloadSong(song: Song, context: Context,onIdChange:(id:Long)->Unit ){
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(song.songUrl))
            .setTitle(song.title) // Establece el título de la descarga
            .setDescription(song.desc) // Establece la descripción de la descarga
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Establece la visibilidad de la notificación
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                "${context.resources.getString(R.string.app_name)}_${song.author}_${song.title}.mp3"
            )
    onIdChange(downloadManager.enqueue(request))
}