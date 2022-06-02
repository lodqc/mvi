package com.immotor.albert.mvinet.net
import com.immotor.albert.mvinet.data.NetState

/**
 * 处理结果
 */
interface ResponseSource {
    /**
     * 开始
     * @param witch 区分请求
     */
    fun start(witch: Int) {

    }

    /**
     * 成功
     * @param witch 区分请求
     * @param result 请求结果
     */
    fun success(witch: Int, result: Any) {

    }

    /**
     * 失败
     * @param witch 区分请求
     * @param error 失败信息
     */
    fun error(witch: Int, error: Pair<Int, String>) {
//        AxToast.toast(error.second)
    }

    /**
     * 结束
     * @param witch 区分请求
     */
    fun complete(witch: Int) {

    }

    /**
     * 下载进度
     * @param witch 区分请求
     * @param percent 当前进度 浮点型
     */
    fun progress(witch: Int, percent: Float) {

    }

    /**
     * 处理结果
     */
    fun <T> handleResult(
        netState: NetState<T>,
        success: ((T) -> Unit)? = null,
        progress: ((Float) -> Unit)? = null,
    ) {
        when (netState) {
            is NetState.Start -> {
                // 开始
                start(netState.which)
            }
            is NetState.Success -> netState.data?.let { data ->
                // 成功 两个只执行一个
                // 执行传入的回调
                success?.invoke(data)
                // 执行接口的回调
                success ?: success(netState.which, data)
            }
            is NetState.Error -> netState.error?.let {
                // 错误
                error(netState.which, it)
            }
            is NetState.Complete -> {
                // 完成
                complete(netState.which)
            }
            is NetState.Progress -> netState.percent?.let {
                // 下载进度
                progress?.invoke(it)
                progress ?: progress(netState.which, it)
            }
        }
    }

}


///**
// * BaseActivity扩展
// * 添加数据监听
// * @param liveData MutableLiveData
// * @param progress 进度
// * @param success 成功
// */
//fun <T, VB : ViewBinding> BasicActivity<VB>.addObserve(
//    liveData: MutableLiveData<Resource<T>>,
//    progress: ((Float) -> Unit)? = null,
//    success: ((T) -> Unit)? = null,
//) {
//    liveData.observe(this) {
//        handleResult(it, success, progress)
//    }
//}
//
///**
// * BaseFragment扩展
// * 添加数据监听
// * @param liveData MutableLiveData
// * @param progress 进度
// * @param success 成功
// */
//fun <T, VB : ViewBinding> BasicFragment<VB>.addObserve(
//    liveData: MutableLiveData<Resource<T>>,
//    // progress和success有默认值null，所以可以只传递其中的一个，
//    // 由于两个方法入参不同，不需要携带形参名，可以自动推断
//    progress: ((Float) -> Unit)? = null,
//    success: ((T) -> Unit)? = null,
//) {
//    liveData.observe(this) {
//        handleResult(it, success, progress)
//    }
//}