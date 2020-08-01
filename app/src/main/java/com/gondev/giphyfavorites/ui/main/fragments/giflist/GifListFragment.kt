package com.gondev.giphyfavorites.ui.main.fragments.giflist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.gondev.giphyfavorites.BR
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.databinding.GifDataItemBinding
import com.gondev.giphyfavorites.databinding.GifListFragmentBinding
import com.gondev.giphyfavorites.model.network.response.GifData
import com.gondev.recyclerviewadapter.RecyclerViewListAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GifListFragment : Fragment() {
    private lateinit var binding: GifListFragmentBinding
    private val viewModel: GifListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GifListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.vm = viewModel

        binding.recyclerView.adapter =
            RecyclerViewListAdapter<GifData, GifDataItemBinding>(
                layoutResId = R.layout.item_gif,
                bindingVariableId = BR.gifData,
                diffCallback = object : DiffUtil.ItemCallback<GifData>() {
                    override fun areItemsTheSame(oldItem: GifData, newItem: GifData) =
                        oldItem.id == newItem.id

                    override fun areContentsTheSame(oldItem: GifData, newItem: GifData) =
                        oldItem == newItem
                },
                lifecycleOwner = viewLifecycleOwner,
                param = *arrayOf(BR.vm to binding.vm!!)
            )
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        const val ARG_SECTION_NUMBER = "section_number"
        const val SECTION_TRENDING = 0
        const val SECTION_FAVORITES = 1
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): GifListFragment {
            return GifListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}