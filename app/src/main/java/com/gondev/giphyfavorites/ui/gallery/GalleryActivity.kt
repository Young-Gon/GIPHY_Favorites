package com.gondev.giphyfavorites.ui.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gondev.giphyfavorites.BR
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.databinding.GalleryActivityBinding
import com.gondev.giphyfavorites.databinding.GalleryItemBinding
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.ui.GiphyAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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

        Timber.d("index=${intent.getIntExtra("index", 0)}")
        Handler().postDelayed({
            binding.viewPager.visibility= View.VISIBLE
            binding.viewPager.setCurrentItem(intent.getIntExtra("index", 0), false)
        }, 100)
    }
}


/**
 * [ViewPager2]는 내부적으로 [RecyclerView]를 갖고 있고 대부분의 구현을 이 RecyclerView가 하고 있다
 * ViewPager2가 재공하지 못하는 기능들은 내부 RecyclerView를 받아와서 처리해아 한다
 */
fun ViewPager2.getInnerRecyclerView() =
    getChildAt(0) as RecyclerView