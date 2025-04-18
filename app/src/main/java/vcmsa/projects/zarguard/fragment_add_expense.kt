package vcmsa.projects.zarguard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = view.findViewById<Spinner>(R.id.categorySpinner)
        val categories = listOf(
            "Bills", "Furniture and Equipment", "Travel",
            "Wants", "Meals and Entertainment", "Food"
        )
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
    }
}