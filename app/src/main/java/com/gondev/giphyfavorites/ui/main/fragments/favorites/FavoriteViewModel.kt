package com.gondev.giphyfavorites.ui.main.fragments.favorites

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI

class FavoriteViewModel@ViewModelInject constructor(
    val dao: GifDataDao,
    val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val gifList: LiveData<State<List<GifDataEntity>>> = liveData {

    }
}