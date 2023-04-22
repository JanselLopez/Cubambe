package com.jansellopez.cubambe.data

import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.data.model.toDomain
import com.jansellopez.cubambe.data.network.SongsService
import javax.inject.Inject

class SongsRepository @Inject constructor(
    private val songsService: SongsService
) {
    suspend fun getCategoriesFromApi(): List<Song> = songsService.getSongs().map { it.toDomain() }
}