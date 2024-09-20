package com.example.localenlp_mobile_v1.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localenlp_mobile_v1.ActivityOfDataImage
import com.example.localenlp_mobile_v1.DB.VideoDB
import com.example.localenlp_mobile_v1.R

class AdapterForVideo(private val context: Context, private val listOfString: ArrayList<String>) :
    RecyclerView.Adapter<AdapterForVideo.DataViewHolder>() {
    private val db: VideoDB = VideoDB(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterForVideo.DataViewHolder {
        // Inflate the layout for each item in the list (shape_of_image.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_vedio, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val dataItem = listOfString[position]
        val mc:MediaController = MediaController(context)
        holder.video.setMediaController(mc)
    }

    override fun getItemCount(): Int {
        return listOfString.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val video: VideoView = itemView.findViewById(R.id.videoView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletVedio)
        val updateButton: ImageView = itemView.findViewById(R.id.updateVedio)
    }
}