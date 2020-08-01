package com.gondev.giphyfavorites.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.gondev.giphyfavorites.R


@BindingAdapter("onPageScrollStateChanged")
fun RecyclerView.setOnPageScrolledListener(onPageScrollStateChangedListener: OnPageScrolledListener) {
    val onPageChangeListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                onPageScrollStateChangedListener.onPageScrolled(newState)
            }
        }
    }

    ListenerUtil.trackListener<RecyclerView.OnScrollListener>(
        this,
        onPageChangeListener, R.id.onListReachedBottomListener
    )?.let {
        clearOnScrollListeners()
    }

    addOnScrollListener(onPageChangeListener)

}

interface OnPageScrolledListener {
    fun onPageScrolled(newState: Int)
}

@BindingAdapter("visibleGone")
fun View.showHide(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("src", "src_size", "thumbnail", "thumbnail_size")
fun ImageView.bindImage(src: String, srcSize: Int, thumbnail: String, thumbnailSize: Int) {
    val roundedCorners: Transformation<Bitmap> = RoundedCorners(4)

    Glide.with(context)
        .load(src)
        .thumbnail(
            Glide.with(context).load(thumbnail)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(getGlideRequestOption(thumbnail, thumbnailSize))
        )
        .optionalTransform(roundedCorners)
        .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(roundedCorners) )
        .apply(getGlideRequestOption(src, srcSize))
        .into(this)
}

fun getGlideRequestOption(imageName: String, size: Int) =
    RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(imageName))
        .override(size)