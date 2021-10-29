package com.kokatto.kobold.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.kokatto.kobold.api.model.basemodel.AutoTextModel
import com.kokatto.kobold.roomdb.daointerface.AutoTextModelDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [AutoTextModel::class], version = 1)
abstract class AutoTextDatabase : RoomDatabase() {

    abstract fun autoTextDao(): AutoTextModelDao

    companion object {
        private var INSTANCE: AutoTextDatabase? = null

        fun getInstance(context: Context): AutoTextDatabase? {
            if (INSTANCE == null) {
//                synchronized(AutoTextDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AutoTextDatabase::class.java, "autotext.db")
                        .allowMainThreadQueries()
                        .build()
//                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
