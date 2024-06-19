package com.capstone.chilifit.helper.repository

import androidx.lifecycle.LiveData
import com.capstone.chilifit.data.local.database.ResultDao
import com.capstone.chilifit.data.local.entity.ResultEntity

class HistoryRepository(private val resultDao: ResultDao) {

    fun getAllResults(): LiveData<List<ResultEntity>> {
        return resultDao.getAllResults()
    }

    suspend fun insert(resultEntity: ResultEntity) {
        resultDao.insert(resultEntity)
    }

    suspend fun delete(resultEntity: ResultEntity) {
        resultDao.delete(resultEntity)
    }
}