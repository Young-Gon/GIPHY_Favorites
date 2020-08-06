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
import com.gondev.giphyfavorites.ui.main.getDistinct
import kotlinx.coroutines.launch
import timber.log.Timber

const val PAGE_SIZE = 20

class TrendingViewModel @ViewModelInject constructor(
    dao: GifDataDao,
    private val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : GiphyViewModel(dao) {

    private var pagination = Pagination(0, 0, 0)
    private var isZeroItemLoaded = false

    val state: MutableLiveData<State> = MutableLiveData(State.loading())

    override val gifList = LivePagedListBuilder(dao.findGif(), PAGE_SIZE)
        .setBoundaryCallback(object : PagedList.BoundaryCallback<GifDataEntity>() {
            override fun onZeroItemsLoaded() {
                super.onZeroItemsLoaded()
                isZeroItemLoaded = true
                Timber.d("load from onZero")
                loadDataFromNetwork()
            }

            override fun onItemAtEndLoaded(itemAtEnd: GifDataEntity) {
                super.onItemAtEndLoaded(itemAtEnd)

                Timber.d("load from onItemAtEndLoaded")
                loadDataFromNetwork(offset = pagination.offset + pagination.count)
            }
        })
        .build().getDistinct()

    fun loadDataFromNetwork(limit: Int = PAGE_SIZE, offset: Int = 0, option: (() -> Unit)? = null) {
        Timber.d("offset=${offset}")
        viewModelScope.launch {
            state.value = State.loading()
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
        Timber.d("load from onRefreshList")
        loadPrevDataFromNetwork {
            refresh.value = false
        }
    }

    fun onDataSourceInitializingFinished(size: Int) {
        if (!isZeroItemLoaded) {
            Timber.d("load header after finished loading DataSource (list.size=${size})")
            pagination.offset = size
            loadPrevDataFromNetwork()
        }
    }

    private fun loadPrevDataFromNetwork(limit: Int = PAGE_SIZE, option: (() -> Unit)? = null) =
        viewModelScope.launch {
            state.value = State.loading()
            val trendingDatetime = dao.findFirstGifDate() ?: return@launch
            try {
                var offset = -PAGE_SIZE
                do {
                    val netResult =
                        giphyAPI.getGifList(limit = PAGE_SIZE, offset = PAGE_SIZE + offset)
                    val firstIndex =
                        netResult.data.indexOfFirst { it.trending_datetime.time <= trendingDatetime.time }
                    if (firstIndex == 0) {
                        Timber.d("NOTHING to add GIFs at header")
                        return@launch
                    }

                    val newList = if (firstIndex > -1)
                        netResult.data.subList(0, firstIndex)
                    else
                        netResult.data
                    Timber.d("added ${newList.size} GIFs at header")
                    dao.insert(newList.map { it.toEntity() })
                    pagination.offset += newList.size
                    offset += newList.size
                } while (firstIndex == -1)

                state.value = State.success()
            } catch (e: Exception) {
                Timber.e(e)
                state.value = State.error(e)
            } finally {
                option?.invoke()
            }
        }
}