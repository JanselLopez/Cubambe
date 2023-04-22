package com.jansellopez.cubambe.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.domain.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {
    val songs = MutableLiveData<List<Song>>()

    fun onCreate(){
        viewModelScope.launch {
            songs.postValue(getSongsUseCase())
        }
    }
}