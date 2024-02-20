package com.example.colorphone.model

data class Folder(
    val id: Long,
    val name: String,
    var typeFolder: Int,
    var size: Long = 0,
    var postion: Int = 0
) {
    companion object {
        const val IS_VIDEO = 1
        const val IS_IMAGE = 2
        const val IS_ALL = 3
    }
}
