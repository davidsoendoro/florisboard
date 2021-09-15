package com.kokatto.kobold.roomdb.daointerface

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kokatto.kobold.api.model.basemodel.AutoTextModel

@Dao
interface AutoTextModelDao {
//    @Query("Select * from autotextmodel")
//    fun getAutoTextList(): ArrayList<AutoTextModel>

    @Insert
    fun insertAutoText(autoTextModel: AutoTextModel)

    @Update
    fun updateAutoText(autoTextModel: AutoTextModel)

    @Delete
    fun deleteAutoText(autoTextModel: AutoTextModel)
}
