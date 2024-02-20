package com.example.colorphone.ui.customer

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.colorphone.base.BaseFragment
import com.example.colorphone.databinding.FragmentVideoBinding
import com.example.colorphone.model.DateItem
import com.example.colorphone.model.ListItem.Companion.TYPE_DATE
import com.example.colorphone.model.ListItem.Companion.TYPE_MEDIA
import com.example.colorphone.model.Media
import com.example.colorphone.model.MediaItem
import com.example.colorphone.ui.customer.adapter.GalleryAdapter
import com.example.colorphone.util.PrefUtil
import com.example.colorphone.util.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment(val requestManager: RequestManager, val prefUtil: PrefUtil) :
    BaseFragment<FragmentVideoBinding>(FragmentVideoBinding::inflate) {
    private var galleryAdapter: GalleryAdapter? = null
  //  var mInterstitialAd: InterstitialAd? = null

    override fun init(view: View) {
        initAdapter()
//        if (!prefUtil.isPremium) {
//            loadInter()
//        }
    }

    override fun onSubscribeObserver(view: View) {
        getImageGallery()
    }

    private fun initAdapter() {
        galleryAdapter = context?.let {
            GalleryAdapter(it, requestManager) {
            //    Constants.logEvent("Customer_Choose")
             //   it.media?.let { it1 -> showAds(it1) }
            }
        }
        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (galleryAdapter!!.getItemViewType(position)) {
                    TYPE_MEDIA -> 1
                    TYPE_DATE -> 3
                    else -> 1
                }
            }
        }
        binding.apply {
            rcvVideo.apply {
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
//                        gotoPreview(media)
//                    }, 500)
//
//                }
//
//                override fun onAdDismissedFullScreenContent() {
//                    super.onAdDismissedFullScreenContent()
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

    private fun getImageGallery() {
        imageViewModel.dataListVideoMedia.observe(viewLifecycleOwner) {
            binding.pbVideo.gone()
            groupHashMapDate(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun groupHashMapDate(list: List<Media>) {
        val hashMapDate = imageViewModel.groupDataIntoHashMap(list)
        val sortedDateList = hashMapDate.keys.sortedByDescending {
            imageViewModel.sdf.parse(it)?.time
        }
        for (date in sortedDateList) {
            val dateItem = DateItem()
            dateItem.date = date
            imageViewModel.consolidatedListVideo.add(dateItem)
            for (media in hashMapDate[date]!!) {
                val mediaItem = MediaItem()
                mediaItem.media = media
                imageViewModel.consolidatedListVideo.add(mediaItem)
            }
        }
        galleryAdapter?.submitList(imageViewModel.consolidatedListVideo)
        galleryAdapter?.notifyDataSetChanged()
    }

//    private fun gotoPreview(media: Media) {
//        if (navController?.currentDestination?.id == R.id.customerFragment) {
//            navController?.navigate(R.id.previewFragment, Bundle().apply {
//                putParcelable("media", media)
//                putInt("screen", Constants.CUSTOMER)
//            })
//        }
//    }
}