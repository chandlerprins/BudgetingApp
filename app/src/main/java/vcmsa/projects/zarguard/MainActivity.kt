package vcmsa.projects.zarguard

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(SplashFragment()) // Load Dashboard (Home) by default

        bottomNav = findViewById(R.id.bottomNavigationView)

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
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    // Function to show the More Popup
    private fun showMorePopup() {

        val popup = PopupMenu(this, bottomNav)
        popup.inflate(R.menu.more_menu)
        popup.show()
    }
}
