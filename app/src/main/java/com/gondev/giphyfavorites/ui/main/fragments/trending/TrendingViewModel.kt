package com.gondev.giphyfavorites.ui.main.fragments.trending

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.State
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.model.network.response.Pagination
import com.gondev.giphyfavorites.ui.main.fragments.GiphyViewModel
import com.gondev.giphyfavorites.ui.main.getDistinct
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 한번에 가저올 GIF목록 크기 입니다
 */
const val PAGE_SIZE = 20

/**
 * GIPHY Trending 목록을 구성하는 [ViewModel]입니다
 *
 * @see TrendingFragment
 */
class TrendingViewModel @ViewModelInject constructor(
    dao: GifDataDao,
    private val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : GiphyViewModel(dao) {

    /**
     * 로딩된 페이지의 정보를 저장합니다
     */
    private var pagination = Pagination(0, 0, 0)

    /**
     * 최초 로드시 추가 화면갱신을 막기 위한 플래그 입니다
     */
    private var isZeroItemLoaded = false

    /**
     * 네트워크 상태를 나타냅니다
     */
    val state: MutableLiveData<State> = MutableLiveData(State.loading())

    /**
     * GIF 목록을 나타냅니다
     * 이 목록은 [PagedList]로 되어 있는 데, 나중에 [PagedListAdapter]에 전달되어 화면이 갱신 됩니다
     */
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

    /**
     * 네트워크로 부터 offset 이후 부터 limite 만큼 데이터를 가저 옵니다
     *
     * @param limit 가저올 데이터 크기 default=[PAGE_SIZE]
     * @param offset offset 이후 부터 데이터를 가저옵니다 default=0
     * @param option 네트워크 작업이 완료된 후 해야 할 일을 추가로 지정할 수 있습니다
     */
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

    /**
     * [SwipeRefreshLayout][androidx.swiperefreshlayout.widget.SwipeRefreshLayout]의 로딩표시 제어
     */
    val refresh = MutableLiveData<Boolean>(false)

    /**
     * [SwipeRefreshLayout][androidx.swiperefreshlayout.widget.SwipeRefreshLayout]에 의해
     * 리프레쉬가 발생하면 호출 됩니다
     */
    fun onRefreshList() {
        Timber.d("load from onRefreshList")
        loadPrevDataFromNetwork {
            refresh.value = false
        }
    }

    /**
     * DataSource 초기화 완료
     * [DataSource][androidx.paging.DataSource]의 데이터가 화면을 구성하기 적당할 만큼
     * 로딩이 완료되면 호출 됩니다
     */
    fun onDataSourceInitializingFinished(size: Int) {
        if (!isZeroItemLoaded) {
            Timber.d("load header after finished loading DataSource (list.size=${size})")
            pagination.offset = size
            loadPrevDataFromNetwork()
        }
    }

    /**
     * 이전 데이터 요청
     * 디비 데이터 보다 최신 데이터를 요청합니다
     * 페이지 단위로 요청하여 디비에 있는 최신 날짜보다 최신인 항목만 삽입 합니다
     * @param limit 가저올 데이터 크기 default=[PAGE_SIZE]
     * @param option 네트워크 작업이 완료된 후 해야 할 일을 추가로 지정할 수 있습니다
     */
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