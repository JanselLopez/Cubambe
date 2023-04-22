package com.jansellopez.cubambe.data.model

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

fun SongNetwork.toDomain() = Song(id,title,author,desc,song,poster,size)