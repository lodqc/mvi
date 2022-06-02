package com.immotor.albert.mvinet.net.download

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.elvishew.xlog.XLog
import com.immotor.albert.mvinet.NetInitializer
import com.immotor.albert.mvinet.di.getCoroutineContext
import com.immotor.albert.mvinet.di.getError
import com.immotor.albert.mvinet.di.getService
import com.immotor.albert.mvinet.di.getTaskManager
import com.immotor.albert.mvinet.data.NetState
import com.immotor.albert.mvinet.net.ERROR_DOWNLOAD
import com.immotor.albert.mvinet.util.FileUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import okio.ByteString.Companion.encodeUtf8
import retrofit2.Response
import java.io.*
import java.util.*

class DownloadTask(
    coroutineScope: CoroutineScope, // 协程作用域
    val url: String, // 下载链接
    liveData: MutableLiveData<NetState<Uri>>? = null, // 监听器
    var saveName: String = "", // 文件名 支持只传一个后缀名 e.g .jpg
    var savePath: String = "" // 文件目录，Q版本下Environment.DIRECTORY_DCIM等等
) {
    private var completedSize: Long = 0 // 已完成大小
    private var contentLength: Long = 0 // 总大小
    private lateinit var tempFileName: String // 临时文件
    private var sandbox = false  // 是否沙盒
    private lateinit var uri: Uri // 文件uri
    private var time = System.currentTimeMillis() // 发射时间
    private val interval = 200L // 发射间隔时间
    private var download: Job

    init {
        download = coroutineScope.launch {
            // 开始
            liveData?.value = NetState.Start()
            flow {
                // 下载前准备
                fetchName()
                completedSize = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && !sandbox) {
                    fetchCompletedSizeQ()
                } else {
                    fetchCompletedSize()
                }
                // 构建响应数据,建议下载文件一边读一边写，不建议response.body().bytes()一次性加载
                val service = getService()
                // 请求并保存
                val data = service.download(url, mapOf("Range" to "bytes=$completedSize-"))
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && !sandbox) {
                    packResponseQ(data, this)
                } else {
                    packResponse(data, this)
                }
                emit(NetState.Success(data = uri))
                getTaskManager().remove(this@DownloadTask)
            }
                .flowOn(getCoroutineContext())
                .cancellable()
                .catch {
                    // 异常
                    XLog.e("Exception", it)
                    val error = getError().getError(ERROR_DOWNLOAD)
                    liveData?.value = NetState.Error(error = error)
                }
                .onCompletion {
                    getTaskManager().remove(this@DownloadTask)
                    // 结束
                    liveData?.value = NetState.Complete()
                }
                .collect {
                    // 结果
                    liveData?.value = it
                }
        }
    }

    /**
     * 取消
     */
    fun cancel() {
        download.cancel()
    }

    private suspend fun packResponse(
        res: Response<ResponseBody>,
        flow: FlowCollector<NetState<Uri>>
    ) {
        res.body() ?: throw IllegalArgumentException("responseBody is empty")
        res.body()?.let {
            // 数据长度
            contentLength = it.contentLength()
            val file = File(savePath, tempFileName)
            val outputStream = FileOutputStream(file, isAppend(res))
            val inputStream = it.byteStream()
            val bis = BufferedInputStream(inputStream)
            saveFile(outputStream, inputStream, bis, flow)
        }
    }

    private fun isAppend(res: Response<ResponseBody>): Boolean {
        var append = true
        XLog.e("临时文件地址: $savePath${File.separator}$tempFileName")
        //服务器不支持断点下载时重新下载
        if (res.headers()["Content-Range"].isNullOrEmpty()) {
            // 服务器不支持断点续传
            completedSize = 0
            append = false
        }
        return append
    }

    /**
     * 下载文件
     */
    private suspend fun saveFile(
        outputStream: FileOutputStream,
        inputStream: InputStream,
        bis: BufferedInputStream,
        flow: FlowCollector<NetState<Uri>>
    ) {
        try {
            var length: Int
            val buffer = ByteArray(2048)
            while (bis.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
                completedSize += length.toLong()
                progress(flow)
            }
            progress(flow, true)
            // 文件路径
            var filePath = savePath + File.separator + saveName
            //下载完成
            var newFile = File(filePath)
            //处理文件已存在逻辑
            if (newFile.exists() && newFile.isFile) {
                var suffix = ""
                if (saveName.contains(".")) {
                    suffix = saveName.substring(saveName.lastIndexOf("."))
                }
                saveName = getRandomString() + suffix
                filePath = savePath + File.separator + saveName
                newFile = File(filePath)
            }
            XLog.e("文件保存地址: $filePath")
            val oldFile = File(savePath + File.separator + tempFileName)
            if (oldFile.exists() && oldFile.isFile) {
                val rename = oldFile.renameTo(newFile)
                XLog.e("重命名: ${newFile.absolutePath}")
                uri = Uri.fromFile(newFile)
            }
        } finally {
            try {
                bis.close()
                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun packResponseQ(
        res: Response<ResponseBody>,
        flow: FlowCollector<NetState<Uri>>
    ) {
        res.body() ?: throw IllegalArgumentException("responseBody is empty")
        res.body()?.let {
            // 数据长度
            contentLength = it.contentLength()
            val inputStream = it.byteStream()
            val bis = BufferedInputStream(inputStream)
            NetInitializer.context.contentResolver.openOutputStream(
                uri,
                if (isAppend(res)) "wa" else "w"
            )
                ?.let {
                    saveFileQ(it, inputStream, bis, flow)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun saveFileQ(
        outputStream: OutputStream,
        inputStream: InputStream,
        bis: BufferedInputStream,
        flow: FlowCollector<NetState<Uri>>
    ) {
        try {
            var length: Int
            val buffer = ByteArray(2048)
            while (bis.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
                completedSize += length.toLong()
                progress(flow)
            }
            progress(flow, true)
            val u = query(saveName)
            if (u != null) {
                // 已存在同名文件，改名
                var suffix = ""
                if (saveName.contains(".")) {
                    suffix = saveName.substring(saveName.lastIndexOf("."))
                }
                saveName = getRandomString() + suffix
            }
            XLog.e("重命名: $savePath$saveName")
            ContentValues().run {
                put(MediaStore.MediaColumns.DISPLAY_NAME, saveName)
                // 更新文件名
                NetInitializer.context.contentResolver.update(
                    uri,
                    this,
                    "relative_path=? AND _display_name=?",
                    arrayOf(savePath, tempFileName)
                )
            }
        } finally {
            try {
                bis.close()
                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun progress(
        flow: FlowCollector<NetState<Uri>>,
        force: Boolean = false/*强制更新一次，让100%正常显示*/
    ) {
        if (System.currentTimeMillis() - time >= interval || force) {
            time = System.currentTimeMillis()
            val percent = (completedSize.toFloat()) / contentLength
            flow.emit(value = NetState.Progress(percent = percent))
        }
    }

    /**
     * 获取断点文件已完成的节点
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun fetchCompletedSizeQ(): Long {
        var uri = query(tempFileName)
        // 通过查找，要插入的Uri已经存在，就无需再次插入
        // 否则会出现新插入的文件，文件名被系统更改的现象，因为insert不会执行覆盖操作
        if (uri == null) {
            ContentValues().run {
                put(MediaStore.MediaColumns.RELATIVE_PATH, savePath) //下载到指定目录
                put(MediaStore.MediaColumns.DISPLAY_NAME, tempFileName)   //文件名
                //取contentType响应头作为文件类型
//            put(MediaStore.MediaColumns.MIME_TYPE, response.body?.contentType().toString())
                uri = NetInitializer.context.contentResolver.insert(getInsertUri(), this)
                //当相同路径下的文件，在文件管理器中被手动删除时，就会插入失败
            }
            uri ?: throw NullPointerException("Uri insert failed. Try changing filename")
        }

        uri?.run {
            this@DownloadTask.uri = this
            try {
                if (scheme == ContentResolver.SCHEME_FILE) {
                    return File(path).length()
                } else {
                    return NetInitializer.context.contentResolver.openFileDescriptor(
                        this,
                        "r"
                    )?.statSize
                        ?: 0L
                }
            } catch (e: FileNotFoundException) {
                return 0L
            }
        }

        return 0L
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun query(name: String): Uri? {
        savePath = savePath.let {
            //Remove the prefix slash if it exists /DCIM -> DCIM
            if (it.startsWith("/")) it.substring(1) else it
        }.let {
            //Suffix adds a slash if it does not exist DCIM -> DCIM/
            if (it.endsWith("/")) it else "$it/"
        }
        val columnNames = arrayOf(
            MediaStore.MediaColumns._ID,
        )
        return NetInitializer.context.contentResolver.query(
            getInsertUri(), columnNames,
            "relative_path=? AND _display_name=?", arrayOf(savePath, name), null
        )?.use {
            if (it.moveToFirst()) {
                val uriId = it.getLong(0)
                ContentUris.withAppendedId(getInsertUri(), uriId)
            } else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getInsertUri() = MediaStore.Downloads.EXTERNAL_CONTENT_URI

    /**
     * 获取断点文件已完成的节点
     */
    private fun fetchCompletedSize(): Long {
        mkdirNotExist(savePath)
        val file = File(savePath, tempFileName)
        if (file.exists() && file.isFile) {
            val size = file.length()
            XLog.e("断点文件下载，临时文件名[$tempFileName] 节点[$size]")
            return size
        }
        return 0L
    }

    /**
     * 获取名称
     */
    private fun fetchName(): Boolean {
        //自定义名称为空时采用文件原名称
        if (saveName.isEmpty() || saveName.startsWith(".")) {
            var link = url
            if (link.endsWith("/")) {
                // 移除末尾的"/"
                link = link.removeSuffix("/")
            }
            if (url.contains("/")) {
                //文件原名称
                val tempName = link.substring(link.lastIndexOf("/") + 1)
                if (tempName.contains(".")) {
                    saveName = tempName
                } else {// 有可能是单纯的没有后缀
                    // 拼接传入的后缀
                    saveName = tempName + saveName
                }
            }
        }
        // 自定义path为空时使用默认path
        if (savePath.isEmpty()) {
            if (FileUtil.existSDCard()) {
                savePath =
                    NetInitializer.context.getExternalFilesDir("download")?.absolutePath ?: ""
                sandbox = true
            } else {
                savePath = NetInitializer.context.filesDir.absolutePath
            }
        }
        // 非空验证
        require(savePath.isNotEmpty()) { "saveFileDir is empty" }
        require(saveName.isNotEmpty()) { "saveFileName is empty" }
        tempFileName = url
        try {
            tempFileName = url.encodeUtf8().md5().hex().lowercase()
        } catch (e: Exception) {
            e.printStackTrace()
            XLog.e("断点文件下载: 文件名MD5加密失败 ${e.message}")
        }
        tempFileName += ".temp"
        return sandbox
    }

    private fun mkdirNotExist(dir: String): Boolean {
        val file = File(dir)
        return file.exists() || file.mkdirs()
    }

    /**
     * 获得随机字符串
     */
    private fun getRandomString(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString().replace("-", "")
    }
}