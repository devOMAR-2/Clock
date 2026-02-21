package com.devomar.clock.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devomar.clock.R
import com.devomar.clock.databinding.FragmentStopwatchBinding
import com.devomar.clock.stopwatch.LapAdapter.Companion.formatMs

class StopwatchFragment : Fragment(R.layout.fragment_stopwatch) {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StopwatchViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            viewModel.tick()
            handler.postDelayed(this, 16L)  // ~60fps update
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStopwatchBinding.bind(view)

        val lapAdapter = LapAdapter()
        binding.rvLaps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLaps.adapter = lapAdapter

        viewModel.elapsedMs.observe(viewLifecycleOwner) { ms ->
            binding.tvElapsed.text = formatMs(ms)
        }

        viewModel.laps.observe(viewLifecycleOwner) { laps ->
            lapAdapter.submitList(laps.toList())
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.btnStartStop.text = when (state) {
                StopwatchViewModel.State.RUNNING -> getString(R.string.btn_stop)
                else -> getString(R.string.btn_start)
            }
            binding.btnLap.isEnabled = state == StopwatchViewModel.State.RUNNING
            binding.btnReset.isEnabled = state != StopwatchViewModel.State.RUNNING

            if (state == StopwatchViewModel.State.RUNNING) {
                handler.removeCallbacks(ticker)
                handler.post(ticker)
            } else {
                handler.removeCallbacks(ticker)
            }
        }

        binding.btnStartStop.setOnClickListener {
            if (viewModel.state.value == StopwatchViewModel.State.RUNNING) {
                viewModel.pause()
            } else {
                viewModel.start()
            }
        }
        binding.btnReset.setOnClickListener { viewModel.reset() }
        binding.btnLap.setOnClickListener { viewModel.lap() }
    }

    override fun onDestroyView() {
        handler.removeCallbacks(ticker)
        super.onDestroyView()
        _binding = null
    }
}
