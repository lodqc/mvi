/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.immotor.albert.mvinet.net.converter

import com.immotor.albert.mvinet.BuildConfig
import com.squareup.moshi.*
import com.immotor.albert.mvinet.data.BaseResponse
import com.immotor.albert.mvinet.net.NetBuilder
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.*

/**
 * A [converter][Converter.Factory] which uses Moshi for JSON.
 *
 *
 * Because Moshi is so flexible in the types it supports, this converter assumes that it can
 * handle all types. If you are mixing JSON serialization with something else (such as protocol
 * buffers), you must [add this][Retrofit.Builder.addConverterFactory] last to allow the other converters a chance to see their types.
 *
 *
 * Any [@JsonQualifier][JsonQualifier]-annotated annotations on the parameter will be used
 * when looking up a request body converter and those on the method will be used when looking up a
 * response body converter.
 */
/**
 * change: 修改responseBodyConverter方法中获取的MoshiResponseBodyConverter
 * 从官方库中copy
 */
class MoshiConverterFactory private constructor(
    private val moshi: Moshi,
    private val lenient: Boolean,
    private val failOnUnknown: Boolean,
    private val serializeNulls: Boolean
) : Converter.Factory() {
    /** Return a new factory which uses [lenient][JsonAdapter.lenient] adapters.  */
    fun asLenient(): MoshiConverterFactory {
        return MoshiConverterFactory(moshi, true, failOnUnknown, serializeNulls)
    }

    /** Return a new factory which uses [JsonAdapter.failOnUnknown] adapters.  */
    fun failOnUnknown(): MoshiConverterFactory {
        return MoshiConverterFactory(moshi, lenient, true, serializeNulls)
    }

    /** Return a new factory which includes null values into the serialized JSON.  */
    fun withNullSerialization(): MoshiConverterFactory {
        return MoshiConverterFactory(moshi, lenient, failOnUnknown, true)
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        var adapter: JsonAdapter<*> =
            moshi.adapter<Any>(type, jsonAnnotations(annotations))
        if (lenient) {
            adapter = adapter.lenient()
        }
        if (failOnUnknown) {
            adapter = adapter.failOnUnknown()
        }
        if (serializeNulls) {
            adapter = adapter.serializeNulls()
        }

        // add by Wick, 提供NetResult套壳的JsonAdapter
        val resultAdapter: JsonAdapter<BaseResponse<*>>? = when (type.rawType) {
            // 下载文件
            ResponseBody::class -> null
            else ->  if (NetBuilder.isFormat) {
                // 服务端Result格式数据
                val newType = Types.newParameterizedType(BaseResponse::class.java, type)
                moshi.adapter(newType, jsonAnnotations(annotations))
            } else {
                null
            }
        }

        return MoshiResponseBodyConverter(adapter, resultAdapter)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        var adapter: JsonAdapter<*> =
            moshi.adapter<Any>(type, jsonAnnotations(parameterAnnotations))
        if (lenient) {
            adapter = adapter.lenient()
        }
        if (failOnUnknown) {
            adapter = adapter.failOnUnknown()
        }
        if (serializeNulls) {
            adapter = adapter.serializeNulls()
        }
        return MoshiRequestBodyConverter(adapter)
    }

    companion object {
        /** Create an instance using `moshi` for conversion.  */
        /** Create an instance using a default [Moshi] instance for conversion.  */
        @JvmOverloads  // Guarding public API nullability.
        fun create(moshi: Moshi? = Moshi.Builder().build()): MoshiConverterFactory {
            if (moshi == null) throw NullPointerException("moshi == null")
            return MoshiConverterFactory(moshi, false, false, false)
        }

        private fun jsonAnnotations(annotations: Array<Annotation>): Set<Annotation> {
            var result: MutableSet<Annotation>? = null
            for (annotation in annotations) {
                if (annotation.javaClass.isAnnotationPresent(JsonQualifier::class.java)) {
                    if (result == null) result = LinkedHashSet()
                    result.add(annotation)
                }
            }
            return if (result != null) Collections.unmodifiableSet(result) else emptySet<Annotation>()
        }
    }
}