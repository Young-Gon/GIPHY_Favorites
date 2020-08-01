package com.gondev.giphyfavorites.ui.main.fragments.trending

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.model.network.response.Pagination
import com.orhanobut.logger.Logger


class TrendingViewModel @ViewModelInject constructor(
    val dao: GifDataDao,
    val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val pagination: MutableLiveData<Pagination> = MutableLiveData(Pagination(0, 0, 0))

    val gifList: LiveData<State<List<GifDataEntity>>> = liveData {

        val dbResult = dao.findGifOrderByTrendingDatetimeDESC()

        val disposable = emitSource(dbResult.map {
            State.loading(it)
        })
        try {
            val netResult = giphyAPI.getGifList()
            disposable.dispose()

            dao.insert(netResult.data.map { it.toEntity() })
            val newResult = dao.findGifOrderByTrendingDatetimeDESC()
            emitSource(newResult.map {
                State.success(it)
            })
            pagination.value=netResult.pagination
        } catch (e: Exception) {
            Logger.e(e, "")

            emitSource(dbResult.map {
                State.error(e, it)
            })
        }
    }

    fun onReachedBottom(newState: Int) {

    }

    fun onRefreshList() {

    }
}