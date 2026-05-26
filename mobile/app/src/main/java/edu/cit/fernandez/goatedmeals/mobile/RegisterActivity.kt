package edu.cit.fernandez.goatedmeals.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.fernandez.goatedmeals.mobile.api.AuthResponse
import edu.cit.fernandez.goatedmeals.mobile.api.RegisterRequest
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Hook into your EXACT XML UI elements
        val etFirstname = findViewById<EditText>(R.id.etFirstname)
        val etLastname = findViewById<EditText>(R.id.etLastname)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etContactNumber = findViewById<EditText>(R.id.etContactNumber)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegisterAccount)
        val tvLoginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)

        // 2. Handle the Register button click
        btnRegister.setOnClickListener {
            val firstname = etFirstname.text.toString().trim()
            val lastname = etLastname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val contactNumber = etContactNumber.text.toString().trim()

            // --- NEW: Input Validation to protect your backend! ---
            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || contactNumber.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // ------------------------------------------------------

            btnRegister.isEnabled = false
            btnRegister.text = "Creating Account..."

            // 3. Create your custom RegisterRequest
            val request = RegisterRequest(firstname, lastname, email, password, contactNumber)

            // 4. Send the network request
            RetrofitClient.getInstance(this).registerUser(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_LONG).show()

                        // Teleport back to the login screen
                        finish()
                    } else {
                        // Usually means the email is already taken
                        val errorMsg = response.body()?.message ?: "Registration failed. Email might be in use."
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"
                    Toast.makeText(this@RegisterActivity, "Connection Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // 5. Return to Login screen if they already have an account
        tvLoginRedirect.setOnClickListener {
            finish()
        }
    }
}