package com.jansellopez.cubambe.core

import android.content.Context
import android.os.Environment
import android.util.Log
import com.jansellopez.cubambe.R
import com.jansellopez.cubambe.data.model.Song
import java.io.File

fun deleteSong(context: Context, song: Any){
    val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val songFile = File(
        if(song is Song)
            "$downloadsFolder/${context.resources.getString(R.string.app_name)}_${song.author}_${song.title}.mp3"
        else
            "$downloadsFolder/$song"
    )
    songFile.delete()
}