import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.ActivityOfDataAudio
import com.example.localenlp_mobile_v1.ActivityOfDataImage
import com.example.localenlp_mobile_v1.ActivityOfDataText
import com.example.localenlp_mobile_v1.ActivityOfDataVedio
import com.example.localenlp_mobile_v1.Classes.DataItem
import com.example.localenlp_mobile_v1.R

// Adapter for RecyclerView that binds a list of DataItem objects to the UI
class DataAdapter(private val dataList: List<DataItem>) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // Inflate the layout for each item in the list (layout_of_data_type.xml)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_of_data_type, parent, false)
        return DataViewHolder(view)
    }

    // Called to bind data to the views at a specific position
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        // Get the data item at the current position
        val dataItem = dataList[position]
        // Bind the data to the views
        holder.imageView.setImageResource(dataItem.imageResId)
        holder.textView.text = dataItem.text
        var titleText:String = dataItem.text

        holder.itemView.setOnClickListener {
            if (titleText == "Text"){
                val intent = Intent(holder.itemView.context, ActivityOfDataText::class.java)
                holder.itemView.context.startActivity(intent)
            }
            if (titleText == "Image"){
                val intent = Intent(holder.itemView.context, ActivityOfDataImage::class.java)
                holder.itemView.context.startActivity(intent)
            }
            if (titleText == "Vedio"){
                val intent = Intent(holder.itemView.context, ActivityOfDataVedio::class.java)
                holder.itemView.context.startActivity(intent)
            }
            if (titleText == "Audio"){
                val intent = Intent(holder.itemView.context, ActivityOfDataAudio::class.java)
                holder.itemView.context.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textOfDataType)
    }
}
