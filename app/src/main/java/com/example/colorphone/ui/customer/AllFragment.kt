package com.example.colorphone.ui.customer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.insertImage
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.colorphone.R
import com.example.colorphone.base.BaseFragment
import com.example.colorphone.databinding.FragmentAllBinding
import com.example.colorphone.model.DateItem
import com.example.colorphone.model.Folder.Companion.IS_IMAGE
import com.example.colorphone.model.Media
import com.example.colorphone.model.MediaItem
import com.example.colorphone.ui.customer.adapter.GalleryAdapter
import com.example.colorphone.model.ListItem.Companion.TYPE_DATE
import com.example.colorphone.model.ListItem.Companion.TYPE_MEDIA
import com.example.colorphone.util.PrefUtil
import com.example.colorphone.util.gone
import com.example.colorphone.util.insertImage
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AllFragment(val requestManager: RequestManager, val prefUtil: PrefUtil) :
    BaseFragment<FragmentAllBinding>(FragmentAllBinding::inflate) {
    private var galleryAdapter: GalleryAdapter? = null

    //    private var mInterstitialAd: InterstitialAd? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mediaItem: MediaItem? = null
    override fun init(view: View) {
        initAdapter()
//        if (!prefUtil.isPremium) {
//            loadInter()
//        }
    }

    override fun onSubscribeObserver(view: View) {
        getVideoGallery()
    }

    private fun initAdapter() {
        galleryAdapter = context?.let {
            GalleryAdapter(it, requestManager) {
                //  Constants.logEvent("Customer_Choose")
                mediaItem = it
                //   it.media?.let { it1 -> showAds(it1) }
                mediaItem?.media?.let { it1 -> gotoPreview(it1) }
            }
        }
        gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (galleryAdapter!!.getItemViewType(position)) {
                    TYPE_MEDIA -> 1
                    TYPE_DATE -> 3
                    else -> 1
                }
            }
        }
        binding.apply {
            rcvAllMedia.apply {
                val poolView = RecyclerView.RecycledViewPool()
                adapter = galleryAdapter
                layoutManager = gridLayoutManager
                setRecycledViewPool(poolView)
                setHasFixedSize(true)
            }
        }
    }

    private fun getVideoGallery() {
        imageViewModel.dataVideoAndPhotoMedia.observe(viewLifecycleOwner) {
            binding.pbAllMedia.gone()
            groupHashMapDate(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun groupHashMapDate(list: List<Media>) {
        lifecycleScope.launch {
            val hashMapDate = imageViewModel.groupDataIntoHashMap(list)
            val sortedDateList = hashMapDate.keys.sortedByDescending {
                imageViewModel.sdf.parse(it)?.time
            }
            for (date in sortedDateList) {
                val dateItem = DateItem()
                dateItem.date = date
                imageViewModel.consolidatedListAll.add(dateItem)
                for (media in hashMapDate[date]!!) {
                    val mediaItem = MediaItem()
                    mediaItem.media = media
                    imageViewModel.consolidatedListAll.add(mediaItem)
                }
            }
            galleryAdapter?.submitList(imageViewModel.consolidatedListAll)
            galleryAdapter?.notifyDataSetChanged()
        }
    }

//    private fun loadInter() {
//        var adRequest = AdRequest.Builder().build()
//
//        activity?.let {
//            InterstitialAd.load(
//                it,
//                "ca-app-pub-4978184164715100/6638703849",
//                adRequest,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        mInterstitialAd = null
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        mInterstitialAd = interstitialAd
//                    }
//                })
//        }
//    }
//
//    private fun showAds(media: Media) {
//        if (mInterstitialAd == null) {
//            Constants.isShowInterTheme = false
//            gotoPreview(media)
//        } else {
//            Constants.isShowAdsInter = true
//            activity?.let { mInterstitialAd?.show(it) }
//            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//                override fun onAdShowedFullScreenContent() {
//                    super.onAdShowedFullScreenContent()
//                    Constants.isShowInterTheme = true
//                    Handler(Looper.getMainLooper()).postDelayed({
////                        gotoPreview(media)
//                    }, 500)
//
//                }
//
//                override fun onAdDismissedFullScreenContent() {
//                    super.onAdDismissedFullScreenContent()
//                    gotoPreview(media)
//                    Constants.isShowAdsInter = false
//                }
//
//                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                    super.onAdFailedToShowFullScreenContent(p0)
//                    gotoPreview(media)
//                    Constants.isShowAdsInter = false
//                }
//
//                override fun onAdClicked() {
//                    super.onAdClicked()
//                    Constants.isClickAds = true
//                }
//
//            }
//        }
//    }

    private fun gotoPreview(media: Media) {
        if (navController?.currentDestination?.id == R.id.customerFragment) {
            if (media.isImage == IS_IMAGE) {
                activity?.let {
                    val fileDir = File(Environment.getExternalStorageDirectory(), "DCIM/ColorPhone")
                    if (!fileDir.exists()) {
                        fileDir.mkdirs()
                    }
                    val imagePath = File(fileDir, "${System.currentTimeMillis()}.jpg")
                    val options = UCrop.Options()
                    val intent = UCrop.of(Uri.fromFile(File(media.path)), Uri.fromFile(imagePath))
                        .withAspectRatio(9F, 16F)
                        .withOptions(options)
                        .getIntent(it)
                    startActivityForResult(intent, UCrop.REQUEST_CROP)
                }
            } else {
//                navController?.navigate(R.id.previewFragment, Bundle().apply {
//                    putParcelable("media", media)
//                    putInt("screen", Constants.CUSTOMER)
//                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultPath = UCrop.getOutput(data!!)?.path
            val resultUri = UCrop.getOutput(data)
            val media = mediaItem?.media?.let {
                Media(
                    it.id,
                    it.idAlbum,
                    File(resultPath.toString()).name,
                    it.isImage,
                    it.typeMedia,
                    resultUri.toString(),
                    resultPath.toString(),
                    it.pathFolderOrigin,
                    it.size,
                    it.isHide,
                    File(resultPath.toString()).lastModified(),
                    it.dateSelect,
                    it.idFolder,
                    it.timeAddToAlbum,
                    it.isSelected,
                    it.duration
                )
            }
            resultUri?.let { activity?.insertImage(resultPath.toString(), it) }
//            if (navController?.currentDestination?.id == R.id.customerFragment)
//                navController?.navigate(R.id.previewFragment, Bundle().apply {
//                    putParcelable("media", media)
//                    putInt("screen", Constants.CUSTOMER)
//                })
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("loge_let_me_ok", "onActivityResult: ${cropError}")
        }
    }
}