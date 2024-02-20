package com.example.colorphone.model

import com.example.colorphone.model.ListItem.Companion.TYPE_MEDIA

class MediaItem : ListItem {
    var media : Media? = null
    override fun type(): Int {
        return TYPE_MEDIA
    }
}