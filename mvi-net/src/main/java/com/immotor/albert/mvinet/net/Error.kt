package com.immotor.albert.mvinet.net

import com.immotor.albert.mvinet.NetInitializer
import com.immotor.albert.mvinet.R
import javax.inject.Inject
import javax.inject.Singleton

// 无网络
const val ERROR_NO_INTERNET = -1

// 网络请求异常
const val ERROR_REQUEST = -2

// 未知
const val ERROR_UNKNOWN = -3

// 空数据
const val ERROR_EMPTY = -4

// 下载失败
const val ERROR_DOWNLOAD = -5

@Singleton
class Error @Inject constructor(){
    private val errors: Map<Int, String>
        get() = mapOf(
            // 无网络
            Pair(ERROR_NO_INTERNET,  NetInitializer.context.getString(R.string.error_no_internet)),
            // 请求错误
            Pair(ERROR_REQUEST,  NetInitializer.context.getString(R.string.error_network)),
            // 空数据
            Pair(ERROR_EMPTY,  NetInitializer.context.getString(R.string.error_empty)),
            // 下载失败
            Pair(ERROR_DOWNLOAD,  NetInitializer.context.getString(R.string.error_download)),
            // 未知错误
            Pair(ERROR_UNKNOWN,  NetInitializer.context.getString(R.string.error_unknown))
        ).withDefault {
            // 未知错误
            NetInitializer.context.getString(R.string.error_unknown)
        }

    /**
     * 从errors中取错误信息
     */
    fun getError(errorCode: Int): Pair<Int, String> {
        return Pair(errorCode, errors.getValue(errorCode))
    }

     fun getError(errorCode: Int, errorMsg: String): Pair<Int, String> {
        return Pair(errorCode, errorMsg)
    }
}

