package com.gondev.giphyfavorites.ui.main.fragments.trending

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.model.network.response.Pagination
import com.gondev.giphyfavorites.ui.main.fragments.GiphyViewModel
import kotlinx.coroutines.launch
import timber.log.Timber


class TrendingViewModel @ViewModelInject constructor(
    dao: GifDataDao,
    private val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : GiphyViewModel(dao) {

    private var pagination = Pagination(0, 0, 0)

    val state: MutableLiveData<State> = MutableLiveData(State.loading())

    override val gifList = LivePagedListBuilder(dao.findGif(), 20)
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
        })
        .build()

    fun loadDataFromNetwork(limit: Int = 20, offset: Int = 0, option: (() -> Unit)? = null) {
        Timber.d("offset=${offset}")
        viewModelScope.launch {
            try {
                val netResult = giphyAPI.getGifList(limit = limit, offset = offset)

                dao.insert(netResult.data.map { it.toEntity() })
                state.value = State.success()
                pagination = netResult.pagination
                option?.invoke()
                Timber.d(pagination.toString())
            } catch (e: Exception) {
                Timber.e(e)
                state.value = State.error(e)
            }
        }
    }

    val refresh = MutableLiveData<Boolean>(false)

    fun onRefreshList() {
        loadDataFromNetwork {
            refresh.value = false
        }
    }
}