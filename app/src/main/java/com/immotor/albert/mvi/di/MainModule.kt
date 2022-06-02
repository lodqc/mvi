package com.immotor.albert.mvi.di


import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.immotor.albert.mvi.data.entity.Repo
import com.immotor.albert.mvi.data.remote.MainRemoteData
import com.immotor.albert.mvi.data.remote.MainService
import com.immotor.albert.mvi.data.remote.RepoPagingSource
import com.immotor.albert.mvi.data.repository.MainRepository
import com.immotor.albert.mvinet.net.ServiceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlin.coroutines.CoroutineContext

/**
 * ViewModelComponent和ViewModelScoped对应
 */
@Module
@InstallIn(ViewModelComponent::class)
class MainModule {

    /**
     * 生成MainService实例，生命周期为viewModel
     */
    @ViewModelScoped
    @Provides
    fun provideService(generator: ServiceGenerator) = generator.create(MainService::class.java)

    /**
     * ImageRemoteData实例，提供远程数据
     */
    @ViewModelScoped
    @Provides
    fun provideRemoteData(service: MainService, pager: Pager<Int, Repo>) =
        MainRemoteData(service, pager)

    /**
     * ImageRepository包括了远程以及本来数据的操作，通过他来和viewModel对接
     */
    @ViewModelScoped
    @Provides
    fun provideRepository(remoteData: MainRemoteData, io: CoroutineContext) =
        MainRepository(remoteData, io)

    @ViewModelScoped
    @Provides
    fun providePager(service: MainService) =
        Pager(
            config = PagingConfig(10),
            pagingSourceFactory = { RepoPagingSource(service) }
        )
}