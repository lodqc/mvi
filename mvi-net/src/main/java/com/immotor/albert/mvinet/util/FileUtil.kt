package com.immotor.albert.mvinet.util

import android.os.Environment

object FileUtil {
    /**
     * 判断sd卡的存在
     *
     * @return
     */
    fun existSDCard(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}