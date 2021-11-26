package com.example.downloadvideoandplay.design

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadvideoandplay.LocalVideoListAdapter
import com.example.downloadvideoandplay.R
import com.example.downloadvideoandplay.base.BaseActivity
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity() {
    var fileName: String? = null

    //    var urlVideo1 = "https://cdn.videvo.net/videvo_files/video/premium/video0287/large_watermarked/_Awards1_preview.mp4"
    private var urlVideo =
        "https://cdn.videvo.net/videvo_files/video/free/2017-12/large_watermarked/171124_B1_HD_001_preview.mp4"
    private lateinit var recyclerOfVideoList: RecyclerView

    private var downloadFileId: Long = 0
    private var downloadManager: DownloadManager? = null

    private val DOWNLOAD_ID = "PREF_DOWNLOAD_ID"
    var statusName = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnDownloadVideo: Button = findViewById(R.id.btnDownloadVideo)
        val btnPlayVideo: Button = findViewById(R.id.btnPlayVideo)
        recyclerOfVideoList = findViewById(R.id.recyclerView)
        recyclerOfVideoList.layoutManager = LinearLayoutManager(this)

        btnPlayVideo.visibility = View.GONE
        btnDownloadVideo.setOnClickListener {
            downloadVideo(urlVideo)
        }
        btnPlayVideo.setOnClickListener {
//            getDirectory("")
        }
//        checkDownloadFileStatus()
    }

    private fun getDirectory() {

        val gpath: String =
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/Demo"
        val fullPath = File(gpath + File.separator)
        getFileListDownloadDirectory(fullPath)
        Log.e("fullPath", "" + gpath + "\n" + fullPath)
    }

    private fun getFileListDownloadDirectory(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                fileList.add(currentFile.absoluteFile)
            }

            Log.e("FileList", "imageReaderNew: ${fileList.size} -> ${statusName.size}")

            recyclerOfVideoList.adapter = LocalVideoListAdapter(this, fileList, statusName)
        }

    }

    private fun checkDownloadFileStatus() {
        var downloadFileList = JSONArray()
        var downloadFileQuery: Query?
        var list: Long
        val tempArray = JSONArray()
        statusName = arrayListOf<String>()

        try {
            downloadFileList = JSONArray(objSharedPref.getString(DOWNLOAD_ID))

        } catch (e: JSONException) {

        }
        Log.e(TAG, "checkDownloadFileStatus: FileList:${downloadFileList.length()}")

        if (downloadFileList.length() > 0) {
            for (i in 0 until downloadFileList.length()) {
                list = downloadFileList[i].toString().toLong()
                downloadFileQuery = Query().setFilterById(list)
                Log.e(TAG, "checkDownloadFileStatus:list: $list")
                downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val downloadCursor: Cursor? = downloadManager?.query(downloadFileQuery)

                if (downloadCursor != null) {

                    if (downloadCursor.moveToFirst()) {
                        val columnIndex: Int = downloadCursor.getColumnIndex(COLUMN_STATUS)

                        val nameInt = downloadCursor.getColumnIndex(COLUMN_LOCAL_URI)
                        val downloadFileLocalUri: String = downloadCursor.getString(nameInt)
                        val mFile = File(Uri.parse(downloadFileLocalUri).path)
                        val downloadFilePath = mFile.absolutePath

                        Log.i(TAG, "checkDownloadFileStatus Name: $downloadFilePath")
                        statusName.add(downloadFilePath)

                        when (downloadCursor.getInt(columnIndex)) {
                            STATUS_FAILED -> {
                                Log.e("status", "checkDownloadFileStatus: Failed")
//                                statusName.add("STATUS_FAILED")

                            }
                            STATUS_PAUSED -> {
                                Log.e("status", "checkDownloadFileStatus: paused")
//                                statusName.add("STATUS_PAUSED")
                            }
                            STATUS_RUNNING -> {
                                Log.e("status", "checkDownloadFileStatus: running")
//                                statusName.add("STATUS_RUNNING")
                            }
                            STATUS_PENDING -> {
                                Log.e("status", "checkDownloadFileStatus: pending")
//                                statusName.add("STATUS_PENDING")
                            }
                            STATUS_SUCCESSFUL -> {
                                Log.e("status", "checkDownloadFileStatus: successful")
//                                statusName.add("STATUS_SUCCESSFUL")

                                try {
                                    Log.i(
                                        TAG,
                                        "checkDownloadFileStatus: $list == ${downloadFileList[i]}"
                                    )
                                    tempArray.put(i)

                                    Log.e(
                                        TAG,
                                        "checkDownloadFileStatus: FileList:${tempArray}"
                                    )
                                } catch (e: JSONException) {

                                }

                            }
                        }
                    }
                }
            }
            for (i in 0 until tempArray.length())
                downloadFileList.remove(tempArray.optInt(i))

            Log.e(
                TAG,
                "checkDownloadFileStatus: Final:${downloadFileList}"
            )

            objSharedPref.putString(DOWNLOAD_ID, downloadFileList.toString())
        }
        for (i in 0 until statusName.size)
            Log.e("$TAG=statusName", statusName[i].toString())
        getDirectory()
    }

    private fun downloadVideo(url: String) {

        fileName = url
        fileName = fileName?.substring(fileName?.lastIndexOf('/')!! + 1)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadVideo = Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Android Data download using DownloadManager.")
            .setNotificationVisibility(Request.VISIBILITY_VISIBLE)
            .setAllowedNetworkTypes(Request.NETWORK_WIFI or Request.NETWORK_MOBILE)
        downloadVideo.setDestinationInExternalFilesDir(
            this,
            Environment.DIRECTORY_DOWNLOADS, "/Demo/paras.mp4"
        )

        downloadVideo.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        downloadFileId = downloadManager!!.enqueue(downloadVideo)
        var array = JSONArray()

        try {
            array = JSONArray(objSharedPref.getString(DOWNLOAD_ID))

            Log.e(TAG, "checkDownloadFileStatus: FileList:${array.length()}")
        } catch (e: JSONException) {

        }

        array.put(downloadFileId)
        objSharedPref.putString(DOWNLOAD_ID, array.toString())
        Log.e(TAG, "downloadVideo: $array")

        val downloadCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    if (intent.action == ACTION_DOWNLOAD_COMPLETE) {

                        Toast.makeText(
                            this@MainActivity,
                            "Download Complete...",
                            Toast.LENGTH_SHORT
                        ).show()
                        val path =
                            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/Demo/$fileName")
                        Log.e("path", "onReceive: $path")
                        checkDownloadFileStatus()
                    }
                }
            }
        }
        registerReceiver(
            downloadCompleteReceiver,
            IntentFilter(ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onResume() {
        super.onResume()
        checkDownloadFileStatus()
    }
}

