package com.gondev.giphyfavorites.ui.main.fragments.favorites

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gondev.giphyfavorites.model.database.dao.GifDataDao
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.model.network.api.GiphyAPI
import com.gondev.giphyfavorites.ui.main.fragments.IonClickFavorite
import kotlinx.coroutines.launch

class FavoriteViewModel @ViewModelInject constructor(
    val dao: GifDataDao,
    val giphyAPI: GiphyAPI,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(), IonClickFavorite {
    val gifList: LiveData<PagedList<GifDataEntity>> =
        LivePagedListBuilder(dao.findFavoriteGif(), 20)
            .build()

    override fun onClickFavorite(clickedItem: GifDataEntity) {
        viewModelScope.launch {
            // 디비 엔티티를 직접 수정할 경우
            // 리스트 업데이트가 안되는 문제가 있다
            // 기존 엔티티를 복제하여 새로운 엔티티로 만들어 업데이트 시키자
            // https://stackoverflow.com/questions/54493764/pagedlistadapter-does-not-update-list-if-just-the-content-of-an-item-changes
            dao.update(clickedItem.copy(favorite = !clickedItem.favorite))
        }
    }
}