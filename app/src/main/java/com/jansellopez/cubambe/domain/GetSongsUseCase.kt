package com.jansellopez.cubambe.domain

import android.net.ConnectivityManager
import com.jansellopez.cubambe.data.SongsRepository
import com.jansellopez.cubambe.data.model.Song
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val songsRepository: SongsRepository
){
    suspend operator fun invoke():List<Song>  = songsRepository.getCategoriesFromApi()
}