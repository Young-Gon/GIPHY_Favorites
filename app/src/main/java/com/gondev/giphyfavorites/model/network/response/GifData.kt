package com.gondev.giphyfavorites.model.network.response

data class GifData (
    val id: String,
    val title: String,
    val images: Images
)

data class Images(
    val preview_webp: PreviewWebp,
    val fixed_width_downsampled: FixedWidthDownsampled
)

data class PreviewWebp(
    val url: String
)

data class FixedWidthDownsampled(
    val url: String,
    val webp: String
)