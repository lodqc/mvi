package com.immotor.albert.mvinet.net.download

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.immotor.albert.mvinet.data.NetState
import com.immotor.albert.mvinet.di.getTaskManager
import kotlinx.coroutines.CoroutineScope

/**
 * ViewModel扩展
 */
fun ViewModel.download(
    url: String,
    liveData: MutableLiveData<NetState<Uri>>? = null,
    saveName: String = "",
    savePath: String = ""
) {
    viewModelScope.download(url, liveData, saveName, savePath)
}

/**
 * Fragment扩展
 */
fun Fragment.download(
    url: String,
    liveData: MutableLiveData<NetState<Uri>>? = null,
    saveName: String = "",
    savePath: String = ""
) {
    lifecycleScope.download(url, liveData, saveName, savePath)
}

/**
 * 下载入口
 */
fun CoroutineScope.download(
    url: String,
    liveData: MutableLiveData<NetState<Uri>>? = null,
    saveName: String = "",
    savePath: String = ""
): DownloadTask {
    val taskManager = getTaskManager()
    if (taskManager.contain(url)) {
        return taskManager.get(url)!!
    } else {
        val task = DownloadTask(this, url, liveData, saveName, savePath)
        taskManager.add(task)
        return task
    }

}