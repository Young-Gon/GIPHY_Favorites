package com.gondev.giphyfavorites.ui.main.fragments

import com.gondev.giphyfavorites.model.database.entity.GifDataEntity

interface IonClickFavorite {
    fun onClickFavorite(clickedItem: GifDataEntity)
}