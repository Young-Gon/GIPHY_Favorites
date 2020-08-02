package com.gondev.giphyfavorites.ui.main.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class GiphyAdapter<T, V : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    protected val bindingVariableId: Int? = null,
    diffCallback: DiffUtil.ItemCallback<T>,
    protected val lifecycleOwner: LifecycleOwner? = null,
    protected vararg val param: Pair<Int, Any>
) : PagedListAdapter<T, RecyclerViewHolder<T, V>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder<T, V>(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutResId,
                parent,
                false
            ) as V).apply {
                param.forEach {
                    setVariable(it.first, it.second)
                }
                this@GiphyAdapter.lifecycleOwner?.let {
                    lifecycleOwner = it
                }
            }, bindingVariableId
        )

    override fun onBindViewHolder(holder: RecyclerViewHolder<T, V>, position: Int) =
        holder.onBindViewHolder(getItem(position))
}

class RecyclerViewHolder<in T, out V : ViewDataBinding>(
    val binding: V,
    private val bindingVariableId: Int? = null
) : RecyclerView.ViewHolder(binding.root) {

    fun onBindViewHolder(item: T?) {
        try {
            bindingVariableId?.let {
                binding.setVariable(it, item)
                binding.executePendingBindings();
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}