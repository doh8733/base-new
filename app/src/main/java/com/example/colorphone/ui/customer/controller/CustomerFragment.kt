package com.example.colorphone.ui.customer.controller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.colorphone.callscreen.ui.customer.adapter.CustomerPagerAdapter
import com.example.colorphone.R
import com.example.colorphone.base.BaseFragment
import com.example.colorphone.databinding.FragmentCustomerBinding
import com.example.colorphone.model.Media
import com.example.colorphone.util.PrefUtil
import com.example.colorphone.util.gone
import com.example.colorphone.util.setPreventDoubleClick
import com.example.colorphone.util.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerFragment(val glide: RequestManager, val prefUtil: PrefUtil) :
    BaseFragment<FragmentCustomerBinding>(FragmentCustomerBinding::inflate) {
    private var linearLayoutManager = LinearLayoutManager(context)
    private var customerPagerAdapter : CustomerPagerAdapter? = null
    private var mList = mutableListOf<Media>()
    private val requiredPermissionsStorage = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissionsTIRAMISU = arrayOf(
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO
    )

    private val launchPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){

    }
    override fun init(view: View) {
       // glide.load(R.drawable.bg_main).into(binding.ivTheme)
        initListener()
     //   getGallery()

        initViewPager()
        selectedTabAll()
      //  loadAdsBanner()
     //   Constants.logEvent("Customer_Show")
        checkPermissions()

    }

    override fun onResume() {
//        if (!Constants.isShowOpenAds) {
//            binding.lnAdsBanner.show()
//        }else{
//            binding.lnAdsBanner.inv()
//        }
        super.onResume()
    }

//    private fun loadAdsBanner() {
//        val adRequest = AdRequest.Builder().build()
//        val adView = context?.let { AdView(it) }
//        activity?.getAdSize()?.let { adView?.setAdSize(it) }
//        adView?.adUnitId = "ca-app-pub-4978184164715100/7773014084"
//        adView?.loadAd(adRequest)
//        adView?.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                binding.flAdsBanner.addView(adView)
//            }
//
//            override fun onAdClicked() {
//                super.onAdClicked()
//                Constants.isClickAds = true
//            }
//        }
//    }

//    private fun getGallery() {
//        lifecycleScope.launch {
//            context?.let {
//                imageViewModel.getAllImageAndVideo(it) {
//                }
//
//            }
//        }
//    }

    private fun initListener() {
        binding.apply {
            ivBack.setPreventDoubleClick {
               // Constants.logEvent("Customer_Back_Clicked")
                findNavController().popBackStack()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            findNavController().popBackStack()
          //  Constants.logEvent("Customer_Back_Clicked")
        }
    }

    override fun onSubscribeObserver(view: View) {
        binding.apply {
            llAll.setOnClickListener {
                selectedTabAll()
            }
            llPhoto.setOnClickListener {
                selectedTabPhoto()
            }
            tvAll.setOnClickListener {
                selectedTabAll()
            }
            tvPhoto.setOnClickListener {
                selectedTabPhoto()
            }
            tvVideo.setOnClickListener {
                selectedTabVideo()
            }
            llVideo.setOnClickListener {
                selectedTabVideo()
            }
        }
    }

    private fun initViewPager(){
        customerPagerAdapter = parentFragment?.let { CustomerPagerAdapter(prefUtil,glide, it) }
        binding.vpPageCustomer.adapter = customerPagerAdapter
        binding.vpPageCustomer.offscreenPageLimit = 3
        binding.vpPageCustomer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> selectedTabAll()
                    1 -> selectedTabPhoto()
                    2 -> selectedTabVideo()
                }
            }
        })
    }

    private fun selectedTabAll() {
        context?.let {
            binding.apply {
//                llAll.setBackgroundColor(it.getColor(R.color.color_tab_selected))
//                llPhoto.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
//                llVideo.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
                imgIndicatorAll.show()
                imgIndicatorPhoto.gone()
                imgIndicatorVideo.gone()
                binding.vpPageCustomer.setCurrentItem(0,true)
            }
        }
    }

    private fun selectedTabPhoto() {
        context?.let {
            binding.apply {
//                llAll.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
//                llPhoto.setBackgroundColor(it.getColor(R.color.color_tab_selected))
//                llVideo.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
                imgIndicatorAll.gone()
                imgIndicatorPhoto.show()
                imgIndicatorVideo.gone()
                binding.vpPageCustomer.setCurrentItem(1,true)
            }
        }
    }

    private fun selectedTabVideo() {
        context?.let {
            binding.apply {
//                llAll.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
//                llPhoto.setBackgroundColor(it.getColor(R.color.color_tab_unselected))
//                llVideo.setBackgroundColor(it.getColor(R.color.color_tab_selected))
                imgIndicatorAll.gone()
                imgIndicatorPhoto.gone()
                imgIndicatorVideo.show()
                binding.vpPageCustomer.setCurrentItem(2,true)
            }
        }
    }
    private fun isPermissionGranted(permission: String): Boolean {
        return activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission,
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
       // isGotoTheme = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requiredPermissionsTIRAMISU.all { isPermissionGranted(it) }) {
                context?.let {
                    imageViewModel.apply {
                        getAllImage(it)
                        getAllImageAndVideo(it){}
                        getAllVideo(it)
                    }
                }
              //  gotoCustomer()
            } else {
               // requiredPermissionsStorageAPI33()
                launchPermission.launch(requiredPermissionsTIRAMISU)
            }
        } else {
            if (requiredPermissionsStorage.all { isPermissionGranted(it) }) {
                context?.let {
                    imageViewModel.apply {
                        getAllImage(it)
                        getAllImageAndVideo(it){}
                        getAllVideo(it)
                    }
                }
              //  gotoCustomer()
            } else {
             //   requiredPermissionsStorageAPIBelow33()
                launchPermission.launch(requiredPermissionsStorage)
            }
        }
    }
}