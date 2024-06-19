package com.capstone.chilifit.data.network.response

data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val image_url: String,
    val created_at: String
)