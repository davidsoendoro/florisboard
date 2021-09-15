package com.kokatto.kobold.api.model.basemodel

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autotextmodel")
data class AutoTextModel(
    @PrimaryKey(autoGenerate = false) val _id: String = "",
    @ColumnInfo(name = "content") val content: String? = "",
    @ColumnInfo(name = "template") val template: String? = "",
    @ColumnInfo(name = "title") val title: String? = ""
)
