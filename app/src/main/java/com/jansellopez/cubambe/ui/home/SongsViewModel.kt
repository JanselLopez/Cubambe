package com.jansellopez.cubambe.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jansellopez.cubambe.data.model.Song
import com.jansellopez.cubambe.domain.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {
    val songs = MutableLiveData<List<Song>>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
        onCreate()
    }

    fun onCreate(){
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            songs.postValue(getSongsUseCase())
        }
    }

    fun clear(){
        viewModelScope.launch{
            songs.postValue(emptyList())
        }
    }
}