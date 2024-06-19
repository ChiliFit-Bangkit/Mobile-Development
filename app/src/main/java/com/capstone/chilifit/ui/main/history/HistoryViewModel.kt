package com.capstone.chilifit.ui.main.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.capstone.chilifit.data.local.database.DatabseClient
import com.capstone.chilifit.data.local.entity.ResultEntity
import com.capstone.chilifit.helper.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository : HistoryRepository
    val allResults: LiveData<List<ResultEntity>>

    init {
        val resultDao = DatabseClient.getDatabase(application).resultDao()
        historyRepository = HistoryRepository(resultDao)
        allResults = historyRepository.getAllResults()
    }

    fun deleteResult(resultEntity: ResultEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.delete(resultEntity)
        }
    }
}