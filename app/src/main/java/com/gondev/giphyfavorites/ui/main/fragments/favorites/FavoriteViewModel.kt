package com.gondev.giphyfavorites.ui.main.fragments.favorites

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI

class FavoriteViewModel@ViewModelInject constructor(
    val dao: GifDataDao,
    val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val state: MutableLiveData<State> = MutableLiveData(State.loading())
    val gifList: LiveData<PagedList<GifDataEntity>> = liveData {

    }
}