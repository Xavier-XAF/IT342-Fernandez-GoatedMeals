package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cit.fernandez.goatedmeals.mobile.api.AuthResponse
import edu.cit.fernandez.goatedmeals.mobile.api.GoogleLoginRequest // Make sure you created this Data Class!
import edu.cit.fernandez.goatedmeals.mobile.api.LoginRequest
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Auto-Login Check ---
        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
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

        // --- NEW: Hook into the Google UI elements ---
        val btnGoogleSignIn = findViewById<Button>(R.id.btnGoogleSignIn)
        val credentialManager = CredentialManager.create(this)

        // 2. Listen for clicks on the Standard Login button
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            RetrofitClient.getInstance(this).loginUser(request).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@MainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("userEmail", email)

                        val token = response.body()?.data?.token
                        if (token != null) {
                            editor.putString("jwt_token", token)
                        }

                        // Also save the role if it's there (for the Admin check!)
                        val role = response.body()?.data?.user?.role
                        if (role != null) {
                            editor.putString("userRole", role)
                        }

                        editor.apply()

                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Connection Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // 3. Listen for clicks on the Register link
        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // --- NEW: Listen for clicks on the Google button ---
        btnGoogleSignIn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        // IMPORTANT: Paste your Web Client ID from Google Cloud Console below!
                        .setServerClientId("640726925859-1qchde0f6epqhr66378icq7vf5f7sgkb.apps.googleusercontent.com")
                        .setAutoSelectEnabled(true)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(this@MainActivity, request)
                    val credential = result.credential

                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        sendGoogleTokenToBackend(idToken)
                    }
                } catch (e: Exception) {
                    Log.e("GoogleAuth", "Sign-in failed", e)
                    Toast.makeText(this@MainActivity, "Google Sign-in canceled or failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- NEW: Helper Function placed OUTSIDE of onCreate ---
    private fun sendGoogleTokenToBackend(idToken: String) {
        // 1. Use the EXACT SAME LoginRequest, just with different fields!
        val request = LoginRequest(
            loginType = "GOOGLE",
            googleIdToken = idToken
        )

        // 2. Aim it at your EXACT SAME login endpoint!
        RetrofitClient.getInstance(this).loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {

                    val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.putBoolean("isLoggedIn", true)

                    val token = response.body()?.data?.token
                    if (token != null) {
                        editor.putString("jwt_token", token)
                    }

                    val email = response.body()?.data?.user?.email
                    if (email != null) {
                        editor.putString("userEmail", email)
                    }

                    val role = response.body()?.data?.user?.role
                    if (role != null) {
                        editor.putString("userRole", role)
                    }

                    editor.apply()

                    Toast.makeText(this@MainActivity, "Google Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Backend verification failed.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}