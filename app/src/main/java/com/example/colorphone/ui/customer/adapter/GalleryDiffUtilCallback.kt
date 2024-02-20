package com.example.colorphone.ui.customer.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.colorphone.model.ListItem

class GalleryDiffUtilCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return false
    }
}
