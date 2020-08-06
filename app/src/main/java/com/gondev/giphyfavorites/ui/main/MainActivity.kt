package com.gondev.giphyfavorites.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.gondev.giphyfavorites.R
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 엑티비티 입니다
 * 하위항목으로 [ViewPager]를 가지고 있고
 * 이 [ViewPager]에서 [TrendingFragment][com.gondev.giphyfavorites.ui.main.fragments.trending.TrendingViewModel]와
 * [FavoriteFragment][com.gondev.giphyfavorites.ui.main.fragments.favorites.FavoriteViewModel]를
 * 표시 합니다
 *
 * @see SectionsPagerAdapter
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}