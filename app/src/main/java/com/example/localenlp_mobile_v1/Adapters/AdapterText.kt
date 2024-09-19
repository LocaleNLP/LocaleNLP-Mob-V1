package com.example.localenlp_mobile_v1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.R
import TextDB
import androidx.appcompat.app.AppCompatActivity
import com.example.localenlp_mobile_v1.DialogFragment.DialogFragmentForUpdateText

class AdapterText(private val context: Context, private val listOfString: ArrayList<String>) :
    RecyclerView.Adapter<AdapterText.DataViewHolder>() {

    // Initialize the TextDB instance
    private val db: TextDB = TextDB(context)

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // Inflate the layout for each item in the list (shape_of_text.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shape_of_text, parent, false)
        return DataViewHolder(view)
    }

    // Called to bind data to the views at a specific position
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val dataItem = listOfString[position]
        holder.textView.text = dataItem

        // Handle delete button
        holder.delete.setOnClickListener {
            db.deleteText(dataItem)
            listOfString.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listOfString.size)
        }

        // Handle update button
        holder.update.setOnClickListener {
            val dialog = DialogFragmentForUpdateText(dataItem)
            dialog.setDialogListener(object : DialogFragmentForUpdateText.DialogListener {
                override fun onTextUpdated(newText: String) {
                    // Update the text in the database
                    db.updateText(dataItem, newText)

                    // Update the list and notify the adapter
                    listOfString[position] = newText
                    notifyItemChanged(position)
                }
            })

            // Show the dialog
            dialog.show((context as AppCompatActivity).supportFragmentManager, "DialogFragmentForUpdateText")
        }
    }


    override fun getItemCount(): Int {
        return listOfString.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
        val delete: ImageView = itemView.findViewById(R.id.delet)
        val update: ImageView = itemView.findViewById(R.id.update)
    }
}
