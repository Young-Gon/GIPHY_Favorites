package com.gondev.giphyfavorites.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory
import com.bumptech.glide.signature.ObjectKey
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.ui.main.fragments.GiphyAdapter


@BindingAdapter("refresh")
fun SwipeRefreshLayout.setRefresh(refresh: Boolean){
    if(isRefreshing!=refresh)
        isRefreshing=refresh
}

@InverseBindingAdapter(attribute = "refresh", event = "onRefresh")
fun SwipeRefreshLayout.getRefresh():Boolean{
    return isRefreshing
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.setOnRefreshPageListener(listener: InverseBindingListener) {
    setOnRefreshListener{
        listener.onChange()
    }
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.setOnRefreshPageListener(listener: SwipeRefreshLayout.OnRefreshListener) {
    setOnRefreshListener(listener)
}

@BindingAdapter("visibleGone")
fun View.showHide(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("src", "src_size", "thumbnail", "thumbnail_size", requireAll = true)
fun ImageView.bindImage(src: String?, srcSize: Int, thumbnail: String?, thumbnailSize: Int) {
    if(src==null) {
        setImageResource(R.drawable.empty_image)
        return
    }

    val roundedCorners: Transformation<Bitmap> = RoundedCorners(4)

    var glide=Glide.with(context).load(src)


    if(thumbnail!=null) {
        glide = glide.thumbnail(
            Glide.with(context).load(thumbnail)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(getGlideRequestOption(thumbnail, thumbnailSize))
        )
    }

    if(src.endsWith("webp"))
        glide=glide.optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(roundedCorners))

    glide.placeholder(R.drawable.empty_image)
        .optionalTransform(roundedCorners)
        .apply(getGlideRequestOption(src, srcSize))
        .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
        .into(this)
}

fun getGlideRequestOption(imageName: String, size: Int) =
    RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(imageName))
        .override(size)

class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(300, true) //customize to your own needs or apply a builder pattern
    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}

@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: PagedList<T>?) {
    if (layoutManager == null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? GiphyAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(@DimenRes margin: Int) {
    addItemDecoration(
        MarginItemDecoration(
            resources.getDimension(margin).toInt(),
            RecyclerView.HORIZONTAL
        )
    )
    addItemDecoration(
        MarginItemDecoration(
            resources.getDimension(margin).toInt(),
            RecyclerView.VERTICAL
        )
    )
}

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(margin: Float) {
    addItemDecoration(
        MarginItemDecoration(
            context.dpToPx(margin),
            RecyclerView.HORIZONTAL
        )
    )
    addItemDecoration(
        MarginItemDecoration(
            context.dpToPx(margin),
            RecyclerView.VERTICAL
        )
    )
}


fun Context.dpToPx(dp: Float) = resources.displayMetrics.let {
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, it).toInt()
}

@BindingAdapter("hasFixedSize")
fun RecyclerView.hasFixedSize(fix: Boolean) {
    setHasFixedSize(fix)
}

@BindingAdapter("selected")
fun View.select(selected: Boolean) {
    isSelected = selected
}

class MarginItemDecoration(
    private val space: Int,

    @RecyclerView.Orientation
    private val orientation: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) =
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                if(orientation== RecyclerView.HORIZONTAL)
                    left = space
                else
                    top = space
            }
            if(orientation== RecyclerView.HORIZONTAL)
                right = space
            else
                bottom = space
        }
}