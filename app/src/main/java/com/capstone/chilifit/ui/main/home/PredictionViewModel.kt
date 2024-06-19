package com.capstone.chilifit.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.chilifit.data.network.retrofit.ApiConfig
import com.capstone.chilifit.helper.repository.Repository
import kotlinx.coroutines.launch
import java.io.File

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.chilifit.data.network.response.PredictionResponse
import com.capstone.chilifit.helper.repository.PredictionRepository

class PredictionViewModel : ViewModel() {
    private val repository = PredictionRepository()

    private val _predictionResult = MutableLiveData<Result<PredictionResponse>>()
    val predictionResult: LiveData<Result<PredictionResponse>> get() = _predictionResult

    fun createPrediction(imageFile: File, predictionResult: String) {
        repository.sendPrediction(imageFile.absolutePath, predictionResult) { result ->
            _predictionResult.postValue(result)
        }
    }
}
