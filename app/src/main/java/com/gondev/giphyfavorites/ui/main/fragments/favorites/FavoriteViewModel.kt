package com.gondev.giphyfavorites.ui.main.fragments.favorites

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.paging.LivePagedListBuilder
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.ui.main.fragments.GiphyViewModel
import com.gondev.giphyfavorites.ui.main.getDistinct

class FavoriteViewModel @ViewModelInject constructor(
    dao: GifDataDao,
    val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : GiphyViewModel(dao) {
    override val gifList = LivePagedListBuilder(dao.findFavoriteGif(), 20)
        .build().getDistinct()
}