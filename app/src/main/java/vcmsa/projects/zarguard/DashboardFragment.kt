package vcmsa.projects.zarguard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var greetingTextView: TextView
    private lateinit var budgetEditText: EditText
    private lateinit var categoriesLayout: LinearLayout
    private lateinit var addCategoryButton: Button
    private lateinit var saveBudgetButton: Button
    private lateinit var skipButton: Button

    private val categoryViews = mutableListOf<View>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        greetingTextView = view.findViewById(R.id.greetingTextView)
        budgetEditText = view.findViewById(R.id.budgetEditText)
        categoriesLayout = view.findViewById(R.id.categoriesLayout)
        addCategoryButton = view.findViewById(R.id.addCategoryButton)
        saveBudgetButton = view.findViewById(R.id.saveBudgetButton)
        skipButton = view.findViewById(R.id.skipButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserData(currentUser.uid)
        }

        addCategoryButton.setOnClickListener {
            addCategoryInputView()
        }

        saveBudgetButton.setOnClickListener {
            saveBudgetAndCategories()
        }

        skipButton.setOnClickListener {
            loadMainDashboard()
        }
    }

    private fun loadUserData(uid: String) {
        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hasBudget = snapshot.hasChild("budget")
                    val hasCategories = snapshot.hasChild("categories")

                    if (!hasBudget || !hasCategories) {
                        showBudgetSetup()
                    } else {
                        loadMainDashboard()
                    }
                } else {
                    showBudgetSetup()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Database error: ${error.message}")
                Toast.makeText(context, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                loadMainDashboard()
            }
        })
    }

    private fun showBudgetSetup() {
        greetingTextView.text = "Welcome New User! Set up your budget"

        budgetEditText.visibility = View.VISIBLE
        categoriesLayout.visibility = View.VISIBLE
        addCategoryButton.visibility = View.VISIBLE
        saveBudgetButton.visibility = View.VISIBLE
        skipButton.visibility = View.VISIBLE
    }

    private fun loadMainDashboard() {
        greetingTextView.text = "Welcome Back!"

        budgetEditText.visibility = View.GONE
        categoriesLayout.visibility = View.GONE
        addCategoryButton.visibility = View.GONE
        saveBudgetButton.visibility = View.GONE
        skipButton.visibility = View.GONE

        // If you want, you can load and show the user's budget and categories here
        // Later we will build a separate "home screen" for dashboard after setup
    }

    private fun addCategoryInputView() {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.HORIZONTAL
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        container.weightSum = 2f

        val nameEditText = EditText(requireContext())
        nameEditText.hint = "Category Name"
        nameEditText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        nameEditText.setBackgroundResource(android.R.drawable.edit_text)
        nameEditText.setPadding(12, 12, 12, 12)

        val limitEditText = EditText(requireContext())
        limitEditText.hint = "Limit Amount"
        limitEditText.inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        limitEditText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        limitEditText.setBackgroundResource(android.R.drawable.edit_text)
        limitEditText.setPadding(12, 12, 12, 12)
        (limitEditText.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 0, 0)

        container.addView(nameEditText)
        container.addView(limitEditText)

        categoriesLayout.addView(container)
        categoryViews.add(container)
    }

    private fun saveBudgetAndCategories() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        val budgetAmount = budgetEditText.text.toString().trim().toDoubleOrNull()
        if (budgetAmount == null) {
            Toast.makeText(context, "Please enter a valid budget", Toast.LENGTH_SHORT).show()
            return
        }

        val categories = getCategoriesFromInput()
        if (categories.isEmpty()) {
            Toast.makeText(context, "Please add at least one category", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to Firebase
        val userRef = database.child(uid)
        userRef.child("budget").setValue(budgetAmount)
        userRef.child("categories").setValue(categories)
            .addOnSuccessListener {
                Toast.makeText(context, "Budget and Categories saved!", Toast.LENGTH_SHORT).show()
                loadMainDashboard()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Save failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCategoriesFromInput(): Map<String, Double> {
        val categories = mutableMapOf<String, Double>()
        for (container in categoryViews) {
            if (container is LinearLayout && container.childCount == 2) {
                val nameEditText = container.getChildAt(0) as? EditText
                val limitEditText = container.getChildAt(1) as? EditText

                val categoryName = nameEditText?.text.toString().trim()
                val categoryLimit = limitEditText?.text.toString().trim().toDoubleOrNull()

                if (categoryName.isNotEmpty() && categoryLimit != null) {
                    categories[categoryName] = categoryLimit
                }
            }
        }
        return categories
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryViews.clear()
    }
}
