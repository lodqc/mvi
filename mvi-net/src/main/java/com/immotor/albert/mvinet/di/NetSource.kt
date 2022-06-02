package com.immotor.albert.mvinet.di

import com.squareup.moshi.Moshi
import com.immotor.albert.mvinet.net.Error
import com.immotor.albert.mvinet.net.download.DownloadService
import com.immotor.albert.mvinet.net.download.TaskManager
import com.immotor.albert.mvinet.util.Network
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.coroutines.CoroutineContext

/**
 * 自定义的EntryPoint
 * 提供单例实例
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetSource {
    fun getService(): DownloadService

    fun getError(): Error

    fun getMoshi(): Moshi

    fun getNetwork(): Network

    fun getCoroutineContext(): CoroutineContext

    fun getTaskManager(): TaskManager
}