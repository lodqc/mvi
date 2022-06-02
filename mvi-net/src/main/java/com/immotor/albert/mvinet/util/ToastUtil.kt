package com.immotor.albert.mvinet.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.immotor.albert.mvinet.NetInitializer

/**
 * toast工具
 */
object ToastUtil {
    fun toast(content: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(NetInitializer.context, content, duration).show()
    }

    fun toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
        toast(NetInitializer.context.getString(id), duration)
    }

    fun longToast(content: String) {
        toast(content, Toast.LENGTH_LONG)
    }

    fun longToast(@StringRes id: Int) {
        toast(id, Toast.LENGTH_LONG)
    }
}