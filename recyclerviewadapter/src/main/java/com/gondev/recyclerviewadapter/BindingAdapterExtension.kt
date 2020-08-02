package com.gondev.recyclerviewadapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: List<T>?) {
    if(layoutManager==null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? RecyclerViewListAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(@DimenRes margin: Int) =
    addItemDecoration(MarginItemDecoration(resources.getDimension(margin).toInt(),
        (layoutManager as StaggeredGridLayoutManager).orientation))

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(margin: Float) =
    addItemDecoration(MarginItemDecoration(context.dpToPx(margin),
        (layoutManager as StaggeredGridLayoutManager).orientation))


fun Context.dpToPx(dp: Float) = resources.displayMetrics.let {
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,it).toInt()
}

@BindingAdapter("hasFixedSize")
fun RecyclerView.hasFixedSize(fix: Boolean) {
    setHasFixedSize(fix)
}

@BindingAdapter("selected")
fun View.select(selected: Boolean) {
    isSelected = selected
}
