package com.example.localenlp_mobile_v1.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.R

class AdapterForVideo(private val context: Context, private val listOfString: ArrayList<String>) :
    RecyclerView.Adapter<AdapterForVideo.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_vedio, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val videoUri = Uri.parse(listOfString[position])
        holder.video.setVideoURI(videoUri)

        val mc = MediaController(context)
        holder.video.setMediaController(mc)
        mc.setAnchorView(holder.video)

        holder.video.setOnPreparedListener {
            it.start() // Automatically start the video when ready
        }

        holder.video.setOnErrorListener { mp, what, extra ->
            Toast.makeText(context, "Error playing video", Toast.LENGTH_SHORT).show()
            true // Indicates the error was handled
        }

        // Set OnClickListeners for delete and update buttons if needed
        holder.deleteButton.setOnClickListener {
            // Handle delete action
        }

        holder.updateButton.setOnClickListener {
            // Handle update action
        }
    }


    override fun getItemCount(): Int = listOfString.size

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val video: VideoView = itemView.findViewById(R.id.videoView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletVedio)
        val updateButton: ImageView = itemView.findViewById(R.id.updateVedio)
    }


}
