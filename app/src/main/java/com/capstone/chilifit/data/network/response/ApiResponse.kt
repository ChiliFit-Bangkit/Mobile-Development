package com.capstone.chilifit.data.network.response

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null
)
