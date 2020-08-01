package com.gondev.giphyfavorites.ui

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.RecyclerView
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