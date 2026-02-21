package com.devomar.clock.alarm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devomar.clock.R
import com.devomar.clock.alarm.model.Alarm
import com.devomar.clock.databinding.FragmentAddEditAlarmBinding

class AddEditAlarmFragment : Fragment(R.layout.fragment_add_edit_alarm) {

    private var _binding: FragmentAddEditAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlarmViewModel by viewModels()

    private var existingAlarm: Alarm? = null

    private val dayChips by lazy {
        listOf(
            binding.chipSun,
            binding.chipMon,
            binding.chipTue,
            binding.chipWed,
            binding.chipThu,
            binding.chipFri,
            binding.chipSat
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditAlarmBinding.bind(view)

        val alarmId = arguments?.getInt("alarmId", -1) ?: -1

        if (alarmId != -1) {
            viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
                val alarm = alarms.find { it.id == alarmId }
                if (alarm != null && existingAlarm == null) {
                    existingAlarm = alarm
                    populateFields(alarm)
                }
            }
        }

        binding.btnSave.setOnClickListener { saveAlarm() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun populateFields(alarm: Alarm) {
        binding.timePicker.hour = alarm.hour
        binding.timePicker.minute = alarm.minute
        binding.etLabel.setText(alarm.label)
        for (i in 0..6) {
            dayChips[i].isChecked = alarm.repeatDays and (1 shl i) != 0
        }
    }

    private fun saveAlarm() {
        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute
        val label = binding.etLabel.text?.toString()?.trim() ?: ""
        var repeatDays = 0
        for (i in 0..6) {
            if (dayChips[i].isChecked) repeatDays = repeatDays or (1 shl i)
        }

        val existing = existingAlarm
        if (existing != null) {
            viewModel.updateAlarm(
                existing.copy(hour = hour, minute = minute, label = label, repeatDays = repeatDays)
            )
        } else {
            viewModel.addAlarm(
                Alarm(hour = hour, minute = minute, label = label, repeatDays = repeatDays)
            )
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
