package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.fernandez.goatedmeals.mobile.api.AuthResponse
import edu.cit.fernandez.goatedmeals.mobile.api.LoginRequest
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- NEW: Auto-Login Check ---
        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // If they are already logged in, skip this screen and go straight to Home!
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // 1. Hook into your XML UI elements
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // 2. Listen for clicks on the Login button
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic frontend validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Package the data exactly how Spring Boot expects it
            val request = LoginRequest(email, password)

            // 3. Send the request via Retrofit
            RetrofitClient.instance.loginUser(request).enqueue(object : Callback<AuthResponse> {

                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@MainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // --- NEW: Save Session to SharedPreferences ---
                        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("userEmail", email) // Save the email they logged in with
                        editor.apply()

                        // --- NEW: Teleport to Home Screen ---
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Closes the login screen so they can't hit the "Back" button to return to it

                    } else {
                        Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_LONG).show()
                    }
                }

                // What to do if the network totally fails (e.g. server is off)
                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Connection Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // 4. Listen for clicks on the Register link
        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
