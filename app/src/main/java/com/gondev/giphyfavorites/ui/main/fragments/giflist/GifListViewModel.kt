package com.gondev.giphyfavorites.ui.main.fragments.giflist

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.network.api.GipyAPI
import com.gondev.giphyfavorites.ui.main.fragments.giflist.GifListFragment.Companion.ARG_SECTION_NUMBER
import com.gondev.giphyfavorites.ui.main.fragments.giflist.GifListFragment.Companion.SECTION_TRENDING

class GifListViewModel @ViewModelInject constructor(
    val dao: GifDataDao,
    val gipyAPI: GipyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        savedStateHandle.keys().forEach { key ->
            Log.d("TAG", "Received [$key]=[${savedStateHandle.get<Any>(key)}]")
        }

    }

    private val gifDataSource =
        if (savedStateHandle.get<Int>(ARG_SECTION_NUMBER) == SECTION_TRENDING)
            dao.findGifOrderByTrendingDatetimeDESC()
        else
            dao.findFavoriteGifOrderByTrendingDatetimeDESC()

    val gifList = liveData {
        LivePagedListBuilder(gifDataSource, 20).build()
        /*try {
            emit(Result.success(gipyAPI.getMovieList()))
        } catch (e: Exception) {
            emit(Result.error(e, null))
        }*/
    }
}