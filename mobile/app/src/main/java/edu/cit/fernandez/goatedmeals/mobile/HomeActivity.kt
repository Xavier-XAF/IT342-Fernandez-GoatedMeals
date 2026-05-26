package edu.cit.fernandez.goatedmeals.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.fernandez.goatedmeals.mobile.api.Meal
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var rvMeals: RecyclerView
    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Hook into the UI
        rvMeals = findViewById(R.id.rvMeals)
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNav = findViewById(R.id.bottomNavigationView)

        // Tell the RecyclerView to scroll vertically
        rvMeals.layoutManager = LinearLayoutManager(this)

        // 2. Greet the user by grabbing their saved email
        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("userEmail", "Guest")
        tvWelcome.text = "Welcome back,\n$email"

        // 3. Trigger the network call to fetch the catalog
        fetchMeals()

        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // We are already here, do nothing!
                    true
                }
                R.id.nav_search -> {
                    // We haven't built search yet
                    Toast.makeText(this, "Search coming soon!", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.nav_cart -> {
                    val intent = Intent(this, MyScheduleActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    false
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    false
                }
                else -> false
            }
        }

        // 4. Secure Logout Logic
        btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear() // Wipes the session and the JWT token
            editor.apply()

            // Kick them back to the Login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchMeals() {
        RetrofitClient.getInstance(this).getMealCatalog().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful && response.body() != null) {

                    // Success! Feed the list of meals into our MealAdapter
                    val meals = response.body()!!
                    val adapter = MealAdapter(meals)
                    rvMeals.adapter = adapter

                } else {
                    Toast.makeText(this@HomeActivity, "Failed to load menu. Unauthorized?", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}