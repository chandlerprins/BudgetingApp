package vcmsa.projects.zarguard

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class AddExpenseFragment : Fragment(R.layout.fragment_add_expense) {

    private lateinit var merchantEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var uploadGalleryButton: Button
    private lateinit var saveExpenseButton: Button

    private var selectedImagePath: String? = null
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        merchantEditText = view.findViewById(R.id.merchantEditText)
        amountEditText = view.findViewById(R.id.amountEditText)
        dateEditText = view.findViewById(R.id.dateEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        uploadGalleryButton = view.findViewById(R.id.uploadGalleryButton)
        saveExpenseButton = view.findViewById(R.id.saveExpenseButton)

        auth = FirebaseAuth.getInstance()

        setupDatePicker()
        loadCategories()

        uploadGalleryButton.setOnClickListener {
            openGallery()
        }

        saveExpenseButton.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupDatePicker() {
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    dateEditText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun loadCategories() {
        val uid = auth.currentUser?.uid ?: return
        val categoriesRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("categories")

        categoriesRef.get().addOnSuccessListener { snapshot ->
            val categories = mutableListOf<String>()
            for (categorySnapshot in snapshot.children) {
                val categoryName = categorySnapshot.key
                if (categoryName != null) {
                    categories.add(categoryName)
                }
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                selectedImagePath = saveImageToInternalStorage(imageUri)
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val fileName = generateUniqueFileName(uri)
            val dir = File(requireContext().filesDir, "receipts")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val file = File(dir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateUniqueFileName(uri: Uri): String {
        val timestamp = System.currentTimeMillis()
        val extension = getFileExtension(uri) ?: "jpg"
        return "receipt_$timestamp.$extension"
    }

    private fun getFileExtension(uri: Uri): String? {
        return requireContext().contentResolver.getType(uri)?.split("/")?.last()
    }

    private fun saveExpense() {
        val merchant = merchantEditText.text.toString().trim()
        val amount = amountEditText.text.toString().trim().toDoubleOrNull()
        val date = dateEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val category = categorySpinner.selectedItem?.toString() ?: ""

        if (merchant.isEmpty() || amount == null || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        val expensesRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("expenses")
        val newExpenseRef = expensesRef.push()

        val expenseData = mutableMapOf<String, Any>(
            "merchant" to merchant,
            "amount" to amount,
            "date" to date,
            "description" to description,
            "category" to category
        )

        if (!selectedImagePath.isNullOrEmpty()) {
            expenseData["receiptPath"] = selectedImagePath!!
        }

        newExpenseRef.setValue(expenseData)
            .addOnSuccessListener {
                Toast.makeText(context, "Expense Saved!", Toast.LENGTH_SHORT).show()
                resetForm()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Failed to save expense: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetForm() {
        merchantEditText.text.clear()
        amountEditText.text.clear()
        dateEditText.text.clear()
        descriptionEditText.text.clear()
        categorySpinner.setSelection(0)
        selectedImagePath = null
    }
}
