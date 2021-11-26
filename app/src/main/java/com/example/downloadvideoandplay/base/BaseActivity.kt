package com.example.downloadvideoandplay.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.downloadvideoandplay.PrefUtils

open class BaseActivity : AppCompatActivity() {

    open lateinit var objSharedPref: PrefUtils
    var TAG = "tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        objSharedPref = PrefUtils(this@BaseActivity)
        TAG = this.javaClass.simpleName
    }

}