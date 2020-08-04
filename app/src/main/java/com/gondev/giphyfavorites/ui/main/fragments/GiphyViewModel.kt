package com.gondev.giphyfavorites.ui.main.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.ui.main.Event
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class GiphyViewModel(
    val dao: GifDataDao
) : ViewModel() {

    abstract val gifList: LiveData<PagedList<GifDataEntity>>

    /**
     * ViewModel은 View와 Model 사이에 존재한다
     * Model에서 온 data는 ViewModel에서 잠시 있다가
     * UI를 구성하기 위해 View로 흘러간다
     * 반대로, 사용자로 부터 data가 변경 되면 다시
     * ViewModel로 돌아 온다
     * 이 때, 변경된 데이터가 ViewModel에서 처리 불가능한
     * 경우라면 어떻게 할까?
     * 예를 들어, 지금과 같이 새로운 Activity를 실행 시키기 위해
     * Context가 필요한 경우이다
     * 이러한 경우 처리방법은 3가지로 나눌수 있다
     *
     * 1. ViewModel에서 직접 Context를 들고 있다가 처리해주는 방법
     *    ViewModel에서 Context를 들고 있으므로 의존관걔가 생긴다
     *    좋은 방법이 아니다
     *
     * 2. Context가 필요한 시점에 주입 받는다
     *    이 경우라면 view로 부터 onClick 이벤트를 발생시킨 view를
     *    받을 수 있고, 이 view에서 Context를 얻을 수 있다
     *    하지만 결국 ViewModel 내부에서 context에 접근 한다는 점에서
     *    1번 과 같다 나쁘진 않지만 좋지도 않다
     *
     * 3. Context가 필요한 시점에 이를 처리 할 수 있는 Context가
     *    처리하도록 위임할 수 있다
     *    이렇게 하면 ViewModel 내부에서는 Context에 접근할 필요가
     *    없고, 의존 관계도 생기지 않는다
     *    결국, 일의 책임 소재가 명확해 진다
     *    좋은 방법이지만 이렇게 하기 위해 결국 [Event]클레스의 도움을
     *    받아야 한다 로직이 복잡해진다
     */
    val requestStartGalleryActivity = MutableLiveData<Event<Int>>()
    fun onClickItem(clickedItem: GifDataEntity?) {
        Timber.d(clickedItem?.toString())
        requestStartGalleryActivity.value =
            Event(gifList.value?.indexOfFirst {it: GifDataEntity? ->
                it?.id == clickedItem?.id
            } ?: 0)
    }

    fun onClickFavorite(clickedItem: GifDataEntity) {
        viewModelScope.launch {
            dao.update(clickedItem.copy(favorite = !clickedItem.favorite))
        }
    }
}