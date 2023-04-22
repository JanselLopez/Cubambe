package com.jansellopez.cubambe.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.ui.SingleSongActivity
import com.valentinilk.shimmer.shimmer

@Composable
fun HomeScreen(songs:List<Song>){
    val context = LocalContext.current
    LazyColumn(modifier=Modifier.fillMaxWidth()){
        if(songs.isNotEmpty()) {
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
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun SongCard(
    song: Song,
    modifier:Modifier = Modifier,
    disablePress:Boolean = false
) {
    val context = LocalContext.current
    Box(modifier = modifier
        .fillMaxWidth()
        .height(160.dp)) {
        Card(onClick = {
                if(!disablePress)
                    context.startActivity(
                        Intent(context,SingleSongActivity::class.java)
                            .putExtra("title", song.title)
                            .putExtra("author", song.author)
                            .putExtra("desc", song.desc)
                            .putExtra("posterUrl", song.posterUrl.toString())
                            .putExtra("songUrl", song.songUrl)
                            .putExtra("size",song.size)
                    )
                       },modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxHeight()){
                Spacer(modifier = Modifier.weight(1f))
                GlideImage(
                    model = song.posterUrl,
                    contentDescription = "${song.title}-poster",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = song.title, fontSize = 34.sp, maxLines = 1,fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.montserrat_bold)))
                Column{
                Text(text = song.author, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(0.8f), fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.montserrat_bold)))
                Text(text = "${song.size} MB", modifier = Modifier.alpha(0.6f), fontSize = 12.sp)
                }
            }

        }
    }
}
