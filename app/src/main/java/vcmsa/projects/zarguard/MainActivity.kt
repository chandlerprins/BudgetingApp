package vcmsa.projects.zarguard

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigationView)

        loadFragment(SplashFragment()) // Start with SplashFragment

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
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Optional nice animation
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

    // Function to show the More Popup
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
