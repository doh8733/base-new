package com.example.colorphone.ui.customer

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.colorphone.ui.customer.adapter.GalleryAdapter
import com.example.colorphone.base.BaseFragment
import com.example.colorphone.databinding.FragmentPhotoBinding
import com.example.colorphone.model.DateItem
import com.example.colorphone.model.ListItem
import com.example.colorphone.model.Media
import com.example.colorphone.model.MediaItem
import com.example.colorphone.util.PrefUtil
import com.example.colorphone.util.gone
import com.example.colorphone.util.insertImage
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class PhotoFragment(val requestManager: RequestManager, val prefUtil: PrefUtil) :
    BaseFragment<FragmentPhotoBinding>(FragmentPhotoBinding::inflate) {
    private var galleryAdapter: GalleryAdapter? = null
   // var mInterstitialAd: InterstitialAd? = null
    private var mActivity: Activity? = null
    private var media: MediaItem? = null
    private var mListItem = listOf<ListItem>()
    override fun init(view: View) {
        initAdapter()
//        if (!prefUtil.isPremium) {
//          //  loadInter()
//        }
    }

    override fun onSubscribeObserver(view: View) {
        getImageGallery()
    }

    private fun initAdapter() {
        galleryAdapter = mActivity?.let {
            GalleryAdapter(it, requestManager) {
               // Constants.logEvent("Customer_Choose")
                media = it
             //   it.media?.let { it1 -> showAds(it1) }
            }
        }
        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (galleryAdapter!!.getItemViewType(position)) {
                    ListItem.TYPE_MEDIA -> 1
                    ListItem.TYPE_DATE -> 3
                    else -> 1
                }
            }
        }
        binding.apply {
            rvGallery.apply {
                val poolView = RecyclerView.RecycledViewPool()
                layoutManager = gridLayoutManager
                adapter = galleryAdapter
                setRecycledViewPool(poolView)
                hasFixedSize()
                itemAnimator
            }
        }
    }

//    private fun loadInter() {
//        var adRequest = AdRequest.Builder().build()
//
//        mActivity?.let {
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

//    private fun showAds(media: Media) {
//        if (mInterstitialAd == null) {
//            Constants.isShowInterTheme = false
//            gotoPreview(media)
//        } else {
//            Constants.isShowAdsInter = true
//            mActivity?.let { mInterstitialAd?.show(it) }
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
        mActivity?.let {
            val fileDir = File(Environment.getExternalStorageDirectory(), "DCIM/ColorPhone")
            val imagePath = File(fileDir, "${System.currentTimeMillis()}.jpg")
            val options = UCrop.Options()
            val intent = UCrop.of(Uri.fromFile(File(media.path)), Uri.fromFile(imagePath))
                .withAspectRatio(9F, 16F)
                .withOptions(options)
                .getIntent(it)
            startActivityForResult(intent, UCrop.REQUEST_CROP)
        }
    }

    private fun getImageGallery() {
        imageViewModel.dataPhotoMedia.observe(viewLifecycleOwner) {
            binding.pbLoading.gone()
            it.map {
                Log.e("loge_image_anything", "getImageGallery: ${it.path}", )
            }
            groupHashMapDate(it)
        }
//        imageViewModel.success.observe(viewLifecycleOwner){
//            val sortedList = imageViewModel.consolidatedList.sortedByDescending {
//                if (it is MediaItem){
//                    it.media?.timeCreated
//                }else 0
//            }
//            galleryAdapter?.submitList(sortedList)
//            galleryAdapter?.notifyDataSetChanged()
//        }
    }


    private fun groupHashMapDate(list: List<Media>) {
        val hashMapDate = imageViewModel.groupDataIntoHashMap(list)
        val sortedDateList = hashMapDate.keys.sortedByDescending {
            imageViewModel.sdf.parse(it)?.time
        }
        for (date in sortedDateList) {
            val dateItem = DateItem()
            dateItem.date = date
            dateItem.timeCreated = imageViewModel.sdf.parse(date)?.time
            imageViewModel.consolidatedList.add(dateItem)
            val mediaList = hashMapDate[date] ?: emptyList()
            for (media in mediaList) {
                val mediaItem = MediaItem()
                mediaItem.media = media
                imageViewModel.consolidatedList.add(mediaItem)
            }
        }
        galleryAdapter?.submitList(imageViewModel.consolidatedList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = if (context is Activity) context else null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultPath = UCrop.getOutput(data!!)?.path
            val resultUri = UCrop.getOutput(data)
            val media = media?.media?.let {
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
//            navController?.navigate(R.id.previewFragment, Bundle().apply {
//                putParcelable("media", media)
//                putInt("screen", Constants.CUSTOMER)
//            })
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("loge_let_me_ok", "onActivityResult: ${cropError}")
        }
    }
}