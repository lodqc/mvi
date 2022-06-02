package com.immotor.albert.mvinet.data

// A generic class that contains data and status about loading this data.
/**
 * 网络状态类，包裹请求和响应实体
 */
sealed class NetState<T>(
    // 区分请求
    val which: Int = 0,
    // 数据结果
    val data: T? = null,
    // 错误信息
    val error: Pair<Int, String>? = null,
    // 下载百分比
    val percent: Float? = null
) {
    class Init<T>(which: Int = 0) : NetState<T>(which)
    class Start<T>(which: Int = 0) : NetState<T>(which)
    class Success<T>(which: Int = 0, data: T) : NetState<T>(which, data)
    class Error<T>(which: Int = 0, error: Pair<Int, String>) : NetState<T>(which, error = error)
    class Progress<T>(which: Int = 0, percent: Float): NetState<T>(which, percent = percent)
    class Complete<T>(which: Int = 0) : NetState<T>(which)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data = $data]"
            is Error -> "Error[exception = ${error?.first} ${error?.second}]"
            is Start<T> -> "Loading"
            is Complete -> "Complete"
            is Init -> "Init"
            is Progress -> "Complete = $percent%"
        }
    }
}
