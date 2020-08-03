package com.gondev.giphyfavorites.ui.main.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class GiphyAdapter<T, V : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val bindingVariableId: Int? = null,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val lifecycleOwner: LifecycleOwner? = null,
    private vararg val param: Pair<Int, Any>
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

    override fun onViewAttachedToWindow(holder: RecyclerViewHolder<T, V>) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerViewHolder<T, V>) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }
}

class RecyclerViewHolder<in T, out V : ViewDataBinding>(
    val binding: V,
    private val bindingVariableId: Int? = null
) : RecyclerView.ViewHolder(binding.root), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

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

    fun onAttached() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onDetached() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}