package com.immotor.albert.mvinet.util

import java.io.IOException
import java.io.InputStream
import java.lang.AssertionError
import java.lang.Exception
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*
import kotlin.Throws

/**
 * https校验工具
 * copy from https://github.com/hongyangAndroid/okhttputils/blob/master/okhttputils/src/main/java/com/zhy/http/okhttp/https/HttpsUtils.java
 */
object HttpsUtil {

    data class SSLParams(
        var sslSocketFactory: SSLSocketFactory,
        var trustManager: X509TrustManager
    )

    fun getSslSocketFactory(certificates: Array<InputStream>?): SSLParams {
        return getSslSocketFactory(certificates, null, null)
    }

    /**
     * @param certificates 证书
     * @param bksFile BKS证书库，由多个证书打包而成
     * @param password BKS证书库密码
     */
    private fun getSslSocketFactory(
        certificates: Array<InputStream>?,
        bksFile: InputStream?,
        password: String?
    ): SSLParams {
        return try {
            val trustManagers = certificates?.run {
                prepareTrustManager(*this)
            }
            val keyManagers = prepareKeyManager(bksFile, password)
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager = if (trustManagers == null) {
                UnSafeTrustManager()
            } else {
                MyTrustManager(chooseTrustManager(trustManagers))
            }
            sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
            SSLParams(sslContext.socketFactory, trustManager)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            e.printStackTrace()
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
            throw AssertionError(e)
        }
    }

    private fun prepareTrustManager(vararg certificates: InputStream): Array<TrustManager>? {
        if (certificates.isEmpty()) return null
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = (index++).toString()
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                try {
                    certificate.close()
                } catch (e: IOException) {
                }
            }
            val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            return trustManagerFactory.trustManagers
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
        try {
            if (bksFile == null || password == null) return null
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    class UnSafeHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            // 不对host进行验证
            return true
        }
    }

    private class UnSafeTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }

    private class MyTrustManager(localTrustManager: X509TrustManager?) : X509TrustManager {
        private val defaultTrustManager: X509TrustManager?
        private val localTrustManager: X509TrustManager?

        init {
            val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var4.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(var4.trustManagers)
            this.localTrustManager = localTrustManager
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager?.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager?.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }
}