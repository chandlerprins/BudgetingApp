package vcmsa.projects.zarguard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val emailInput = view.findViewById<EditText>(R.id.emailEditText)
        val passwordInput = view.findViewById<EditText>(R.id.passwordEditText)
        val usernameInput = view.findViewById<EditText>(R.id.usernameEditText)
        val signupButton = view.findViewById<Button>(R.id.signUpButton)
        val googleButton = view.findViewById<Button>(R.id.googleSignInButton)

        signupButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                val userMap = mapOf(
                                    "username" to username,
                                    "email" to email
                                )
                                usersRef.child(userId).setValue(userMap)
                            }
                            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                            (activity as MainActivity).loadFragment(LoginFragment())
                        } else {
                            Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        googleButton.setOnClickListener {
            Toast.makeText(context, "Google Sign-In not implemented yet", Toast.LENGTH_SHORT).show()
            // (I'll show you real Google SignIn next if you want!)
        }
    }
}
