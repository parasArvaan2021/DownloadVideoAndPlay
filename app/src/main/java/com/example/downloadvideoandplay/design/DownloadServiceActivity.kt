package com.example.downloadvideoandplay.design

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.downloadvideoandplay.PrefUtils
import com.example.downloadvideoandplay.R
import com.example.downloadvideoandplay.base.BaseActivity
import org.json.JSONArray

class DownloadServiceActivity : BaseActivity() {
    private val DOWNLOAD_ID = "PREF_DOWNLOAD_ID"
    private val videoUrl = "https://cdn.videvo.net/videvo_files/video/free/2017-12/large_watermarked/171124_B1_HD_001_preview.mp4"
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_service)
        val sharedPreferences = PrefUtils(this)
        findViewById<Button>(R.id.btnVideoStart).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                askPermissions()
            } else {
                startService(
                    Intent(this, DownloadService::class.java)
                        .putExtra("url", videoUrl)
                )
            }
        }

        findViewById<Button>(R.id.btnVideoCancel).setOnClickListener {
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            Log.e("TAG", "onCreate: ${sharedPreferences.getLong("DOWNLOAD_ID")}")
            val downloadFileList = JSONArray(sharedPreferences.getString(DOWNLOAD_ID))
            for (i in 0 until downloadFileList.length()) {
                downloadManager.remove(downloadFileList.optLong(i))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startService(
                        Intent(this, DownloadService::class.java)
                            .putExtra("url", videoUrl)
                    )
                } else {
                    askPermissions()
                }
                return
            }
        }
    }

    private fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
        } else {
            startService(
                Intent(this, DownloadService::class.java)
                    .putExtra("url", videoUrl)
            )
        }
    }

}