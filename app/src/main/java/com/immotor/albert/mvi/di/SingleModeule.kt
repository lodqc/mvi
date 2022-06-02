package com.immotor.albert.mvi.di

import com.immotor.albert.mvi.BuildConfig
import com.immotor.albert.mvinet.net.NetBuilder
import com.immotor.albert.mvinet.net.ServiceGenerator
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingleModeule {
//    @Singleton  // 唯一实例
//    @Provides  // 通过 @Provides注入， 还可以通过@Binds，使用场景不同
//    fun provideServiceGenerator(moshi: Moshi) = NetBuilder().isDebug(BuildConfig.DEBUG)
//        .setBaseUrl("https://t-saas-exchange-battery.ehuandian.net").build(moshi)

//    @Singleton  // 唯一实例
//    @Provides  // 通过 @Provides注入， 还可以通过@Binds，使用场景不同
//    fun provideServiceGenerator(moshi: Moshi) = NetBuilder().isDebug(BuildConfig.DEBUG)
//        .setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdXJyZW50VGltZSI6MTY1Mzk4MDA1MDE2OSwicGxhdElkIjoiMSIsImxldmVsIjoiMiIsImlzcyI6ImF1dGgwIiwidGVuYW50SWQiOiI2MjdCNUM0NzY5MUEzOTNBQTgyRkNFODAiLCJtSWQiOiI1MWQxNGNkNGZjYzE0NmZhYjRhZWYxMDVkNmQ5YzU2MSIsInVzZXJUeXBlIjoiMSIsInVzZXJOYW1lIjoiMTczMjIzNTY5MzEiLCJleHAiOjE2NTQwMjMyNTAsInN1cGVyQWRtaW4iOiIwIn0.7w5b7CiCedyBWgomENTzwxcz_M9L88PZ_ENfR9FAoZI")
//        .setBaseUrl("https://t-saas-manage.ehuandian.net").build(moshi)

    @Singleton  // 唯一实例
    @Provides  // 通过 @Provides注入， 还可以通过@Binds，使用场景不同
    fun provideServiceGenerator(moshi: Moshi) = NetBuilder().isDebug(BuildConfig.DEBUG).isResultFormat(false)
        .setBaseUrl("https://api.github.com").build(moshi)
}