package com.kokatto.kobold.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.vertical(reverse: Boolean = false) {
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, reverse)
}

fun RecyclerView.horizontal(reverse: Boolean = false) {
    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, reverse)
}

fun RecyclerView.setVerticalAdapter(adapter: RecyclerView.Adapter<*>?) {
    this.vertical()
    this.adapter = adapter
}
