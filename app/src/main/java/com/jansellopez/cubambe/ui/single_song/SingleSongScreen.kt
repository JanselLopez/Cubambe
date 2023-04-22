package com.jansellopez.cubambe.ui.single_song

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Environment
import android.text.Html
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.SingleSongActivity
import kotlinx.coroutines.*

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomBar(song: Song) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var percentage by rememberSaveable { mutableStateOf(-1L) }
    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
        , horizontalArrangement = Arrangement.SpaceBetween) {
        if(percentage==-1L) {
            Card(
                onClick = { downloadSong(song, context, scope, percentage) { percent ->
                    Log.d("percent", "$percent $percentage")
                    percentage = percent

                } },
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
                CircularProgressIndicator(progress = percentage/100f, strokeWidth = 4.dp)
                Text(text = "$percentage%", fontSize = 12.sp)
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
                            startY = 250f
                        )
                    )
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = song.author,fontFamily = FontFamily(
                        Font(R.font.montserrat_bold)))
                    Text(text = stringResource(id = R.string.author), fontSize = 12.sp)
                }
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = "${song.size} MB",fontFamily = FontFamily(
                        Font(R.font.montserrat_bold)))
                    Text(text = stringResource(id =R.string.size), fontSize = 12.sp)
                }
            }

        }

    }
}

fun downloadSong(song: Song, context: Context, scope: CoroutineScope,percentage: Long, onChangePercent:(percent:Long)->Unit){
    val request = DownloadManager.Request(Uri.parse(song.songUrl))
        .setTitle(song.title) // Establece el título de la descarga
        .setDescription(song.desc) // Establece la descripción de la descarga
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Establece la visibilidad de la notificación
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${context.resources.getString(R.string.app_name)}_${song.author}_${song.title}.mp3") // Establece la ruta y el nombre del archivo descargado
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)
    //porciento de descarga

    val delayMillis = 400
    scope.launch {
        if(percentage>=100){
            this.cancel()
        }
        while(percentage<100) { // Repite el código 100 veces
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
            }
            cursor.close()
            delay(delayMillis.toLong()) // Espera 5 segundos antes de repetir
        }
    }

}

