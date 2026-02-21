package com.devomar.clock.stopwatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devomar.clock.R
import com.devomar.clock.databinding.ItemLapBinding
import java.util.Locale

class LapAdapter : ListAdapter<Long, LapAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemLapBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ms = getItem(position)
        val lapNumber = currentList.size - position
        holder.binding.tvLapNumber.text =
            holder.itemView.context.getString(R.string.label_lap, lapNumber)
        holder.binding.tvLapTime.text = formatMs(ms)
    }

    class DiffCallback : DiffUtil.ItemCallback<Long>() {
        override fun areItemsTheSame(oldItem: Long, newItem: Long) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Long, newItem: Long) = oldItem == newItem
    }

    companion object {
        fun formatMs(ms: Long): String {
            val h = ms / 3_600_000
            val m = (ms % 3_600_000) / 60_000
            val s = (ms % 60_000) / 1_000
            val cs = (ms % 1_000) / 10
            return String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", h, m, s, cs)
        }
    }
}
