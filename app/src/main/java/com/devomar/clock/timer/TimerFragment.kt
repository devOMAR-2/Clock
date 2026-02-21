package com.devomar.clock.timer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devomar.clock.R
import com.devomar.clock.databinding.FragmentTimerBinding
import java.util.Locale

class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TimerViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            viewModel.tick()
            handler.postDelayed(this, 100L)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTimerBinding.bind(view)

        setupPickers()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUiForState(state)
            if (state == TimerViewModel.State.RUNNING) {
                handler.removeCallbacks(ticker)
                handler.post(ticker)
            } else {
                handler.removeCallbacks(ticker)
                if (state == TimerViewModel.State.DONE) {
                    TimerNotifier.notify(requireContext())
                }
            }
        }

        viewModel.remainingMs.observe(viewLifecycleOwner) { ms ->
            binding.tvCountdown.text = formatMs(ms)
            val total = viewModel.totalMs.value ?: 1L
            val progress = if (total > 0) ((ms * 1000L) / total).toInt() else 0
            binding.progressTimer.progress = progress
        }

        binding.btnTimerStartPause.setOnClickListener {
            when (viewModel.state.value) {
                TimerViewModel.State.IDLE, TimerViewModel.State.PAUSED -> {
                    if (viewModel.state.value == TimerViewModel.State.IDLE) {
                        viewModel.setDuration(
                            binding.pickerHours.value,
                            binding.pickerMinutes.value,
                            binding.pickerSeconds.value
                        )
                    }
                    viewModel.start()
                }
                TimerViewModel.State.RUNNING -> viewModel.pause()
                else -> {}
            }
        }

        binding.btnTimerReset.setOnClickListener { viewModel.reset() }
    }

    private fun setupPickers() {
        binding.pickerHours.minValue = 0
        binding.pickerHours.maxValue = 23
        binding.pickerMinutes.minValue = 0
        binding.pickerMinutes.maxValue = 59
        binding.pickerSeconds.minValue = 0
        binding.pickerSeconds.maxValue = 59
    }

    private fun updateUiForState(state: TimerViewModel.State) {
        val isIdle = state == TimerViewModel.State.IDLE
        val isRunning = state == TimerViewModel.State.RUNNING
        val isDone = state == TimerViewModel.State.DONE

        binding.layoutInput.visibility = if (isIdle) View.VISIBLE else View.GONE
        binding.tvCountdown.visibility = if (!isIdle) View.VISIBLE else View.GONE
        binding.progressTimer.visibility = if (!isIdle) View.VISIBLE else View.GONE
        binding.tvDone.visibility = if (isDone) View.VISIBLE else View.GONE

        binding.btnTimerStartPause.text = when (state) {
            TimerViewModel.State.RUNNING -> getString(R.string.btn_pause)
            else -> getString(R.string.btn_start)
        }
        binding.btnTimerStartPause.isEnabled = !isDone
        binding.btnTimerReset.isEnabled = !isIdle || isDone
    }

    private fun formatMs(ms: Long): String {
        val h = ms / 3_600_000
        val m = (ms % 3_600_000) / 60_000
        val s = (ms % 60_000) / 1_000
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(ticker)
        super.onDestroyView()
        _binding = null
    }
}
