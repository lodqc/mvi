package com.immotor.albert.mvinet.di

import com.immotor.albert.mvinet.NetInitializer
import dagger.hilt.EntryPoints

/**
 * 全局获取单例
 */
inline fun <reified T> get(): T {
    return EntryPoints.get(NetInitializer.context, T::class.java)
}

/**
 * 下载Service
 */
fun getService() = get<NetSource>().getService()

/**
 *  错误信息
 */
fun getError() = get<NetSource>().getError()

/**
 * moshi
 */
fun getMoshi() = get<NetSource>().getMoshi()

/**
 * 判断网络
 */
fun getNetwork() = get<NetSource>().getNetwork()

/**
 * 子线程
 */
fun getCoroutineContext() = get<NetSource>().getCoroutineContext()

/**
 * 下载任务
 */
fun getTaskManager() = get<NetSource>().getTaskManager()