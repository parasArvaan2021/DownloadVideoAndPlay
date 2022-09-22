package com.example.downloadvideoandplay.design


import android.app.DownloadManager
import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.downloadvideoandplay.PrefUtils
import org.json.JSONArray
import org.json.JSONException
import java.io.*


class DownloadService : Service() {
    private var downloadManager: DownloadManager? = null
    var fileName: String? = null
    private var downloadFileId: Long = 0
    lateinit var sharedPreferences: PrefUtils
    private val DOWNLOAD_ID = "PREF_DOWNLOAD_ID"

    val handler = Handler(Looper.getMainLooper())
    var isStatusComplete = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PrefUtils(this)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            val path =
                File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath.toString() + "/Demo/")
            if (!path.exists()) {
                path.mkdir()
            }
            downloadVideo(intent.getStringExtra("url").toString(), path.toString())
//            downloadFile(intent.getStringExtra("url").toString(),path.absolutePath)
        }


        handler.postDelayed(object : Runnable {
            override fun run() {
                checkDownloadFileStatus()
                if (!isStatusComplete)
                    handler.postDelayed(this, 100)
            }
        }, 100)

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    private fun downloadVideo(url: String, path: String) {
        fileName = url
        fileName = fileName?.substring(fileName?.lastIndexOf('/')!! + 1)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadVideo = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Android Data download using DownloadManager.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setVisibleInDownloadsUi(false)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setDestinationInExternalFilesDir(
                this,
                ContextCompat.getExternalFilesDirs(this,Environment.DIRECTORY_DOWNLOADS).toString(),
                "$fileName"
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadFileId = downloadManager!!.enqueue(downloadVideo)
        val array = JSONArray()
        array.put(downloadFileId)
        sharedPreferences.putString(DOWNLOAD_ID, array.toString())
    }

    private fun checkDownloadFileStatus() {
        var downloadFileList = JSONArray()
        var downloadFileQuery: DownloadManager.Query?
        var list: Long
        val tempArray = JSONArray()

        try {
            downloadFileList = JSONArray(sharedPreferences.getString(DOWNLOAD_ID))

        } catch (e: JSONException) {

        }
        Log.e("TAG", "DownloadService: FileList:${downloadFileList.length()}")

        if (downloadFileList.length() > 0) {
            for (i in 0 until downloadFileList.length()) {
                list = downloadFileList[i].toString().toLong()
                downloadFileQuery = DownloadManager.Query().setFilterById(list)
                Log.e("TAG", "DownloadService:list: $list")
                downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val downloadCursor: Cursor? = downloadManager?.query(downloadFileQuery)

                if (downloadCursor != null) {

                    if (downloadCursor.moveToFirst()) {
                        val columnIndex: Int =
                            downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

//                        val nameInt = downloadCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
//                        val downloadFileLocalUri: String = downloadCursor.getString(nameInt)
//                        val mFile = File(Uri.parse(downloadFileLocalUri).path.toString())
//                        val downloadFilePath = mFile.absolutePath

//                        Log.i("TAG", "service Name: $downloadFilePath")

                        when (downloadCursor.getInt(columnIndex)) {
                            DownloadManager.STATUS_FAILED -> {
                                isStatusComplete = true
                                Log.e("status", "DownloadService: Failed")
                            }
                            DownloadManager.STATUS_PAUSED -> {
                                Log.e("status", "DownloadService: paused")
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                Log.e("status", "DownloadService: running")
                            }
                            DownloadManager.STATUS_PENDING -> {
                                Log.e("status", "DownloadService: pending")

                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Log.e("status", "DownloadService: successful")

                                try {
                                    Log.i(
                                        "TAG",
                                        "DownloadService: $list == ${downloadFileList[i]}"
                                    )
                                    tempArray.put(i)

                                    Log.e(
                                        "TAG",
                                        "DownloadService: FileList:${tempArray}"
                                    )
                                } catch (e: JSONException) {

                                }
                                isStatusComplete = true
                                stopSelf()
                            }
                        }
                    }
                }
            }
            for (i in 0 until tempArray.length())
                downloadFileList.remove(tempArray.optInt(i))

            Log.e(
                "TAG",
                "DownloadService: Final:${downloadFileList}"
            )
            sharedPreferences.putString(DOWNLOAD_ID, downloadFileList.toString())
        }
    }
}