package com.gondev.giphyfavorites.ui.main.fragments.trending

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.model.network.response.Pagination
import kotlinx.coroutines.launch
import timber.log.Timber


class TrendingViewModel @ViewModelInject constructor(
    private val dao: GifDataDao,
    private val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var pagination = Pagination(0, 0, 0)

    val state: MutableLiveData<State> = MutableLiveData(State.loading())

    val gifList = LivePagedListBuilder(dao.findGifOrderByTrendingDatetimeDESC(), 20)
        .setBoundaryCallback(object : PagedList.BoundaryCallback<GifDataEntity>() {
            override fun onZeroItemsLoaded() {
                super.onZeroItemsLoaded()
                Timber.d("load form onZero")
                loadDataFromNetwork()
            }

            override fun onItemAtEndLoaded(itemAtEnd: GifDataEntity) {
                super.onItemAtEndLoaded(itemAtEnd)
                Timber.d("load form onItemAtEndLoaded")
                loadDataFromNetwork(offset = pagination.offset + pagination.count)
            }

            fun loadDataFromNetwork(limit: Int = 40, offset: Int = 0) {
                Timber.d("offset=${offset}")
                viewModelScope.launch {
                    try {
                        val netResult = giphyAPI.getGifList(limit = limit, offset = offset)

                        dao.insert(netResult.data.map { it.toEntity() })
                        state.value = State.success()
                        pagination = netResult.pagination
                        Timber.d(pagination.toString())
                    } catch (e: Exception) {
                        Timber.e(e)
                        state.value = State.error(e)

                    }
                }
            }
        })
        .build()

    fun onRefreshList() {

    }
}