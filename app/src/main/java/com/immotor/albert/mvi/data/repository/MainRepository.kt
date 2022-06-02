package com.immotor.albert.mvi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.immotor.albert.mvi.data.remote.MainRemoteData
import com.immotor.albert.mvi.data.remote.RepoPagingSource
import com.immotor.albert.mvicore.mvvk.MMKVOwner
import com.immotor.albert.mvicore.mvvk.mmkvBool
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

@ViewModelScoped
class MainRepository constructor(
    private val remoteData: MainRemoteData,
    private val ioDispatcher: CoroutineContext
): MMKVOwner {

    //通过委托封装的mmkv本地数据存储
    var isFirstLaunch by mmkvBool(default = true)
    fun getBatteryStation(map: HashMap<String, Any>) = flow {
        emit(remoteData.getBatteryStation(map))
    }.flowOn(ioDispatcher) // 指定网络请求的线程

    fun querySiteListForMobile() = remoteData.querySiteListForMobile()
    fun searchRepos() = remoteData.searchRepos()

}