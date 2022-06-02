package com.immotor.albert.mvicore

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV

class CoreInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        MMKV.initialize(context)
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}