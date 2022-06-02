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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.immotor.albert.mvinet.data.BaseResponse
import com.immotor.albert.mvinet.net.ServerException
import okhttp3.ResponseBody
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import retrofit2.Converter
import java.io.IOException

/**
 * change: 修改convert方法
 */
class MoshiResponseBodyConverter<T>(
    private val adapter: JsonAdapter<T>,
    private val resultAdapter: JsonAdapter<BaseResponse<*>>?
) :
    Converter<ResponseBody, T?> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val source = value.source()
        return try {
            // Moshi has no document-level API so the responsibility of BOM skipping falls to whatever
            // is delegating to it. Since it's a UTF-8-only library as well we only honor the UTF-8 BOM.
            if (source.rangeEquals(
                    0,
                    UTF8_BOM
                )
            ) {
                // 跳过utf8-bom格式文件的bom头
                source.skip(UTF8_BOM.size.toLong())
            }
            val reader = JsonReader.of(source)
            // add by Wick, 解析套壳数据
            var result : T? = null
            resultAdapter?.run {
                // 不为空
                val rawResult = fromJson(reader)
                rawResult?.run {
                    //todo 处理服务端自定义异常并抛出
                    when (code) {
                        // e.g
                        101 -> {
                            // 101 异常
                            throw ServerException(this)
                        }
                        else -> {
                            // 获取data数据
                            result = this.result as T?
                        }
                    }
                }
            }
            // 为空直接解析
            resultAdapter ?: run {
                result = adapter.fromJson(reader)
            }
            if (reader.peek() != JsonReader.Token.END_DOCUMENT) {
                throw JsonDataException("JSON document was not fully consumed.")
            }
            result
        } finally {
            value.close()
        }
    }

    companion object {
        private val UTF8_BOM: ByteString = "EFBBBF".decodeHex()
    }
}