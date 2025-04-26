package vcmsa.projects.zarguard

import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            // After 3 seconds, navigate to login screen
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }, 3000) // 3 second delay
    }
}