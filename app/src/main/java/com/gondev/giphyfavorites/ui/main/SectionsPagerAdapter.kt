package com.gondev.giphyfavorites.ui.main

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.ui.main.fragments.favorites.FavoriteFragment
import com.gondev.giphyfavorites.ui.main.fragments.trending.TrendingFragment


const val SECTION_TRENDING = 0
const val SECTION_FAVORITES = 1

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = when (position) {
        SECTION_TRENDING -> TrendingFragment()
        SECTION_FAVORITES -> FavoriteFragment()
        else -> throw IllegalArgumentException("지원하지 않는 페이지 입니다")
    }

    override fun getPageTitle(position: Int) =
        context.resources.getStringArray(R.array.tab_title)[position]

    override fun getCount() = 2
}