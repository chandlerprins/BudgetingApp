package vcmsa.projects.zarguard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ExpenseReportDetailFragment : Fragment(R.layout.fragment_expense_report_detail) {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var categoryTitle: TextView
    private lateinit var totalSpentText: TextView
    private lateinit var expenseRecycler: RecyclerView
    private lateinit var adapter: ExpenseItemAdapter
    private val expenseList = mutableListOf<ExpenseItem>()

    companion object {
        private const val ARG_CATEGORY = "category_name"
        fun newInstance(category: String): ExpenseReportDetailFragment {
            val fragment = ExpenseReportDetailFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        categoryTitle = view.findViewById(R.id.categoryTitle)
        totalSpentText = view.findViewById(R.id.totalSpentText)
        expenseRecycler = view.findViewById(R.id.expenseRecycler)

        val category = arguments?.getString(ARG_CATEGORY) ?: return
        categoryTitle.text = "Category: $category"

        adapter = ExpenseItemAdapter(expenseList)
        expenseRecycler.layoutManager = LinearLayoutManager(requireContext())
        expenseRecycler.adapter = adapter

        loadExpenses(category)
    }

    private fun loadExpenses(categoryName: String) {
        val uid = auth.currentUser?.uid ?: return
        val expensesRef = database.child(uid).child("expenses")

        expensesRef.orderByChild("category").equalTo(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    expenseList.clear()
                    var total = 0.0

                    for (expense in snapshot.children) {
                        val merchant = expense.child("merchant").getValue(String::class.java) ?: ""
                        val amount = expense.child("amount").getValue(Double::class.java) ?: 0.0
                        val date = expense.child("date").getValue(String::class.java) ?: ""

                        expenseList.add(ExpenseItem(merchant, amount, date))
                        total += amount
                    }

                    adapter.notifyDataSetChanged()
                    totalSpentText.text = "Total Spent: R%.2f".format(total)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}
