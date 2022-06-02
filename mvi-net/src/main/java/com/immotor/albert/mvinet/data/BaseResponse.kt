package com.immotor.albert.mvinet.data

/**
 * 服务端数据格式,通过gradle的RESULT_FORMAT设置自动包裹
 */
data class BaseResponse<T>(
    val code: Int = 0,
    val msg: String = "",
    val result: T?, // +? 防止空安全反序列化失败
)