package com.gondev.giphyfavorites.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import kotlinx.coroutines.launch

class GalleryViewModel @ViewModelInject constructor(
    private val dao: GifDataDao,
    private val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val gifList = LivePagedListBuilder(
        if (savedStateHandle.get<Boolean>("isFavorite") == true)
            dao.findFavoriteGif()
        else
            dao.findGif()
        , 20
    ).build()

    fun onPageSelected(position: Int) {
        currentItem.value = gifList.value?.get(position)
    }

    val currentItem = MutableLiveData<GifDataEntity?>()

    fun onClickFavorite(clickedItem: GifDataEntity) {
        viewModelScope.launch {
            val newItem = clickedItem.copy(favorite = !clickedItem.favorite)
            dao.update(newItem)
            currentItem.value = newItem
        }
    }
}