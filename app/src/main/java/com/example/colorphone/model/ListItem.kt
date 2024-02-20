package com.example.colorphone.model

interface ListItem {
    abstract fun type() : Int

    companion object {
        const val TYPE_DATE = 0
        const val TYPE_MEDIA = 1
    }
}