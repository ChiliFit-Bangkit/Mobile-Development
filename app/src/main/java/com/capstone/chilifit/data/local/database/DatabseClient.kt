package com.capstone.chilifit.data.local.database

import android.content.Context

object DatabseClient {

    private var resultDatabase: ResultDatabase? = null

    fun getDatabase(context: Context): ResultDatabase {
        if (resultDatabase == null) {
            synchronized(ResultDatabase::class.java) {
                if (resultDatabase == null) {
                    resultDatabase = ResultDatabase.getDatabase(context)
                }
            }
        }
        return resultDatabase!!
    }
}
