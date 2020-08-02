package com.gondev.giphyfavorites.model.network.response

import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import java.util.*


data class Result(
    val data: List<GifData>,
    val pagination: Pagination
)

data class Pagination(
    val total_count: Int,
    val count: Int,
    val offset: Int
)

data class GifData(
    val id: String,
    val title: String,
    val trending_datetime: Date,
    val images: Images
) {
    fun toEntity() =
        GifDataEntity(
            id = id,
            title = title,
            trendingDatetime = trending_datetime,
            thumbnail = images.fixed_width_small_still.url,
            thumbnailSize = images.fixed_width_small_still.size,
            fixedWidthDownsampled = images.fixed_width_downsampled.webp?:images.fixed_width_downsampled.url,
            fixedWidthDownsampledSize = if(images.fixed_width_downsampled.webp!=null) images.fixed_width_downsampled.webp_size else images.fixed_width_downsampled.size,
            originalImage = images.original.webp,
            originalImageSize = images.original.webp_size
        )
}

data class Images(
    val fixed_width_small_still: FixedWidthSmallStill,
    val fixed_width_downsampled: FixedWidthDownsampled,
    val original: FixedWidthDownsampled
)

data class FixedWidthSmallStill(
    val url: String?,
    val size: Int
)

data class FixedWidthDownsampled(
    val url: String,
    val size: Int,
    val webp: String?,
    val webp_size: Int
)