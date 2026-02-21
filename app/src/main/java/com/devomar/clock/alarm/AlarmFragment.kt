package com.devomar.clock.alarm

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devomar.clock.R
import com.devomar.clock.databinding.FragmentAlarmBinding

class AlarmFragment : Fragment(R.layout.fragment_alarm) {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlarmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAlarmBinding.bind(view)

        val adapter = AlarmAdapter(
            onToggle = { alarm, enabled -> viewModel.toggleAlarm(alarm, enabled) },
            onItemClick = { alarm ->
                findNavController().navigate(
                    R.id.action_alarm_to_addEditAlarm,
                    bundleOf("alarmId" to alarm.id)
                )
            },
            onDelete = { alarm -> viewModel.deleteAlarm(alarm) }
        )

        binding.rvAlarms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlarms.adapter = adapter

        viewModel.alarms.observe(viewLifecycleOwner) { alarms ->
            adapter.submitList(alarms)
            binding.tvNoAlarms.visibility = if (alarms.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddAlarm.setOnClickListener {
            findNavController().navigate(
                R.id.action_alarm_to_addEditAlarm,
                bundleOf("alarmId" to -1)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
