package com.capstone.chilifit.data.network.response

data class ImageClassificationRequest(
    val id: Int,
    val prediction_result: String,
    val image_url: String,
    val created_at: String
)
