package com.capstone.chilifit.data.network.retrofit

import com.capstone.chilifit.data.network.response.ArticleResponse
import com.capstone.chilifit.data.network.response.PredictionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("api/articles")
    suspend fun getArticles(): ArticleResponse
    @Multipart
    @POST("api/predictions")
    fun createPrediction(
        @Part imageFile: MultipartBody.Part,
        @Part("prediction_result") predictionResult: RequestBody
    ): Call<PredictionResponse>
}