package com.example.downloadvideoandplay.design

import android.net.Uri
import android.os.Bundle


import androidx.appcompat.app.AppCompatActivity
import com.example.downloadvideoandplay.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView





class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var exoplayer:Player
    private lateinit var playerView:PlayerView
    private lateinit var url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        playerView=findViewById(R.id.playerView)
        url = intent.getStringExtra("VIDEO_URI").toString()
        initializePlayer()


//        videoPlayer = findViewById(R.id.VideoView)
//        val mController = MediaController(this)
//        mController.setAnchorView(videoPlayer)
//
//        val uri = Uri.parse(url)
//        if (!url.isNullOrBlank()){
//            Log.e("intent", "onCreate: $uri", )
//            videoPlayer.setMediaController(mController)
//            videoPlayer.setVideoURI(uri)
//            videoPlayer.requestFocus()
//            videoPlayer.start()
//        }
    }

    private fun initializePlayer() {
        // Initialize ExoPlayer
        exoplayer = SimpleExoPlayer.Builder(this)
            .build()


        // Set the exoPlayer to the playerView
        playerView.player=exoplayer

        // Create a MediaItem
        val mediaItem = createMediaItem()

        exoplayer.addMediaItem(mediaItem)
        exoplayer.prepare()
        exoplayer.play()
    }

    private fun createMediaItem(): MediaItem {
        val mediaUri = Uri.parse(url)
        return MediaItem.fromUri(mediaUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoplayer.stop()
        exoplayer.release()
    }


}