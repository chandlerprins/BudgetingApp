package vcmsa.projects.zarguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            // After 3 seconds, manually load LoginFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, LoginFragment())
                .commit()
        }, 3000)
    }
}
