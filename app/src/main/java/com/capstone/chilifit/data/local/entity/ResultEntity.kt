package com.capstone.chilifit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "result_table")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classificationResult: String,
    val classificationImagePath: String
)
