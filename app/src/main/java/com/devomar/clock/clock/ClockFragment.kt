package com.devomar.clock.clock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devomar.clock.R
import com.devomar.clock.databinding.FragmentClockBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClockFragment : Fragment(R.layout.fragment_clock) {

    private var _binding: FragmentClockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClockViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClockBinding.bind(view)

        viewModel.isAnalog.observe(viewLifecycleOwner) { isAnalog ->
            binding.analogClock.visibility = if (isAnalog) View.VISIBLE else View.GONE
            binding.tvDigitalTime.visibility = if (isAnalog) View.GONE else View.VISIBLE
        }

        viewModel.selectedTimeZone.observe(viewLifecycleOwner) { tz ->
            binding.tvTimezone.text = tz.getDisplayName(false, java.util.TimeZone.SHORT)
            tick()
        }

        binding.btnToggleMode.setOnClickListener {
            viewModel.toggleMode()
        }

        binding.btnTimezone.setOnClickListener {
            TimezonePickerDialog { tzId ->
                viewModel.setTimeZone(tzId)
            }.show(parentFragmentManager, "timezone_picker")
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(ticker)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tick() {
        if (_binding == null) return
        val tz = viewModel.selectedTimeZone.value ?: return
        val now = Calendar.getInstance(tz)
        binding.analogClock.calendar = now

        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        timeFmt.timeZone = tz
        binding.tvDigitalTime.text = timeFmt.format(now.time)

        val dateFmt = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        dateFmt.timeZone = tz
        binding.tvDate.text = dateFmt.format(now.time)
    }
}
