package com.gondev.giphyfavorites.ui.main.fragments.giflist

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.network.Result
import com.gondev.giphyfavorites.model.network.api.GipyAPI

class GifListViewModel @ViewModelInject constructor(
    val dao: GifDataDao,
    val gipyAPI: GipyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel()  {
    init {
        savedStateHandle.keys().forEach { key ->
            Log.d("TAG", "Received [$key]=[${savedStateHandle.get<Any>(key)}]")
        }
    }

    val gifList= liveData {
        try {
            emit(Result.success(gipyAPI.getMovieList()))
        } catch (e: Exception) {
            emit(Result.error(e,null))
        }
    }
}