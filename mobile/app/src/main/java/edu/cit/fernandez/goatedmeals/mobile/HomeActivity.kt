package edu.cit.fernandez.goatedmeals.mobile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val tvMainContent = findViewById<TextView>(R.id.tvMainContent)
        val tvWelcomeHeader = findViewById<TextView>(R.id.tvWelcomeHeader)

        // Grab the user's email from the session to personalize the header!
        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "User")
        tvWelcomeHeader.text = "Welcome,\n$userEmail"

        // Listen for clicks on the Bottom Navigation Bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    tvMainContent.text = "Home: Meal Catalog Grid will go here!"
                    true
                }
                R.id.nav_search -> {
                    tvMainContent.text = "Search: Advanced filters will go here!"
                    true
                }
                R.id.nav_cart -> {
                    tvMainContent.text = "Cart: Your weekly meal box will go here!"
                    true
                }
                R.id.nav_profile -> {
                    // In Week 8, we will add the "Sign Out" button here!
                    tvMainContent.text = "Profile: Your settings will go here!"
                    true
                }
                else -> false
            }
        }
    }
}