package com.immotor.albert.mvinet.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.immotor.albert.mvinet.net.ServiceGenerator
import com.immotor.albert.mvinet.net.download.DownloadService
import com.immotor.albert.mvinet.util.Network
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * 单例的一个Module
 * SingletonComponent和Singleton对应
 */
@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * 下载Service
     */
    @Singleton
    @Provides
    fun provideDownloadService(generator: ServiceGenerator) = generator.create(DownloadService::class.java)

    /**
     * 单例
     * 由provides提供依赖
     * 提供协程线程
     */
    @Singleton
    @Provides
    fun provideCoroutineContext(): CoroutineContext = Dispatchers.IO

    /**
     * 提供网络连接判断
     * @ApplicationContext 是一种@Qualifier修饰器实现，用于区分Context和ApplicationContext
     */
    @Singleton
    @Provides
    fun provideNetworkConnectivity(@ApplicationContext context: Context) = Network(context)
}