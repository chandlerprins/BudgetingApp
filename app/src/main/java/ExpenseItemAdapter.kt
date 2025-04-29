package vcmsa.projects.zarguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ExpenseItem(
    val merchant: String,
    val amount: Double,
    val date: String
)

class ExpenseItemAdapter(private val items: List<ExpenseItem>) :
    RecyclerView.Adapter<ExpenseItemAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val merchantText: TextView = view.findViewById(R.id.merchantText)
        val amountText: TextView = view.findViewById(R.id.amountText)
        val dateText: TextView = view.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = items[position]
        holder.merchantText.text = "Merchant: ${expense.merchant}"
        holder.amountText.text = "Amount: R%.2f".format(expense.amount)
        holder.dateText.text = "Date: ${expense.date}"
    }

    override fun getItemCount(): Int = items.size
}
