package vcmsa.projects.zarguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { // Check if the fragment is still attached
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Already logged in → go straight to DashboardFragment
                    (activity as? MainActivity)?.loadFragment(DashboardFragment())
                } else {
                    // Not logged in → show SignUpFragment
                    (activity as? MainActivity)?.loadFragment(SignUpFragment())
                }
            }
        }, 3000) // 3-second delay
    }
}