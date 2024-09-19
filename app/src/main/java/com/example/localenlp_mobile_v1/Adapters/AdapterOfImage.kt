package com.example.localenlp_mobile_v1.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.localenlp_mobile_v1.ActivityOfDataImage
import com.example.localenlp_mobile_v1.DB.ImageDB
import com.example.localenlp_mobile_v1.R

class AdapterOfImage(private val context: Context, private val listOfString: ArrayList<String>) :
    RecyclerView.Adapter<AdapterOfImage.DataViewHolder>() {

    private val db: ImageDB = ImageDB(context)
    private var selectedPosition = -1 // Keep track of which image to update

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // Inflate the layout for each item in the list (shape_of_image.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_image, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val dataItem = listOfString[position]

        // Use Glide or another library to load the image (assuming dataItem is a URL or image path)
        Glide.with(context).load(dataItem).into(holder.image)

        // Handle delete button
        holder.deleteButton.setOnClickListener {
            db.deleteImage(dataItem)
            listOfString.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listOfString.size)
        }

        // Handle update button
        holder.updateButton.setOnClickListener {
            selectedPosition = position // Mark this image for update
            pickImageGallery() // Trigger image selection
        }
    }

    override fun getItemCount(): Int {
        return listOfString.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView3)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletImage)
        val updateButton: ImageView = itemView.findViewById(R.id.updateImage)
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        (context as AppCompatActivity).startActivityForResult(intent, ActivityOfDataImage.IMAGE_REQUEST_CODE)
    }

    // Function to handle the result of the image selection
    fun handleImageResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ActivityOfDataImage.IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null && selectedPosition != -1) {
                // Get the old image URI
                val oldImageUri = listOfString[selectedPosition]

                // Remove the old image from the database
                db.deleteImage(oldImageUri)

                // Insert the new image into the database
                val newImageUri = selectedImageUri.toString()
                db.addImage(newImageUri)

                // Update the list and notify the adapter
                listOfString[selectedPosition] = newImageUri
                notifyItemChanged(selectedPosition)

                Toast.makeText(context, "Image updated: $newImageUri", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to update image.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
