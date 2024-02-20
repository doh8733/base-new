package com.example.colorphone.model

class DateItem : ListItem {
    var date : String? = null
    var timeCreated : Long? = 0L
    override fun type(): Int {
        return ListItem.TYPE_DATE
    }
}