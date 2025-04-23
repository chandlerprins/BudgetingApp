package vcmsa.projects.zarguard

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class dialog_add_category (private val onCategoryAdded: (String, Double) -> Unit) : DialogFragment() {


override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_add_category, null)

    val nameInput = view.findViewById<EditText>(R.id.categoryNameInput)
    val limitInput = view.findViewById<EditText>(R.id.categoryLimitInput)
    val saveBtn = view.findViewById<Button>(R.id.saveCategoryButton)
    val cancelBtn = view.findViewById<Button>(R.id.cancelButton)

    val builder = AlertDialog.Builder(requireContext())
    builder.setView(view)

    val dialog = builder.create()

    saveBtn.setOnClickListener {
        val name = nameInput.text.toString().trim()
        val limit = limitInput.text.toString().toDoubleOrNull()

        if (name.isEmpty() || limit == null) {
            Toast.makeText(context, "Please enter valid values", Toast.LENGTH_SHORT).show()
        } else {
            onCategoryAdded(name, limit)
            dialog.dismiss()
        }
    }

    cancelBtn.setOnClickListener {
        dialog.dismiss()
    }

    return dialog
   }
}