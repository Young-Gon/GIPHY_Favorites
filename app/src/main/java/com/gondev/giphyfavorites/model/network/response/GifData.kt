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
    var offset: Int
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
            fixedWidth = images.fixed_width.webp?:images.fixed_width.url,
            fixedWidthSize = if(images.fixed_width.webp!=null) images.fixed_width.webp_size else images.fixed_width.size,
            originalImage = images.original.webp?:images.original.url,
            originalImageSize = if(images.original.webp!=null) images.original.webp_size else images.original.size,
            originalImageStill = images.original_still.url,
            originalImageStillSize = images.original_still.size
        )
}

data class Images(
    val fixed_width: FixedWidthDownsampled,
    val fixed_width_small_still: FixedWidthSmallStill,
    val original: FixedWidthDownsampled,
    val original_still: FixedWidthSmallStill
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