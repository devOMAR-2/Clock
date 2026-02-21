package com.devomar.clock.clock

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.devomar.clock.R
import java.util.TimeZone

class TimezonePickerDialog(
    private val onTimezoneSelected: (String) -> Unit
) : DialogFragment() {

    private val allTimezoneIds: List<String> = TimeZone.getAvailableIDs()
        .toList()
        .sorted()

    private var filteredIds = allTimezoneIds.toMutableList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val searchField = EditText(context).apply {
            hint = getString(R.string.hint_search_timezone)
            setSingleLine()
            setPadding(32, 16, 32, 8)
        }

        val listView = ListView(context)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, filteredIds)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            onTimezoneSelected(filteredIds[position])
            dismiss()
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.lowercase() ?: ""
                filteredIds.clear()
                filteredIds.addAll(
                    allTimezoneIds.filter { it.lowercase().contains(query) }
                )
                adapter.notifyDataSetChanged()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(searchField)
            addView(listView)
        }

        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_timezone_title)
            .setView(container)
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }
}
