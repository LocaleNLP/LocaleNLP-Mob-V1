package com.example.localenlp_mobile_v1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Classes.AudioClass
import com.example.localenlp_mobile_v1.Interfaces.OnItemClickListener
import com.example.localenlp_mobile_v1.R
import java.text.SimpleDateFormat
import java.util.*

class AdapterAudio(
    private var records: List<AudioClass>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AdapterAudio.AudioViewHolder>() {

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var tvFilename: TextView = itemView.findViewById(R.id.tvFileName)
        var tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClickListener(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItelLongClickListener(position)
            }
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemview_layout, parent, false)
        return AudioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val record = records[position]
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(record.timestamp)
            val strDate: String = sdf.format(date)

            holder.tvFilename.text = record.filename
            holder.tvMeta.text = "Recorded at: $strDate | Duration: ${record.duration}"
            holder.checkBox.isChecked = false // Set initial state; adjust as needed
        }
    }
}
