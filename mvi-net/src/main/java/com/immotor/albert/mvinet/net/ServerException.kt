/*
 * Copyright (C) 2016 Square, Inc.
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
package com.immotor.albert.mvinet.net

import com.immotor.albert.mvinet.data.BaseResponse
import java.util.*

/** 服务器异常  */
//transient关键字标记的成员变量不参与序列化过程
class ServerException(@Transient private val result: BaseResponse<*>) : RuntimeException(getMessage(result)) {
    private val code: Int = result.code
    override val message: String = result.msg

    /** HTTP status code.  */
    fun code(): Int {
        return code
    }

    /** HTTP status message.  */
    fun message(): String {
        return message
    }

    /** The full HTTP response. This may be null if the exception was serialized.  */
    fun result(): BaseResponse<*> {
        return result
    }

    companion object {
        private fun getMessage(result: BaseResponse<*>): String {
            Objects.requireNonNull(result, "response == null")
            return "HTTP " + result.code + " " + result.msg
        }
    }

}