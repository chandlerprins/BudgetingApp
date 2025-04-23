package vcmsa.projects.zarguard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class dialog_date_range_picker(private val onDatesSelected: (startDate: String, endDate: String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_date_range_picker, null)
        val startInput = view.findViewById<EditText>(R.id.startDateInput)
        val endInput = view.findViewById<EditText>(R.id.endDateInput)
        val applyBtn = view.findViewById<Button>(R.id.applyDateFilterButton)

        val calendar = Calendar.getInstance()

        fun showDatePicker(editText: EditText) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val picker = DatePickerDialog(requireContext(), { _, y, m, d ->
                val dateStr = String.format("%02d/%02d/%d", d, m + 1, y)
                editText.setText(dateStr)
            }, year, month, day)

            picker.show()
        }

        startInput.setOnClickListener { showDatePicker(startInput) }
        endInput.setOnClickListener { showDatePicker(endInput) }

        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()

        applyBtn.setOnClickListener {
            val start = startInput.text.toString()
            val end = endInput.text.toString()
            if (start.isNotBlank() && end.isNotBlank()) {
                onDatesSelected(start, end)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }

        return dialog
    }
}