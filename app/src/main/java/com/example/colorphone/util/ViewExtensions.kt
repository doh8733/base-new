package com.example.colorphone.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.StrictMode
import android.os.SystemClock
import android.provider.MediaStore
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.colorphone.model.Folder.Companion.IS_IMAGE
import com.example.colorphone.model.Folder.Companion.IS_VIDEO
import java.util.Locale
import java.util.TimeZone

fun Context.getActionBarHeight(): Int {
    val tv = TypedValue()
    if (this.theme?.resolveAttribute(
            android.R.attr.actionBarSize,
            tv,
            true
        ) == true
    ) {
        return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    }
    return 0
}

fun View.changeBackgroundColor(newColor: Int) {
    setBackgroundColor(
        ContextCompat.getColor(
            context,
            newColor
        )
    )
}

fun ImageView.setTintColor(@ColorRes color: Int) {
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
}

fun TextView.changeTextColor(newColor: Int) {
    setTextColor(
        ContextCompat.getColor(
            context,
            newColor
        )
    )
}

fun View.animRotation() {
    val anim = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.interpolator = LinearInterpolator()
    anim.duration = 4000
    anim.isFillEnabled = true
    anim.repeatCount = Animation.INFINITE
    anim.fillAfter = true
    startAnimation(anim)
}

fun View.animRotation2() {
    val anim = RotateAnimation(
        360f, 0f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.interpolator = LinearInterpolator()
    anim.duration = 4000
    anim.isFillEnabled = true
    anim.repeatCount = Animation.INFINITE
    anim.fillAfter = true
    startAnimation(anim)
}

fun View.animRotation3() {
    val anim = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.interpolator = LinearInterpolator()
    anim.duration = 1500
    anim.isFillEnabled = true
    anim.repeatCount = Animation.INFINITE
    anim.fillAfter = true
    startAnimation(anim)
}

fun View.isShow() = visibility == View.VISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.inv() {
    visibility = View.INVISIBLE
}

fun View.setPreventDoubleClick(debounceTime: Long = 500, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.setPreventDoubleClickScaleView(debounceTime: Long = 500, action: () -> Unit) {
    setOnTouchListener(object : View.OnTouchListener {
        private var lastClickTime: Long = 0
        private var rect: Rect? = null

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            fun setScale(scale: Float) {
                v.scaleX = scale
                v.scaleY = scale
            }

            if (event.action == MotionEvent.ACTION_DOWN) {
                //action down: scale view down
                rect = Rect(v.left, v.top, v.right, v.bottom)
                setScale(0.9f)
            } else if (rect != null && !rect!!.contains(
                    v.left + event.x.toInt(),
                    v.top + event.y.toInt()
                )
            ) {
                //action moved out
                setScale(1f)
                return false
            } else if (event.action == MotionEvent.ACTION_UP) {
                //action up
                setScale(1f)
                //handle click too fast
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) {
                } else {
                    lastClickTime = SystemClock.elapsedRealtime()
                    action()
                }
            } else {
                //other
            }

            return true
        }
    })
}

fun Fragment.displayToast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.displayToast(@StringRes msg: Int) {
    Toast.makeText(context, getString(msg), Toast.LENGTH_SHORT).show()
}

fun Fragment.convertDpToPx(dp: Int): Int {
    val dip = dp.toFloat()
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip,
        resources.displayMetrics
    ).toInt()
}

fun Context.convertDpToPx(dp: Int): Int {
    val dip = dp.toFloat()
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip,
        resources.displayMetrics
    ).toInt()
}

fun Context.haveNetworkConnection(): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    return try {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName
                    .equals("WIFI", ignoreCase = true)
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName
                    .equals("MOBILE", ignoreCase = true)
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        haveConnectedWifi || haveConnectedMobile
    } catch (e: java.lang.Exception) {
        System.err.println(e.toString())
        false
    }
}

fun Context.sendEmailMore(
    addresses: Array<String>,
    subject: String,
    body: String
) {

    disableExposure()
    val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
    emailSelectorIntent.data = Uri.parse("mailto:")

    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
    // intent.type = "message/rfc822"
    // intent.data = Uri.parse("mailto:") // only email apps should handle this
    intent.putExtra(Intent.EXTRA_EMAIL, addresses)
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
//            if (uris.size > 0)
//                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)


    intent.putExtra(
        Intent.EXTRA_TEXT, body + "\n\n\n" +
                "DEVICE INFORMATION (Device information is useful for application improvement and development)"
                + "\n\n" + getDeviceInfo()
    )
    intent.selector = emailSelectorIntent

    if (intent.resolveActivity(packageManager) != null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "you need install gmail", Toast.LENGTH_SHORT).show()
    }
}

private fun getDeviceInfo(): String {
    val densityText = when (Resources.getSystem().displayMetrics.densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "LDPI"
        DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
        DisplayMetrics.DENSITY_HIGH -> "HDPI"
        DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
        DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
        DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
        else -> "HDPI"
    }

    //TODO: Update android Q
    var megAvailable = 0L

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val bytesAvailable: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
            megAvailable = bytesAvailable / (1024 * 1024)
        }
    }


    return "Manufacturer ${Build.MANUFACTURER}, Model ${Build.MODEL}," +
            " ${Locale.getDefault()}, " +
            "osVer ${Build.VERSION.RELEASE}, Screen ${Resources.getSystem().displayMetrics.widthPixels}x${Resources.getSystem().displayMetrics.heightPixels}, " +
            "$densityText, Free space ${megAvailable}MB, TimeZone ${
                TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
            }"
}

private fun disableExposure() {
    if (Build.VERSION.SDK_INT >= 24) {
        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}


fun Context.getStringResourceByName(aString: String): String {
    val packageName: String = packageName
    val resId: Int = resources.getIdentifier(aString, "string", packageName)
    return getString(resId)
}

fun Context.openBrowser(url: String) {
    var url = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://$url"
    }
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(browserIntent)
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
}

fun Context.getIp():String{
    val wifiMgr = this.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val wifiInfo = wifiMgr!!.connectionInfo
    val ip = wifiInfo.ipAddress
    return Formatter.formatIpAddress(ip)
}

fun Context.getDisplayWidth(): Float {
    val displayMetrics = resources.displayMetrics
    return displayMetrics.widthPixels.toFloat()
}
fun isVideo(path: String): Boolean {
    return path.endsWith(".mp4") ||
            path.endsWith(".avi") ||
            path.endsWith(".mkv") ||
            path.endsWith(".wmv") ||
            path.endsWith(".mov") ||
            path.endsWith(".flv") ||
            path.endsWith(".3gp") ||
            path.endsWith(".webm")
}
fun isImage(typeMedia: String): Int {
    val list: MutableList<String> = mutableListOf()
    list.add("jpg")
    list.add("jpeg")
    list.add("gif")
    list.add("png")
    list.add("svg")
    list.add("webp")
    list.add("pds")
    list.add("ai")

    list.find {
        it == typeMedia.toLowerCase()
    }?.let {
        return IS_IMAGE
    }
    return IS_VIDEO
}

fun Activity.insertImage(path :String,uri: Uri){
    MediaStore.Images.Media.insertImage(
        this.contentResolver,
        path,
        "Color_Phone_Crop_Image_${System.currentTimeMillis()}",
        "Color Phone Crop Image"
    )
    this.sendBroadcast(
        Intent(
            Intent.ACTION_MEDIA_SCANNER_FINISHED,
            uri
        )
    )
}