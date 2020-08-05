package com.gondev.giphyfavorites.ui.gallery

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.gondev.giphyfavorites.BR
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.databinding.GalleryActivityBinding
import com.gondev.giphyfavorites.databinding.GalleryItemBinding
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.ui.GiphyAdapter
import dagger.hilt.android.AndroidEntryPoint

fun Context.startGalleryActivity(index: Int, isFavorite: Boolean) {
    startActivity(Intent(this, GalleryActivity::class.java).apply {
        putExtra("index", index)
        putExtra("isFavorite", isFavorite)
    })
}

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: GalleryActivityBinding
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)

        window.decorView.setBackgroundColor(resources.getColor(android.R.color.black))
        supportActionBar?.setBackgroundDrawable(ColorDrawable( Color.BLACK))
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter =
            GiphyAdapter<GifDataEntity, GalleryItemBinding>(
                layoutResId = R.layout.item_gif_viewpager,
                bindingVariableId = BR.gifData,
                diffCallback = object : DiffUtil.ItemCallback<GifDataEntity>() {
                    override fun areItemsTheSame(oldItem: GifDataEntity, newItem: GifDataEntity) =
                        oldItem.id == newItem.id

                    override fun areContentsTheSame(
                        oldItem: GifDataEntity,
                        newItem: GifDataEntity
                    ) =
                        oldItem == newItem
                },
                lifecycleOwner = this,
                param = *arrayOf(BR.vm to viewModel)
            )

        Handler().postDelayed({
            binding.viewPager.visibility = View.VISIBLE
            binding.viewPager.setCurrentItem(intent.getIntExtra("index", 0), false)
        }, 150)

    }

    fun onClickItem(v: View) {
        toggleHideyBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.navigationBarColor = Color.BLACK
    }

    fun toggleHideyBar() {
        val uiOptions: Int = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled =
            uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions

        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions
    }
}
