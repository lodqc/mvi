package com.immotor.albert.mvinet.net


import android.text.TextUtils
import com.immotor.albert.mvinet.net.converter.MoshiConverterFactory
import com.immotor.albert.mvinet.net.interceptor.HttpLogInterceptor
import com.immotor.albert.mvinet.util.HttpsUtil
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit

private const val TIMEOUT_READ = 30   // In seconds
private const val TIMEOUT_CONNECT = 30   // In seconds
private const val TIMEOUT_WRITE = 30   // In seconds
// .cer文件直接用文档打开复制
private const val CER = "-----BEGIN CERTIFICATE-----\n" +
        "MIIDXTCCAkWgAwIBAgIEdl4fbjANBgkqhkiG9w0BAQsFADBfMQswCQYDVQQGEwJD\n" +
        "TjEMMAoGA1UECBMDdGFvMQwwCgYDVQQHEwN0YW8xEDAOBgNVBAoTB3BpY3R1cmUx\n" +
        "EDAOBgNVBAsTB3BpY3R1cmUxEDAOBgNVBAMTB3BpY3R1cmUwHhcNMjIwNTEyMDU1\n" +
        "NzQzWhcNMzIwNTA5MDU1NzQzWjBfMQswCQYDVQQGEwJDTjEMMAoGA1UECBMDdGFv\n" +
        "MQwwCgYDVQQHEwN0YW8xEDAOBgNVBAoTB3BpY3R1cmUxEDAOBgNVBAsTB3BpY3R1\n" +
        "cmUxEDAOBgNVBAMTB3BpY3R1cmUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEK\n" +
        "AoIBAQC3TLHq6JKHkZ225vjcoHUT+BOyGgbRZg2jxOqrbevhzx3iz6/jiTEWjMIZ\n" +
        "c9PLInPP8fMNlZRm8L6tY7HxE9Va6h9+bIDOf2Le5LmFWDVPHZlLZr+sj15bTI/v\n" +
        "JCKXPs7OnKr13XE51zEW6eXzq2Un4VUwePXc/KHUnxosbq2x4SWiv1KoySMrXvHB\n" +
        "BjTbRuDxLSbGihWtlpdg7kBZ+MzrAqXwMpnUjcZr6WxDk7Db1Z8/lL6PlLArzzKB\n" +
        "oqDubuv0YbYf+PR10jEs55F8rTVnorw7g/e7J4GUEQX8y2S15OoCNk50Ib8l4OhJ\n" +
        "yPVGjCuPXPOPdve/ufqqH3VM3eqlAgMBAAGjITAfMB0GA1UdDgQWBBRMBw3iBXUl\n" +
        "1Xpaucd3igjx1z15XzANBgkqhkiG9w0BAQsFAAOCAQEArWdyh5PdKyOr3a0UoOPj\n" +
        "rt3oc3IwrJrb5V0rIWxJvZT9/lmpjTd2zntZXIiUeXPf3vQRJL/KmVKwlXIuuUAU\n" +
        "ymh/wGH89337iKXhNKpuziyCglLp7xgUp0j4rzImfQ/AbeRLEGbv6i+JP/ZflTHp\n" +
        "j3YWddMMT3rOF7QRhE3wKZNhx0uUJiTKJ7DVfXJ2UjGlLkgUa6PqRXnm3jXfN38g\n" +
        "OYQoa63jdnZZwTp+uODrBBUCSzRPsgXEBqI2KewTHSWITBtFCf3S0+q0QNSxsick\n" +
        "+2m4A7x0WMPh9ZFdVbcsmIW/wWC6aP0SnpF/ZHob/KFwXhKjgq+a6Gil8mIt9OmL\n" +
        "xA==\n" +
        "-----END CERTIFICATE-----"

/**
 * 提供Retrofit实例
 */
class ServiceGenerator(private val moshi: Moshi, netBuilder: NetBuilder) {
    private val client: OkHttpClient
    private lateinit var retrofit: Retrofit
    // 向请求中添加header
    private val tokenInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", netBuilder.token)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }
    init {
        // 传入公钥/证书
        val params = HttpsUtil.getSslSocketFactory(arrayOf(ByteArrayInputStream(CER.toByteArray())))
        // okhttp实例
        val builder = OkHttpClient.Builder()
            .addInterceptor(HttpLogInterceptor(netBuilder.isDebug))
        netBuilder.interceptors.forEach {
            builder.addInterceptor(it)
        }
        if(!TextUtils.isEmpty(netBuilder.token)){
            builder.addInterceptor(tokenInterceptor)
        }
        client = builder
            // 连接超时时间
            .connectTimeout(TIMEOUT_CONNECT.toLong(), TimeUnit.SECONDS)
            // 读取超时时间
            .readTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
            // 写超时
            .writeTimeout(TIMEOUT_WRITE.toLong(), TimeUnit.SECONDS)
            // SSL
            .sslSocketFactory(params.sslSocketFactory, params.trustManager)
            // 此处没有验证host，可以自定义HostnameVerifier验证
            .hostnameVerifier(HttpsUtil.UnSafeHostnameVerifier())
            .build()
        refresh(netBuilder.baseUrl)
    }

    fun refresh(baseUrl: String) {
        // retrofit实例
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * 创建service
     */
    fun <S> create(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}

class NetBuilder{
    val interceptors = mutableListOf<Interceptor>()
    var isDebug = true
    var baseUrl = ""
    var token = ""
    companion object{
        var isFormat = true
    }
    fun addInterceptor(interceptor: Interceptor): NetBuilder {
        interceptors.add(interceptor)
        return this
    }

    fun isDebug(isDebug: Boolean): NetBuilder {
        this.isDebug = isDebug
        return this
    }

    /**
     * 是否使用BaseResponse包裹数据
     */
    fun isResultFormat(isResultFormat: Boolean): NetBuilder {
        isFormat = isResultFormat
        return this
    }

    fun setBaseUrl(baseUrl: String): NetBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun setToken(token: String): NetBuilder {
        this.token = token
        return this
    }

    fun build(moshi: Moshi): ServiceGenerator {
        return ServiceGenerator(moshi,this)
    }
}