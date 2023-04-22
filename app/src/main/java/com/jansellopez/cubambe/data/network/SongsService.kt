package com.jansellopez.cubambe.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongsService @Inject constructor(
    private val apiClient: SongsApiClient
){
    suspend fun getSongs(): List<SongNetwork> {
       return withContext(Dispatchers.IO) {
            apiClient.getSongs().body()?.songs?: emptyList()
        }
    }
}
