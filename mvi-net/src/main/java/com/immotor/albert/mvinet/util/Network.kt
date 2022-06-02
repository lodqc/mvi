package com.immotor.albert.mvinet.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * 判断网络连接
 * @Inject 告诉hilt使用构造器创建依赖
 */
class Network constructor(private val context: Context) {

    /**
     *@link http://www.cocoachina.com/articles/65133
     *@link https://blog.csdn.net/qq_39420519/article/details/116134999
     */
    @Suppress("DEPRECATION")
    fun isConnected(): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // wifi
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true // vpn
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // 移动网络
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

}