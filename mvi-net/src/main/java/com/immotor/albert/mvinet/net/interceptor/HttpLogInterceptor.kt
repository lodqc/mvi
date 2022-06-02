package com.immotor.albert.mvinet.net.interceptor


import kotlin.Throws
import com.elvishew.xlog.XLog
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

class HttpLogInterceptor(debug: Boolean) : Interceptor {
    private var level = if (debug) Level.BODY else Level.NONE
    val tag = "HttpLog"
    private val showMessage = StringBuilder()

    enum class Level {
        NONE,  //不打印log
        BASIC,  //只打印 请求首行 和 响应首行、请求的header+body、返回的body数据
        HEADERS,  //打印请求和响应的所有 Header
        BODY //所有数据全部打印
    }

    private fun log(message: String): String {
        // 请求或者响应开始
        if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
            showMessage.setLength(0)
        }
        showMessage.append(
            message.trimIndent()
        )
        // 响应结束，打印整条日志
        return if (message.startsWith("<-- END HTTP")) {
            showMessage.toString()
        } else showMessage.toString()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        XLog.tag(tag).e("MyInterceptor HttpLogInterceptor " + chain.request().url)
        val request: Request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        val startNs = System.nanoTime()
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            XLog.tag(tag).e(e.toString())
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    @Throws(IOException::class)
    private fun logForRequest(request: Request, connection: Connection?) {
        val logBody = level == Level.BODY || level == Level.BASIC
        val logHeaders = level == Level.BODY || level == Level.HEADERS || level == Level.BASIC
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1
        val sb = StringBuffer()
        val reqBodyJson: String
        try {
            val requestStartMessage = "--> " + request.method + ' ' + request.url + ' ' + protocol
            sb.append(log(requestStartMessage))
            if (logHeaders) {
                val headers = request.headers
                var i = 0
                val count = headers.size
                while (i < count) {
                    sb.append(
                        """
    
    ${headers.name(i)}: ${headers.value(i)}
    """.trimIndent()
                    )
                    i++
                }
            }
            if (logBody && hasRequestBody) {
                if (isPlaintext(requestBody!!.contentType())) {
                    sb.append(
                        """
    
    Content-Type: ${requestBody.contentType()}
    """.trimIndent()
                    )
                    reqBodyJson = bodyToString(request)
                    sb.append("\nbody: $reqBodyJson")
                } else {
                    sb.append("\nbody: maybe [file part] , too large too print , ignored!")
                }
            }
        } catch (e: Exception) {
            XLog.tag(tag).e(e.toString())
        } finally {
            sb.append(
                """
    
    --> END ${request.method}
    """.trimIndent()
            )
            XLog.tag(tag).e(sb.toString())
        }
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        val builder: Response.Builder = response.newBuilder()
        val clone: Response = builder.build()
        var responseBody = clone.body
        val logBody = level == Level.BODY || level == Level.BASIC
        val logHeaders = level == Level.BODY || level == Level.HEADERS
        val sb = StringBuffer()
        try {
            sb.append("<-- " + clone.code + ' ' + clone.message + ' ' + clone.request.url + " (" + tookMs + "ms）")
            if (logHeaders) {
                val headers = clone.headers
                var i = 0
                val count = headers.size
                while (i < count) {
                    sb.append(
                        """
    
    ${headers.name(i)}: ${headers.value(i)}
    """.trimIndent()
                    )
                    i++
                }
            }
            if (logBody && clone.promisesBody()) {
                if (responseBody?.contentType() != null && isPlaintext(
                        responseBody.contentType()
                    )
                ) {
                    val body = responseBody.string()
                    sb.append("\nbody:$body")
                    responseBody = body.toResponseBody(responseBody.contentType())
                    return response.newBuilder().body(responseBody).build()
                } else {
                    sb.append("\nbody: maybe [file part] , too large too print , ignored!")
                }
            }
        } catch (e: Exception) {
            XLog.tag(tag).e(e.toString())
        } finally {
            sb.append("\n<-- END HTTP")
            XLog.tag(tag).e(sb.toString())
        }
        return response
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType?.type == "text") {
            return true
        }
        var subtype = mediaType?.subtype
        subtype = subtype?.lowercase(Locale.getDefault())
        subtype?.let {
            if (subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("html")
            ) return true
        }
        return false
    }

    private fun bodyToString(request: Request): String {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            val requestBody = copy.body
            if (requestBody != null) {
                requestBody.writeTo(buffer)
                var charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                if (charset != null) {
                    return URLDecoder.decode(buffer.readString(charset), UTF8.name())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }
}