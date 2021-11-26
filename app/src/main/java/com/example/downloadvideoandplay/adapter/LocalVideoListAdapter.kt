package com.example.downloadvideoandplay

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadvideoandplay.design.VideoPlayerActivity
import java.io.File

class LocalVideoListAdapter(
    private val context: Context,
    private val localVideoList: ArrayList<File>,
    private val statusName: ArrayList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemlist_video, parent, false)
        return LocalVideoListHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder1 = holder as LocalVideoListHolder

        val path = localVideoList[position].toString()
        holder1.txtPath.text = " ( ${position + 1} )   $path"

        if (statusName.size > 0)
            for (i in 0 until statusName.size) {
                if (statusName[i].toString() == localVideoList[position].toString()) {
                    Log.i(
                        "TAG",
                        "onBindViewHolder: Compare -> ${statusName[i].toString()} == ${localVideoList[position].toString()}"
                    )
                    holder1.txtPathStatus.text = "STATUS_RUNNING"
                    break
                } else {
                    Log.i("TAG", "onBindViewHolder: Compare -> fail}")
                    if (i == statusName.size - 1)
                        holder1.txtPathStatus.text = "STATUS_SUCCESSFUL"
                }
            }
        else
            holder1.txtPathStatus.text = "STATUS_SUCCESSFUL"

        holder1.clParent.setOnClickListener {
            Log.e("path", "onBindViewHolder: $path")
            val moveToPlayVideo = Intent(context, VideoPlayerActivity::class.java)
            moveToPlayVideo.putExtra("VIDEO_URI", path)
            context.startActivity(moveToPlayVideo)
        }
    }

    override fun getItemCount(): Int {
        return localVideoList.size
    }

    inner class LocalVideoListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtPath: TextView = itemView.findViewById(R.id.txtPath)
        val txtPathStatus: TextView = itemView.findViewById(R.id.txtPathStatus)
        val clParent: LinearLayoutCompat = itemView.findViewById(R.id.clParent)
    }
}