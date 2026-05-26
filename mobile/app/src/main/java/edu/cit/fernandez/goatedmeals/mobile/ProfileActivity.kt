package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import edu.cit.fernandez.goatedmeals.mobile.api.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmailAddress = findViewById<TextView>(R.id.tvEmailAddress)
        val tvUserRole = findViewById<TextView>(R.id.tvUserRole)
        val btnManageBilling = findViewById<Button>(R.id.btnManageBilling)

        // 1. Setup the missing Billing link!
        btnManageBilling.setOnClickListener {
            startActivity(Intent(this, BillingActivity::class.java))
        }

        // 2. Fetch User Profile Data
        RetrofitClient.getInstance(this).getCurrentUser().enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    // Display Data
                    val first = user.firstname ?: ""
                    val last = user.lastname ?: ""
                    tvFullName.text = if (first.isEmpty() && last.isEmpty()) "Valued Guest" else "$first $last"
                    tvEmailAddress.text = user.email
                    tvUserRole.text = "Role: ${user.role}"
                }
            }
            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })

        // 3. Setup Bottom Navigation
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationViewProfile).selectedItemId = R.id.nav_profile
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationViewProfile)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, MyScheduleActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> true // Already here!
                else -> false
            }
        }
    }
}