package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<TextItemViewHolder>() {
    var sleepNightData = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = sleepNightData.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        // Retrieves the data item at the specified position in the dataset and assigns it to the variable `item`
        val item = sleepNightData[position]

        // Set the text of the view held in the ViewHolder
        holder.textView.text = item.sleepQuality.toString() // This will only show a list of numbers (i.e., the user's sleep quality)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false) as TextView
        return TextItemViewHolder(view)
    }
}