package vcmsa.projects.zarguard

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        bottomNav = findViewById(R.id.bottomNavigationView)

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val currentUser: FirebaseUser? = firebaseAuth.currentUser
            if (currentUser != null) {
                Log.d("MainActivity", "AuthState: User is signed in (UID: ${currentUser.uid})")
                loadFragment(DashboardFragment())
            } else {
                Log.d("MainActivity", "AuthState: User is signed out")
                loadFragment(SignUpFragment())
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> loadFragment(DashboardFragment())
                R.id.navigation_add_expense -> loadFragment(AddExpenseFragment())
                R.id.navigation_more -> {
                    showMorePopup()
                    return@setOnItemSelectedListener false
                }
            }
            true
        }

        // Listen for fragment changes to show/hide BottomNav
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
            if (currentFragment != null) {
                setBottomNavVisibility(currentFragment)
            }
        }

        if (savedInstanceState == null) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d("MainActivity", "onCreate: Initial check - User is signed in (UID: ${currentUser.uid})")
                // loadFragment(DashboardFragment()) // Listener will handle this onStart
            } else {
                Log.d("MainActivity", "onCreate: Initial check - User is signed out")
                loadFragment(SignUpFragment())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart() called")
        auth.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop() called")
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener!!)
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setBottomNavVisibility(fragment: Fragment) {
        when (fragment) {
            is SplashFragment, is LoginFragment, is SignUpFragment -> {
                bottomNav.visibility = View.GONE
            }
            else -> {
                bottomNav.visibility = View.VISIBLE
            }
        }
    }

    private fun showMorePopup() {
        val popup = PopupMenu(this, bottomNav)
        popup.menuInflater.inflate(R.menu.more_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                else -> false
            }
        }
        popup.show()
    }
}