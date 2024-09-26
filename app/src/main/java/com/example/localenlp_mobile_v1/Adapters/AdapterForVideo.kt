package com.example.localenlp_mobile_v1.Adapters

import android.content.Context
import android.media.MediaPlayer
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

class AdapterForVideo(
    private val context: Context,
    private val listOfString: ArrayList<String>
) : RecyclerView.Adapter<AdapterForVideo.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_vedio, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val videoUri = Uri.parse(listOfString[position])

        // Log the URI for debugging
        println("Playing video from URI: $videoUri")

        holder.video.setVideoURI(videoUri)

        val mediaController = MediaController(context)
        holder.video.setMediaController(mediaController)
        mediaController.setAnchorView(holder.video)

        holder.video.setOnPreparedListener {
            it.start() // Automatically start the video when ready
        }

        holder.video.setOnErrorListener { mp, what, extra ->
            val errorMessage = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown media error"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Media server died"
                MediaPlayer.MEDIA_ERROR_IO -> "File or network related operation failed"
                MediaPlayer.MEDIA_ERROR_MALFORMED -> "Bitstream is malformed"
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported media"
                else -> "Error playing video"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            true // Indicates the error was handled
        }

        holder.deleteButton.setOnClickListener {
            // TODO: Implement delete logic
            Toast.makeText(context, "Delete button clicked", Toast.LENGTH_SHORT).show()
        }

        holder.updateButton.setOnClickListener {
            // TODO: Implement update logic
            Toast.makeText(context, "Update button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = listOfString.size

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val video: VideoView = itemView.findViewById(R.id.videoView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletVedio)
        val updateButton: ImageView = itemView.findViewById(R.id.updateVedio)
    }
}
