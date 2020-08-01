package com.gondev.giphyfavorites.ui.main.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.gondev.giphyfavorites.BR
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.databinding.FavoriteFragmentBinding
import com.gondev.giphyfavorites.databinding.GifDataItemBinding
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.recyclerviewadapter.RecyclerViewListAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var binding: FavoriteFragmentBinding
    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FavoriteFragmentBinding.inflate(inflater,container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner=viewLifecycleOwner

        binding.recyclerView.adapter =
            RecyclerViewListAdapter<GifDataEntity, GifDataItemBinding>(
                layoutResId = R.layout.item_gif,
                bindingVariableId = BR.gifData,
                diffCallback = object : DiffUtil.ItemCallback<GifDataEntity>() {
                    override fun areItemsTheSame(oldItem: GifDataEntity, newItem: GifDataEntity) =
                        oldItem.id == newItem.id

                    override fun areContentsTheSame(oldItem: GifDataEntity, newItem: GifDataEntity) =
                        oldItem == newItem
                },
                lifecycleOwner = viewLifecycleOwner,
                param = *arrayOf(BR.vm to viewModel)
            )
    }
}