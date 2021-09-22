package com.kokatto.kobold.api.model.basemodel

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "autotextmodel")
data class AutoTextModel(
    @PrimaryKey(autoGenerate = false) val _id: String = "",
    @ColumnInfo(name = "content") val content: String? = "",
    @ColumnInfo(name = "template") val template: String? = "",
    @ColumnInfo(name = "title") val title: String? = ""
): Parcelable
