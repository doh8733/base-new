package com.example.colorphone.base

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.colorphone.R
import com.example.colorphone.ui.ImageViewModel
import com.example.colorphone.util.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<B : ViewBinding>(val inflate: Inflate<B>) : Fragment() {
    var navController: NavController? = null
    private lateinit var _binding: B
    val binding get() = _binding

    var viewPreview: View? = null

    var viewHome: View? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeViewModel: HomeViewModel by activityViewModels()

    val imageViewModel: ImageViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        onSubscribeObserver(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        _binding = inflate.invoke(inflater, container, false)
//        if (navController?.currentDestination?.id == R.id.themeFragment) {
//            if (viewPreview == null) {
//                viewPreview = binding.root
//                return viewPreview
//            } else {
//                return viewPreview
//            }
//        }

        return binding.root
    }

    abstract fun init(view: View)
    abstract fun onSubscribeObserver(view: View)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isConnectedViaWifi(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return mWifi!!.isConnected || mMobile!!.isConnected
    }
}