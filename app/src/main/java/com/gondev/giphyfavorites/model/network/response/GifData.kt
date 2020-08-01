package com.gondev.giphyfavorites.model.network.response

import com.gondev.giphyfavorites.model.database.entity.GifDataEntity
import java.util.*

data class GifData (
    val id: String,
    val title: String,
    val trending_datetime: Date,
    val images: Images
){
    fun toEntity()=
        GifDataEntity(
            id=id,
            title = title,
            trending_datetime = trending_datetime,
            previewWebp = images.preview_webp.url,
            fixedWidthDownsampled = images.fixed_width_downsampled.webp,
            originalImage = images.original.webp
        )
}

data class Images(
    val preview_webp: PreviewWebp,
    val fixed_width_downsampled: FixedWidthDownsampled,
    val original: FixedWidthDownsampled
)

data class PreviewWebp(
    val url: String
)

data class FixedWidthDownsampled(
    val url: String,
    val webp: String
)