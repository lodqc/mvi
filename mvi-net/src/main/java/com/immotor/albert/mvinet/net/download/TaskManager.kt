package com.immotor.albert.mvinet.net.download

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 管理下载任务
 */
@Singleton
class TaskManager @Inject constructor() {
    private val taskMap = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 加入一个任务
     */
    fun add(task: DownloadTask): DownloadTask {
        if (taskMap[task.url] == null) {
            taskMap[task.url] = task
        }
        return task
    }

    /**
     * 通过url获取任务
     */
    fun get(url: String): DownloadTask? {
        return taskMap[url]
    }

    /**
     * 移除一个任务
     */
    fun remove(task: DownloadTask) {
        taskMap.remove(task.url)
    }

    /**
     * 取消任务
     */
    fun cancel(url: String) {
        val task = taskMap.remove(url)
        task?.cancel()
    }

    /**
     * 判断是否存在同任务
     */
    fun contain(url: String): Boolean {
        val task = taskMap[url]
        return task != null
    }
}