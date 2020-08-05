package com.gondev.giphyfavorites.ui.main.fragments.trending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import com.gondev.giphyfavorites.BR
import com.gondev.giphyfavorites.R
import com.gondev.giphyfavorites.databinding.GifDataItemBinding
import com.gondev.giphyfavorites.databinding.TrendingFragmentBinding
import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import com.gondev.giphyfavorites.ui.GiphyAdapter
import com.gondev.giphyfavorites.ui.gallery.startGalleryActivity
import com.gondev.giphyfavorites.ui.main.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class TrendingFragment : Fragment() {
    private lateinit var binding: TrendingFragmentBinding
    private val viewModel: TrendingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TrendingFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner=viewLifecycleOwner

        binding.recyclerView.adapter =
            GiphyAdapter<GifDataEntity, GifDataItemBinding>(
                layoutResId = R.layout.item_gif,
                bindingVariableId = BR.gifData,
                diffCallback = object : DiffUtil.ItemCallback<GifDataEntity>() {
                    override fun areItemsTheSame(oldItem: GifDataEntity, newItem: GifDataEntity) =
                        oldItem.id == newItem.id

                    override fun areContentsTheSame(oldItem: GifDataEntity, newItem: GifDataEntity) =
                        (oldItem == newItem).apply {
                            if(!this)
                                Timber.d("olditem=${oldItem.title}, newItem=${newItem.title}")
                        }
                },
                lifecycleOwner = viewLifecycleOwner,
                param = *arrayOf(BR.vm to viewModel)
            )

        // DataSource의 변화를 관찰하여 크기가 임계값(40)을 넘으면
        // DataSource 초기 로딩이 완료 되었다고 판단하고
        // 네트워크로 부터 최신 데이터를 요청합니다
        // 만약 초기 로딩이 완료 되지 않은 시점에 DataSource set이 변화
        // 한다면 PagedList는 자동으로 폐기 되고 새로운 DataSource set을
        // 요청합니다 이렇게 되면 화면이 깜빡이거나 아이템이 뒤죽박죽이 되는
        // 현상이 발생합니다
        viewModel.gifList.observe(viewLifecycleOwner, object : Observer<PagedList<GifDataEntity>>{
            override fun onChanged(list: PagedList<GifDataEntity>?) {
                if(list==null)
                    return

                Timber.d("list.size=${list.size}")
                if(40<=list.size){
                    viewModel.onDataSourceInitializingFinished(list.size)
                    viewModel.gifList.removeObserver(this)
                }
            }

        })

        // 겔러리 화면 전환요청이 오면 겔러리 엑티비티 시작
        viewModel.requestStartGalleryActivity.observe(viewLifecycleOwner, EventObserver { index ->
            context?.startGalleryActivity(index,false)
        })
    }
}