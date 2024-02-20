package com.example.colorphone.model

import android.os.Parcel
import android.os.Parcelable

data class Media(
    var id: Int,
    var idAlbum: Int = -1,
    var name: String,
    var isImage: Int,
    var typeMedia: String,
    var uri: String,
    var path: String,
    var pathFolderOrigin: String,
    var size: Long,
    var isHide: Boolean,
    var timeCreated: Long,
    var dateSelect: String = "",
    var idFolder: Long = 0,
    var timeAddToAlbum: Long,
    var isSelected: Boolean = false,
    val duration: Long = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    ) {
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(idAlbum)
        parcel.writeString(name)
        parcel.writeInt(isImage)
        parcel.writeString(typeMedia)
        parcel.writeString(uri)
        parcel.writeString(path)
        parcel.writeString(pathFolderOrigin)
        parcel.writeLong(size)
        parcel.writeByte(if (isHide) 1 else 0)
        parcel.writeLong(timeCreated)
        parcel.writeString(dateSelect)
        parcel.writeLong(idFolder)
        parcel.writeLong(timeAddToAlbum)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }

}
