package com.immotor.albert.mvi

import android.app.Application
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TestApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // 初始化XLog
        val build = LogConfiguration.Builder()
            .logLevel(LogLevel.ALL)
            .tag("fq")
        // release不显示
        if (!BuildConfig.DEBUG) {
            build.addInterceptor(
                BlacklistTagsFilterInterceptor(    // 添加黑名单 TAG 过滤器
                    "fq"
                )
            )
        }
        XLog.init(build.build())
    }
}