package com.jansellopez.cubambe.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.SingleSongActivity
import com.valentinilk.shimmer.shimmer
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(songs: List<Song>, onRefresh: () -> Unit){
    val pullRefreshState = rememberPullRefreshState(songs.isEmpty(),onRefresh)

    LazyColumn(modifier= Modifier
        .fillMaxWidth()
        .pullRefresh(pullRefreshState)){
        if(songs.isNotEmpty()) {
            item {
                AddSongCard()
            }
            items(songs, key = { it.id }) { song ->
                SongCard(song)
            }
        }else{
            repeat(5){
                 item {
                     SongCard(
                         song = Song(it,"${stringResource(id = R.string.loading)}...","...", "...","",R.drawable.ic_logo_red_without_background ,0f),
                         modifier = Modifier.shimmer(),
                         disablePress= true,
                     )
                 }
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        PullRefreshIndicator(
            refreshing = songs.isEmpty(),
            state = pullRefreshState,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSongCard() {
    val context = LocalContext.current
    var isShowPublishSong by rememberSaveable {
    mutableStateOf(false)}
    val animatedAlpha by animateFloatAsState(targetValue = if(isShowPublishSong) 1f else 0.5f)
    val animatedIconCloseRotation by animateFloatAsState(targetValue = if(isShowPublishSong) 45f else 0f)

    Box(modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()) {
                    Card(onClick = {
                       if(!isShowPublishSong) isShowPublishSong = true
                    },modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp), contentAlignment = Alignment.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(animatedAlpha)) {
                                IconButton(onClick = { isShowPublishSong = !isShowPublishSong }) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "AddSong", modifier = Modifier.rotate(animatedIconCloseRotation))
                                }
                                if(isShowPublishSong){
                                    var title by rememberSaveable { mutableStateOf("") }
                                    var author by rememberSaveable { mutableStateOf("") }
                                    var desc by rememberSaveable { mutableStateOf("") }
                                    var posterUrl by rememberSaveable { mutableStateOf("") }
                                    var songUrl by rememberSaveable { mutableStateOf("") }
                                    val body by remember {
                                        derivedStateOf {
                                            "#${context.resources.getString(R.string.app_name)}\n\n" +
                                                    "${context.resources.getString(R.string.title)}: $title\n" +
                                                    "${context.resources.getString(R.string.author)}: $author\n" +
                                                    "${context.resources.getString(R.string.description)}:\n $desc\n" +
                                                    "${context.resources.getString(R.string.poster_link)}: $posterUrl\n" +
                                                    "${context.resources.getString(R.string.song_link)}: $songUrl"
                                        }
                                    }

                                    OutlinedTextField(value = title, onValueChange = {title = it}, label = { Text(
                                        text = stringResource(id = R.string.title)
                                    )}, maxLines = 1, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = author, onValueChange = {author = it}, label = { Text(
                                        text = stringResource(id = R.string.author)
                                    )}, modifier = Modifier.fillMaxWidth(), maxLines = 1)
                                    OutlinedTextField(value = desc, onValueChange = {desc = it}, label = { Text(
                                        text = stringResource(id = R.string.description)
                                    )}, modifier = Modifier
                                        .height(150.dp)
                                        .fillMaxWidth())
                                    OutlinedTextField(value = posterUrl, onValueChange = {posterUrl = it}, label = { Text(
                                        text = stringResource(id = R.string.poster_link), maxLines = 1
                                    )}, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Uri
                                    ))
                                    OutlinedTextField(value = songUrl, onValueChange = {songUrl = it}, label = { Text(
                                        text = stringResource(id = R.string.song_link), maxLines = 1
                                    )}, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Uri
                                    ))

                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(onClick = {
                                        val uri = Uri.parse("mailto:21jansel@gmail.com?subject=${context.resources.getString(R.string.app_name)}&body=$body")
                                        val i = Intent(Intent.ACTION_SENDTO,uri)
                                        ContextCompat.startActivity(context, i, Bundle())
                                    }, modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                                            Icon(painter = painterResource(id = R.drawable.gmail), contentDescription = "Gmail")
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(text = stringResource(id = R.string.continue_with_gmail))
                                        }
                                    }
                                    TextButton(onClick = {
                                        val phoneNumber = "+5356207780" // número de teléfono en formato internacional
                                        val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(body)}"
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(url)
                                        context.startActivity(intent)
                                    },modifier = Modifier.fillMaxWidth()) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                                            Icon(painter = painterResource(id = R.drawable.whatsapp_svgrepo_com), contentDescription = "Whatsapp")
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(text = stringResource(id = R.string.continue_with_whatsapp))
                                        }
                                    }

                                }else{
                                    Text(text = "Publish New Song")
                                }
                            }
                        }

                    }}
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun SongCard(
    song: Song,
    modifier:Modifier = Modifier,
    disablePress:Boolean = false
) {
    val context = LocalContext.current
    val (id,title, author, desc, songUrl, posterUrl, size) = song

    Box(modifier = modifier
    .fillMaxWidth()
    .height(160.dp)) {
    Card(onClick = {
            if(!disablePress)
                context.startActivity(
                    Intent(context,SingleSongActivity::class.java)
                        .putExtra("id",id)
                        .putExtra("title", title)
                        .putExtra("author", author)
                        .putExtra("desc", desc)
                        .putExtra("posterUrl", posterUrl.toString())
                        .putExtra("songUrl", songUrl)
                        .putExtra("size",size)
                )
                   },modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(modifier = Modifier.fillMaxHeight()){
            Column(modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp)
                .weight(1.5f), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = song.title, fontSize = 24.sp, maxLines = 1,fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.montserrat_bold)), overflow = TextOverflow.Ellipsis)
                Text(text=song.desc, fontSize = 10.sp, overflow = TextOverflow.Ellipsis, maxLines = 3)
                Column{
                    Text(text = song.author, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(0.8f), fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.montserrat_bold)), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "${song.size} MB", modifier = Modifier.alpha(0.6f), fontSize = 12.sp)
                }
            }
            GlideImage(
                model = song.posterUrl,
                contentDescription = "${song.title}-poster",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }
    }
    }

}
