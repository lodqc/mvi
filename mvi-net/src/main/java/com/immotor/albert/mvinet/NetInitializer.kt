package com.immotor.albert.mvinet

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

class NetInitializer : Initializer<Unit> {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        private fun setContextInstance(context: Context) {
            Companion.context = context
        }
    }
    override fun create(context: Context) {
        setContextInstance(context)
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()
}