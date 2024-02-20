package com.example.colorphone.ui.customer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.colorphone.R
import com.example.colorphone.databinding.ItemImageVideoBinding
import com.example.colorphone.databinding.ItemTimeGroupBinding
import com.example.colorphone.model.DateItem
import com.example.colorphone.model.Folder.Companion.IS_IMAGE
import com.example.colorphone.model.MediaItem
import com.example.colorphone.model.ListItem
import com.example.colorphone.model.ListItem.Companion.TYPE_DATE
import com.example.colorphone.model.ListItem.Companion.TYPE_MEDIA
import com.example.colorphone.util.getDisplayWidth
import com.example.colorphone.util.gone
import com.example.colorphone.util.setPreventDoubleClick

class GalleryAdapter(
    context: Context,
    val requestManager: RequestManager,
    val onClick: (MediaItem) -> Unit
) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(GalleryDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewMedia: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_video, parent, false)
        val viewTime: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_group, parent, false)
        return when (viewType) {
            TYPE_MEDIA -> MediaViewHolder(viewMedia)
            TYPE_DATE -> TimeGroupViewHolder(viewTime)
            else -> MediaViewHolder(viewMedia)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type()
    }

    override fun getItemCount(): Int {
        return if (currentList != null) currentList.size else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_MEDIA -> {
                holder as MediaViewHolder
                holder.bind(currentList[position] as MediaItem)
            }

            TYPE_DATE -> {
                holder as TimeGroupViewHolder
                holder.bindTime(currentList[position] as DateItem)
            }
        }
    }

    inner class TimeGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTimeGroupBinding.bind(view)

        init {

        }

        fun bindTime(dateItem: DateItem) {
            binding.apply {
                tvDateTime.text = dateItem.date
            }
        }
    }

    inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemImageVideoBinding.bind(view)
        fun bind(media: MediaItem) {
            binding.apply {
                itemView.layoutParams.width =
                    (itemView.context.getDisplayWidth() / 3f).toInt()
                requestManager.apply {
                    load(media.media?.path).encodeQuality(50).placeholder(R.drawable.bg_blur)
                        .into(imvItem)
                    if (media.media?.isImage == IS_IMAGE) {
                        load(R.drawable.ic_custom).into(imgCustom)
                        tvTimeDuration.gone()
                    } else {
                        Log.d("TAGHJF", media.media?.duration.toString())
                        tvTimeDuration.visibility = View.GONE
                        load(R.drawable.ic_custom).into(imgCustom)
                        tvTimeDuration.text =
                            media.media?.duration?.let { formatMillisToTimeProgress(it) }
                    }
                }
            }

            itemView.setPreventDoubleClick {
                onClick(media)
            }
        }

        private fun formatMillisToTimeProgress(ms: Long): String {
            val time = ms / 1000
            val h = time / 3600
            val m = (time - h * 3600) / 60
            val s = time - h * 3600 - m * 60
            return if (h > 0) {
                String.format("%02d:%02d:%02d", h, m, s)
            } else {
                String.format("%02d:%02d", m, s)
            }
        }
    }
}