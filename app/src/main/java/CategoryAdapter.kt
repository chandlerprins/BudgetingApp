package vcmsa.projects.zarguard

import CategorySummary
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<CategorySummary>,
    private val onCategoryClick: (CategorySummary) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.categoryNameText)
        val amountText: TextView = view.findViewById(R.id.categoryAmountText)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategoryClick(categories[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.nameText.text = category.name
        holder.amountText.text = "R%.2f / R%.2f".format(category.totalSpent, category.limit)

        if (category.totalSpent > category.limit) {
            holder.amountText.setTextColor(Color.RED)
        } else {
            holder.amountText.setTextColor(Color.parseColor("#00A86B"))
        }
    }

    override fun getItemCount(): Int = categories.size
}
