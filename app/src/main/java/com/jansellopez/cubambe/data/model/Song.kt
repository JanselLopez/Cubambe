package com.jansellopez.cubambe.data.model

import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.network.SongNetwork

data class Song(
    val id:Int,
    val title:String,
    val author:String,
    val desc:String,
    val songUrl:String,
    val posterUrl:Any,
    val size:Float
)

fun SongNetwork.toDomain() = Song(id,title?:"Loading...",author?:"...",desc?:"...",song?:"...",poster?: R.drawable.logo_red_without_background,size?:0f)