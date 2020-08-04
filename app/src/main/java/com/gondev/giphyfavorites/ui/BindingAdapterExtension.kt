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
import androidx.databinding.adapters.ListenerUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
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

// 다음 3개의 BindingAdapter 함수는
// 양방향 데이터 바인딩을 위해 필요한 하나의 세트이다
// https://blog.yatopark.net/2017/07/16/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%9D%98-2-way-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B0%94%EC%9D%B8%EB%94%A9/
@BindingAdapter("refresh")
fun SwipeRefreshLayout.setRefresh(refresh: Boolean) {
    if (isRefreshing != refresh)
        isRefreshing = refresh
}

@InverseBindingAdapter(attribute = "refresh", event = "onRefresh")
fun SwipeRefreshLayout.getRefresh(): Boolean {
    return isRefreshing
}

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.setOnRefreshPageListener(listener: InverseBindingListener) {
    setOnRefreshListener {
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

@BindingAdapter("onPageScrollStateChanged")
fun ViewPager2.setOnPageScrolledListener(onPageScrollStateChangedListener: OnPageScrolledListener) {
    val onPageChangeListener= object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageScrollStateChangedListener.onPageSelected(position)
        }
    }
    ListenerUtil.trackListener<ViewPager2.OnPageChangeCallback>(
        this,
        onPageChangeListener, R.id.currentItemSelectedListener
    )?.let {
        unregisterOnPageChangeCallback(it)
    }

    registerOnPageChangeCallback(onPageChangeListener)
}

interface OnPageScrolledListener {
    fun onPageSelected(position: Int)
}

@BindingAdapter("src", "src_size", "thumbnail", "thumbnail_size", requireAll = true)
fun ImageView.bindImage(src: String?, srcSize: Int, thumbnail: String?, thumbnailSize: Int) {
    if (src == null) {
        setImageResource(R.drawable.empty_image)
        return
    }

    val roundedCorners: Transformation<Bitmap> = RoundedCorners(4)

    var glide = Glide.with(context).load(src)

    // 스틸 썸네일이 없는 경우가 있다
    // 있는 경우만 썸네일을 넣어 주자
    if (thumbnail != null) {
        glide = glide.thumbnail(
            Glide.with(context).load(thumbnail)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(getGlideRequestOption(thumbnail, thumbnailSize))
        )
    }

    // 움직이는 gif 보다 webp가 효율이 좋다
    //  이미지가 webp이면 webp로 재생
    if (src.endsWith("webp"))
        glide = glide.optionalTransform(
            WebpDrawable::class.java,
            WebpDrawableTransformation(roundedCorners)
        )

    // 아니면 일반 이미지로 재생
    glide.optionalTransform(roundedCorners)
        .apply(getGlideRequestOption(src, srcSize))
        .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
        .into(this)
}

/**
 * Best strategy to load images using Glide
 * https://android.jlelse.eu/best-strategy-to-load-images-using-glide-image-loading-library-for-android-e2b6ba9f75b2
 */
fun getGlideRequestOption(imageName: String, size: Int) =
    RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(imageName))
        .override(size)

/**
 * 이미지 교체시 faid-in이 말을 듣지 않을때
 * 해당 클래스로 처리
 * https://stackoverflow.com/questions/53664645/how-to-apply-animation-on-cached-image-in-glide
 */
class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(300, true) //customize to your own needs or apply a builder pattern
    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}

@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: PagedList<T>?) {
    // layoutManager를 실수로 넣지 않을 경우
    // list에 item이 있어도 RecyclerView에는 아이템이 나타나지 않는다
    // 이런 실수를 방지 하려면 이런 경우 아에 앱을 빨리 죽여 버리는게 낫다
    if (layoutManager == null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? PagedListAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("items")
fun <T> ViewPager2.setItems(items: PagedList<T>?) {
    (this.adapter as? PagedListAdapter<T, *>)?.run {
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

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) =
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                if (orientation == RecyclerView.HORIZONTAL)
                    left = space
                else
                    top = space
            }
            if (orientation == RecyclerView.HORIZONTAL)
                right = space
            else
                bottom = space
        }
}