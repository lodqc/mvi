package com.immotor.albert.mvinet.net

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.immotor.albert.mvinet.data.NetState
import com.immotor.albert.mvinet.di.getError
import com.immotor.albert.mvinet.di.getNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * ViewModel扩展
 */
fun <T> ViewModel.request(
    flow: Flow<T>, witch: Int = 0,
    callBck: (state: NetState<T>) -> Unit
) {
    viewModelScope.request(flow, witch, callBck)
}

/**
 * 发送请求，并处理flow的返回结果
 */
fun <T> CoroutineScope.request(
    flow: Flow<T>,
    witch: Int = 0,
    callBck: (state: NetState<T>) -> Unit
) {
    //请求之前操作
    val network = getNetwork()
    if (!network.isConnected()) {
        // 无网络
        val errors = getError()
        callBck.invoke(NetState.Error(witch, errors.getError(ERROR_NO_INTERNET)))
        return
    }
    callBck.invoke(NetState.Start(witch))
    this.launch {
        //网络请求
        flow.catch { // 异常捕获处理
            XLog.e("Exception", it)
            val errors = getError()
            val error = when (it) {
                // 连接超时
                is ConnectException,
                    // Socket网络异常
                is SocketTimeoutException,
                    // 数据解析异常
                is JSONException,
                    // 请求超时
                is TimeoutException -> errors.getError(ERROR_REQUEST)
                is HttpException -> {
                    // non-2xx HTTP response
                    val he = it as HttpException
                    errors.getError(he.code(), he.message())
                }
                is ServerException -> {
                    // todo 处理服务端自定义code
                    val se = it as ServerException
                    errors.getError(se.code(), se.message())
                }
                else -> {
//                    it.message ?: errors.getError(ERROR_UNKNOWN)
                    it.message?.let { cause ->
                        // msg不为空
                        errors.getError(ERROR_UNKNOWN, cause)
                    } ?: errors.getError(ERROR_UNKNOWN) // msg为空
                }
            }
            callBck.invoke(NetState.Error(witch, error))
        }
            .onCompletion {
                // 结束
                callBck.invoke(NetState.Complete(witch))
            }
            //数据请求返回处理
            .collect {
                callBck.invoke(NetState.Success(witch, it))
            }
    }
}