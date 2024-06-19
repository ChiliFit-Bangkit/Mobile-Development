package com.capstone.chilifit.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.chilifit.data.local.entity.ResultEntity

@Dao
interface ResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resultEntity: ResultEntity)
    @Query("SELECT * FROM result_table")
    fun getAllResults(): LiveData<List<ResultEntity>>

    @Delete
    suspend fun delete(resultEntity: ResultEntity)
}
