package vcmsa.projects.zarguard

import CategorySummary
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ExpenseReportListFragment : Fragment(R.layout.fragment_expense_report_list) {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var totalExpensesText: TextView
    private val categoryList = mutableListOf<CategorySummary>()
    private lateinit var adapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        categoryRecycler = view.findViewById(R.id.categoryRecyclerView)
        totalExpensesText = view.findViewById(R.id.totalExpensesText)

        adapter = CategoryAdapter(categoryList) { selectedCategory ->
            // Navigate to detail fragment here (Step 2)
            val fragment = ExpenseReportDetailFragment.newInstance(selectedCategory.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

        categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        categoryRecycler.adapter = adapter

        loadCategoryData()
    }

    private fun loadCategoryData() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = database.child(uid)

        userRef.child("categories").get().addOnSuccessListener { categorySnap ->
            categoryList.clear()
            var totalAll = 0.0

            for (categoryNode in categorySnap.children) {
                val name = categoryNode.key ?: continue
                val limit = categoryNode.getValue(Double::class.java) ?: 0.0

                // Now calculate total spent under this category
                userRef.child("expenses").orderByChild("category").equalTo(name)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var totalSpent = 0.0
                            for (expense in snapshot.children) {
                                val amt = expense.child("amount").getValue(Double::class.java) ?: 0.0
                                totalSpent += amt
                            }

                            val summary = CategorySummary(name, limit, totalSpent)
                            categoryList.add(summary)
                            totalAll += totalSpent
                            adapter.notifyDataSetChanged()
                            totalExpensesText.text = "Total Expenses: R%.2f".format(totalAll)
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
        }
    }
}
