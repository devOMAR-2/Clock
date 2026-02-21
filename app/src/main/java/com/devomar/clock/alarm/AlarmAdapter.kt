package com.devomar.clock.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devomar.clock.alarm.model.Alarm
import com.devomar.clock.databinding.ItemAlarmBinding
import java.util.Locale

class AlarmAdapter(
    private val onToggle: (Alarm, Boolean) -> Unit,
    private val onItemClick: (Alarm) -> Unit,
    private val onDelete: (Alarm) -> Unit
) : ListAdapter<Alarm, AlarmAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlarmBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = getItem(position)
        with(holder.binding) {
            tvAlarmTime.text = String.format(Locale.getDefault(), "%02d:%02d", alarm.hour, alarm.minute)
            tvAlarmLabel.text = alarm.label.ifEmpty { root.context.getString(com.devomar.clock.R.string.label_alarm) }
            tvAlarmTime.alpha = if (alarm.isEnabled) 1f else 0.4f
            tvAlarmLabel.alpha = if (alarm.isEnabled) 1f else 0.4f
            switchEnabled.setOnCheckedChangeListener(null)
            switchEnabled.isChecked = alarm.isEnabled
            switchEnabled.setOnCheckedChangeListener { _, checked -> onToggle(alarm, checked) }
            root.setOnClickListener { onItemClick(alarm) }
            btnDelete.setOnClickListener { onDelete(alarm) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm) = oldItem == newItem
    }
}
