package com.jansellopez.cubambe.ui.player

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel : ViewModel() {
    val mediaPlayer = MutableLiveData<MediaPlayer>()
    init {
        mediaPlayer.postValue(MediaPlayer())
    }
}