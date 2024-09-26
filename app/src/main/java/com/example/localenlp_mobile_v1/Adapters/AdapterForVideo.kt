package com.example.localenlp_mobile_v1.Adapters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.DB.VideoDB
import com.example.localenlp_mobile_v1.R

class AdapterForVideo(
    private val context: Context,
    private val listOfString: ArrayList<String>,
    private val videoPickerLauncher: ActivityResultLauncher<Intent>,
    private val onDeleteVideo: (String) -> Unit // Callback function for deleting video
) : RecyclerView.Adapter<AdapterForVideo.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_vedio, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val videoUri = Uri.parse(listOfString[position])

        holder.video.setVideoURI(videoUri)

        val mediaController = MediaController(context)
        holder.video.setMediaController(mediaController)
        mediaController.setAnchorView(holder.video)

        holder.video.setOnPreparedListener {
            it.start()
        }

        holder.video.setOnErrorListener { mp, what, extra ->
            val errorMessage = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown media error"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Media server died"
                MediaPlayer.MEDIA_ERROR_IO -> "File or network-related operation failed"
                MediaPlayer.MEDIA_ERROR_MALFORMED -> "Bitstream is malformed"
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported media"
                else -> "Error playing video"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            true
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            val videoUriString = listOfString[position]
            // Remove the video from the list
            listOfString.removeAt(position)
            // Notify adapter about the removed item
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listOfString.size)
            // Call the callback to delete from the database
            onDeleteVideo(videoUriString)
        }

    }

    override fun getItemCount(): Int = listOfString.size

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val video: VideoView = itemView.findViewById(R.id.videoView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletVedio)
    }
}
