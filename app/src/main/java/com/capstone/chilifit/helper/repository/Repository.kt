package com.capstone.chilifit.helper.repository

import android.content.Context
import android.widget.Toast
import com.capstone.chilifit.data.network.response.ApiResponse
import com.capstone.chilifit.data.network.response.Article
import com.capstone.chilifit.data.network.response.ArticleResponse
import com.capstone.chilifit.data.network.response.PredictionResponse
import com.capstone.chilifit.data.network.retrofit.ApiConfig
import com.capstone.chilifit.data.network.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Repository(private val apiService: ApiService) {
    suspend fun getArticles(): ArticleResponse {
        return apiService.getArticles()
    }
    suspend fun getArticleById(id: Int): Article? {
        val articles = apiService.getArticles().data
        return articles.find { it.id == id }
    }
    fun sendPrediction(imageFilePath: String, predictionResult: String, callback: (Result<PredictionResponse>) -> Unit) {
        val file = File(imageFilePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image_file", file.name, requestFile)
        val result = RequestBody.create("text/plain".toMediaTypeOrNull(), predictionResult)

        val call = ApiConfig.apiService.createPrediction(body, result)
        call.enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Error: ${response.errorBody()}")))
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}