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

/**
 * [TrendingFragment][com.gondev.giphyfavorites.ui.main.fragments.trending.TrendingViewModel]와
 * [FavoriteFragment][com.gondev.giphyfavorites.ui.main.fragments.favorites.FavoriteViewModel]에서
 * 공통으로 사용하는 코드들을 묶어 놓은 클래스 입니다
 *
 */
abstract class GiphyViewModel(
    val dao: GifDataDao
) : ViewModel() {

    /**
     * [TrendingFragment][com.gondev.giphyfavorites.ui.main.fragments.trending.TrendingViewModel]와
     * [FavoriteFragment][com.gondev.giphyfavorites.ui.main.fragments.favorites.FavoriteViewModel]의
     * gifList는 데이터 셋이 다르지만, 구조는 똑같습니다
     * 각자의 클레스에서 구현 할 수 있도록 abstract으로 남겨 놓습니다
     */
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
     *    ViewModel에서 Context를 들고 있으므로 의존관계가 생긴다
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
        requestStartGalleryActivity.value =
            Event(gifList.value?.indexOfFirst {it: GifDataEntity? ->
                it?.id == clickedItem?.id
            } ?: 0)
    }

    /**
     * "좋아요"를 클릭하면 호출됩니다
     * @param clickedItem "좋아요"를 클릭한 아이템
     */
    fun onClickFavorite(clickedItem: GifDataEntity) {
        viewModelScope.launch {
            // 디비 Entity를 직접 수정하시 마세요
            // 직접 수정하게 되면 레퍼런스 참조에 의해
            // 리스트의 값도 같이 바뀌게 되어 DiffUtil이 변경사항을 확인 할 수 없게 됩니다
            // Entity의 필드는 최대한 val의 형태로 선언 해야 합니다
            // https://stackoverflow.com/questions/54493764/pagedlistadapter-does-not-update-list-if-just-the-content-of-an-item-changes
            dao.update(clickedItem.copy(favorite = !clickedItem.favorite))
        }
    }
}