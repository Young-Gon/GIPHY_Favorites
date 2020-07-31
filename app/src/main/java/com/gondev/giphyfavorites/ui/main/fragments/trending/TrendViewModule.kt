package com.gondev.giphyfavorites.ui.main.fragments.trending

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class TrendViewModule @ViewModelInject constructor(

    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel()  {
    init {
        savedStateHandle.keys().forEach { key ->
            Log.d("TAG", "Received [$key]=[${savedStateHandle.get<Any>(key)}]")
        }
    }
}