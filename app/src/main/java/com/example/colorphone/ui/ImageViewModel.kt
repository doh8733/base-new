package com.example.colorphone.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.colorphone.model.Folder.Companion.IS_IMAGE
import com.example.colorphone.model.Folder.Companion.IS_VIDEO
import com.example.colorphone.model.Media
import com.example.colorphone.model.ListItem
import com.example.colorphone.util.isImage
import com.example.colorphone.util.isVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class ImageViewModel
@Inject
constructor() : ViewModel() {
    //var listAlbums = mutableSetOf<AlbumMedia>()
    var dataPhotoMedia = MutableLiveData<List<Media>>()
    var dataListVideoMedia = MutableLiveData<List<Media>>()
    var dataVideoAndPhotoMedia = MutableLiveData<List<Media>>()
    var consolidatedList = mutableListOf<ListItem>()
    var consolidatedListVideo = mutableListOf<ListItem>()
    var consolidatedListAll = mutableListOf<ListItem>()
    val sdf = SimpleDateFormat.getDateInstance()


    @SuppressLint("Range")
    fun getAllImageAndVideo(
        context: Context,
        onComplete: () -> Unit
    ) {
        var arrMedia = ArrayList<Media>()
        val folders = HashMap<Long, String>()
        val dateMap = HashMap<String, String>()
        var index = 0
        CoroutineScope(Default).launch {
            try {
                val hashMap: HashMap<String, Media> = hashMapOf()
                val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    arrayOf(
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Files.FileColumns.BUCKET_ID,
                    )
                else arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.BUCKET_ID,
                )

                // Return only video and image metadata.
                // Return only video and image metadata.
                val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                val uri = MediaStore.Files.getContentUri("external")
                val cursor =
                    context.contentResolver.query(
                        uri,
                        projection,
                        selection,
                        null,
                        sortOrder
                    )
                if (cursor != null && cursor.count > 0) {

                    while (cursor.moveToNext()) {
                        try {
                            val idMedia: Long =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                            val nameMedia: String = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                            )

                            var pathOrigin: String = ""
                            var path: String = ""


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                pathOrigin = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                )
                                path = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                ) + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                )

                                val a = File(path).path.endsWith("")
                            } else {
                                pathOrigin = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                                val index = pathOrigin.lastIndexOf("/")
                                if (index != -1) {
                                    pathOrigin = pathOrigin.substring(0, index)
                                }
                                path = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                            }
                            val contentUri = Uri.withAppendedPath(uri, "" + idMedia)
                            var typeMedia = "png"
                            var isHide = false
                            val type = nameMedia.split(".")
                            if (type.isNotEmpty()) {
                                typeMedia = type[type.size - 1]
                                isHide = type[0] == "."
                            }
                            val folderIdIndex: Int =
                                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)

                            val folderId: Long = cursor.getLong(folderIdIndex)

                            var timeModified = File(path).lastModified()
                            var coMeMay = "14/05/2021"


                            // sau nay sua sau
                            val outPath = context.filesDir.path + "/file"
                            val output = File(
                                "$outPath/$nameMedia"
                            )
                            var duration = 0L
                            if (isVideo(path)) {
                                try {
//                                    val retriever = MediaMetadataRetriever()
//
//                                    retriever.setDataSource(context, Uri.fromFile(File(path)))
//                                    val time =
//                                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                    duration =
                                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                                            ?.toLong() ?: 0L
                                    // duration = time!!.toLong()
                                    //retriever.release()
                                } catch (e: Exception) {

                                }

                            }

                            if (output.exists()) {
                                if (hashMap[output.path] == null) {
                                    output.delete()
                                } else if (hashMap[output.path]?.pathFolderOrigin == pathOrigin) {
                                    // FileUtils.deleteFile(application, path)
                                }
                            }
                            Log.e("loge_abcd", "getAllImageAndVideo: ádasdas")
                            arrMedia.add(
                                Media(
                                    id = idMedia.toInt(),
                                    name = nameMedia,
                                    isImage = isImage(typeMedia),
                                    typeMedia = typeMedia,
                                    uri = contentUri.toString(),
                                    pathFolderOrigin = pathOrigin,
                                    path = path,
                                    isHide = isHide,
                                    size = File(path).length(),
                                    idFolder = folderId,
                                    dateSelect = coMeMay,
                                    timeCreated = timeModified,
                                    timeAddToAlbum = System.currentTimeMillis(),
                                    duration = duration
                                )
                            )
//                            if (!isVideo(path)) {
//                                arrImage.add(
//                                    Media(
//                                        id = idMedia.toInt(),
//                                        name = nameMedia,
//                                        isImage = isImage(typeMedia),
//                                        typeMedia = typeMedia,
//                                        uri = contentUri.toString(),
//                                        pathFolderOrigin = pathOrigin,
//                                        path = path,
//                                        isHide = isHide,
//                                        size = File(path).length(),
//                                        idFolder = folderId,
//                                        dateSelect = coMeMay,
//                                        timeCreated = timeModified,
//                                        timeAddToAlbum = System.currentTimeMillis(),
//                                        duration = duration
//                                    )
//                                )
//                            } else {
//                                arrVideo.add(
//                                    Media(
//                                        id = idMedia.toInt(),
//                                        name = nameMedia,
//                                        isImage = IS_VIDEO,
//                                        typeMedia = typeMedia,
//                                        uri = contentUri.toString(),
//                                        pathFolderOrigin = pathOrigin,
//                                        path = path,
//                                        isHide = isHide,
//                                        size = File(path).length(),
//                                        idFolder = folderId,
//                                        dateSelect = coMeMay,
//                                        timeCreated = timeModified,
//                                        timeAddToAlbum = System.currentTimeMillis(),
//                                        duration = duration
//                                    )
//                                )
//                            }
                            index++
                            withContext(Main) {
                                if (index == 150) {
                                    // dataPhotoMedia.value = arrImage
                                    dataVideoAndPhotoMedia.value = arrMedia
                                    //    dataListVideoMedia.value = arrVideo
                                    // arrVideo.clear()
                                    // arrImage.clear()
                                    arrMedia.clear()
                                    index = 0
                                }
                            }

                        } catch (ex: Exception) {

                        }
                    }
                    cursor.close()
                    folders.clear()
                    dateMap.clear()
                }
                withContext(Main) {
                    onComplete()
//                    dataPhotoMedia.value = arrImage
//                    dataListVideoMedia.value = arrVideo
                    dataVideoAndPhotoMedia.value = arrMedia

                }
            } catch (e: Exception) {
                withContext(Main) {
                    onComplete()
                }
            }

        }
    }

    @SuppressLint("Range")
    fun getAllVideo(context: Context) {
        var arrVideo = ArrayList<Media>()
        val folders = HashMap<Long, String>()
        val dateMap = HashMap<String, String>()
        var index = 0
        CoroutineScope(Default).launch {
            try {
                val hashMap: HashMap<String, Media> = hashMapOf()
                val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    arrayOf(
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Files.FileColumns.BUCKET_ID,
                    )
                else arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.BUCKET_ID,
                )

                // Return only video and image metadata.
                // Return only video and image metadata.
                val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                val uri = MediaStore.Files.getContentUri("external")
                val cursor =
                    context.contentResolver.query(
                        uri,
                        projection,
                        selection,
                        null,
                        sortOrder
                    )
                if (cursor != null && cursor.count > 0) {

                    while (cursor.moveToNext()) {
                        try {
                            val idMedia: Long =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                            val nameMedia: String = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                            )
                            var pathOrigin: String = ""
                            var path: String = ""


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                pathOrigin = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                )
                                path = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                ) + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                )

                                val a = File(path).path.endsWith("")
                            } else {
                                pathOrigin = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                                val index = pathOrigin.lastIndexOf("/")
                                if (index != -1) {
                                    pathOrigin = pathOrigin.substring(0, index)
                                }
                                path = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                            }
                            val contentUri = Uri.withAppendedPath(uri, "" + idMedia)
                            var typeMedia = "png"
                            var isHide = false
                            val type = nameMedia.split(".")
                            if (type.isNotEmpty()) {
                                typeMedia = type[type.size - 1]
                                isHide = type[0] == "."
                            }
                            val folderIdIndex: Int =
                                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)

                            val folderId: Long = cursor.getLong(folderIdIndex)

                            var timeModified = File(path).lastModified()
                            var coMeMay = "14/05/2021"


                            // sau nay sua sau
                            val outPath = context.filesDir.path + "/file"
                            val output = File(
                                "$outPath/$nameMedia"
                            )
                            var duration = 0L
                            if (isVideo(path)) {
                                try {
//                                    val retriever = MediaMetadataRetriever()
//
//                                    retriever.setDataSource(context, Uri.fromFile(File(path)))
//                                    val time =
//                                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                    duration =
                                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                                            ?.toLong() ?: 0L
                                    // duration = time!!.toLong()
                                    //retriever.release()
                                } catch (e: Exception) {

                                }

                            }

                            if (output.exists()) {
                                if (hashMap[output.path] == null) {
                                    output.delete()
                                } else if (hashMap[output.path]?.pathFolderOrigin == pathOrigin) {
                                    // FileUtils.deleteFile(application, path)
                                }
                            }
                            arrVideo.add(
                                Media(
                                    id = idMedia.toInt(),
                                    name = nameMedia,
                                    isImage = IS_VIDEO,
                                    typeMedia = typeMedia,
                                    uri = contentUri.toString(),
                                    pathFolderOrigin = pathOrigin,
                                    path = path,
                                    isHide = isHide,
                                    size = File(path).length(),
                                    idFolder = folderId,
                                    dateSelect = coMeMay,
                                    timeCreated = timeModified,
                                    timeAddToAlbum = System.currentTimeMillis(),
                                    duration = duration
                                )
                            )
                            index++
                            withContext(Main) {
                                if (index == 70) {
//                                    dataPhotoMedia.value = arrImage
//                                    dataVideoAndPhotoMedia.value = arrMedia
                                    dataListVideoMedia.value = arrVideo
                                    arrVideo.clear()
                                    index = 0
                                }
                            }
                        } catch (ex: Exception) {

                        }
                    }
                    cursor.close()
                    folders.clear()
                    dateMap.clear()
                }
                withContext(Main) {
                    //   onComplete()
                    //dataPhotoMedia.value = arrImage
                    dataListVideoMedia.value = arrVideo
                    //  dataVideoAndPhotoMedia.value = arrMedia

                }
            } catch (e: Exception) {
                withContext(Main) {
                    //    onComplete()
                }
            }

        }
    }


    @SuppressLint("Range")
    fun getAllImage(context: Context) {
        var arrImage = ArrayList<Media>()
        val folders = HashMap<Long, String>()
        val dateMap = HashMap<String, String>()
        var index = 0
        CoroutineScope(Default).launch {
            try {
                val hashMap: HashMap<String, Media> = hashMapOf()
                val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    arrayOf(
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Files.FileColumns.BUCKET_ID,
                    )
                else arrayOf(
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.BUCKET_ID,
                )

                // Return only video and image metadata.
                // Return only video and image metadata.
                val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
                val uri = MediaStore.Files.getContentUri("external")
                val cursor =
                    context.contentResolver.query(
                        uri,
                        projection,
                        selection,
                        null,
                        sortOrder
                    )
                if (cursor != null && cursor.count > 0) {

                    while (cursor.moveToNext()) {
                        try {
                            val idMedia: Long =
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                            val nameMedia: String = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                            )
                            var pathOrigin: String = ""
                            var path: String = ""


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                pathOrigin = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                )
                                path = "sdcard/" + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                                ) + cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                                )

                                val a = File(path).path.endsWith("")
                            } else {
                                pathOrigin = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                                val index = pathOrigin.lastIndexOf("/")
                                if (index != -1) {
                                    pathOrigin = pathOrigin.substring(0, index)
                                }
                                path = cursor.getString(
                                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                                )
                            }
                            val contentUri = Uri.withAppendedPath(uri, "" + idMedia)
                            var typeMedia = "png"
                            var isHide = false
                            val type = nameMedia.split(".")
                            if (type.isNotEmpty()) {
                                typeMedia = type[type.size - 1]
                                isHide = type[0] == "."
                            }
                            val folderIdIndex: Int =
                                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)

                            val folderId: Long = cursor.getLong(folderIdIndex)

                            var timeModified = File(path).lastModified()
                            var coMeMay = "14/05/2021"


                            // sau nay sua sau
                            val outPath = context.filesDir.path + "/file"
                            val output = File(
                                "$outPath/$nameMedia"
                            )
                            var duration = 0L
                            if (isVideo(path)) {
                                try {
//                                    val retriever = MediaMetadataRetriever()
//
//                                    retriever.setDataSource(context, Uri.fromFile(File(path)))
//                                    val time =
//                                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                    duration =
                                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                                            ?.toLong() ?: 0L
                                    // duration = time!!.toLong()
                                    //retriever.release()
                                } catch (e: Exception) {

                                }

                            }

                            if (output.exists()) {
                                if (hashMap[output.path] == null) {
                                    output.delete()
                                } else if (hashMap[output.path]?.pathFolderOrigin == pathOrigin) {
                                    // FileUtils.deleteFile(application, path)
                                }
                            }
                            arrImage.add(
                                Media(
                                    id = idMedia.toInt(),
                                    name = nameMedia,
                                    isImage = IS_IMAGE,
                                    typeMedia = typeMedia,
                                    uri = contentUri.toString(),
                                    pathFolderOrigin = pathOrigin,
                                    path = path,
                                    isHide = isHide,
                                    size = File(path).length(),
                                    idFolder = folderId,
                                    dateSelect = coMeMay,
                                    timeCreated = timeModified,
                                    timeAddToAlbum = System.currentTimeMillis(),
                                    duration = duration
                                )
                            )
                            index++
                            withContext(Main) {
                                if (index == 70) {
                                    dataPhotoMedia.value = arrImage
//                                    dataVideoAndPhotoMedia.value = arrMedia
                                    // dataListVideoMedia.value = arrVideo
                                    arrImage.clear()
                                    index = 0
                                }
                            }
                        } catch (ex: Exception) {

                        }
                    }
                    cursor.close()
                    folders.clear()
                    dateMap.clear()
                }
                withContext(Main) {
                    //   onComplete()
                    //dataPhotoMedia.value = arrImage
                    dataPhotoMedia.value = arrImage
                    //  dataVideoAndPhotoMedia.value = arrMedia

                }
            } catch (e: Exception) {
                withContext(Main) {
                    //    onComplete()
                }
            }

        }
    }

    fun groupDataIntoHashMap(listMediaData: List<Media>): HashMap<String, MutableList<Media>> {
        val groupedHashMap = HashMap<String, MutableList<Media>>()
        for (mediaArray in listMediaData) {
            val hashMapKey = sdf.format(mediaArray.timeCreated)
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(mediaArray)
            } else {
                val list: MutableList<Media> = ArrayList()
                list.add(mediaArray)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

}
