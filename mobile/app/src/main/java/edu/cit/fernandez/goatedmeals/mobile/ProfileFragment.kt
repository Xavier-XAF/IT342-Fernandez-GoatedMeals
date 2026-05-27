package edu.cit.fernandez.goatedmeals.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import edu.cit.fernandez.goatedmeals.mobile.api.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI Bindings
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val btnEditProfile = view.findViewById<ImageView>(R.id.btnEditProfile)

        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val btnUpdatePassword = view.findViewById<Button>(R.id.btnUpdatePassword)

        val btnManageBilling = view.findViewById<Button>(R.id.btnManageBilling)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val sharedPreferences = requireActivity().getSharedPreferences("GoatedMealsPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token != null) {
            val authHeader = "Bearer $token"

            // 1. Fetch Profile Data
            RetrofitClient.getInstance(requireContext()).getCurrentUser(authHeader).enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        val firstName = user.firstname ?: "Goated"
                        val lastName = user.lastname ?: "User"

                        tvProfileName.text = "$firstName $lastName"
                        tvProfileEmail.text = user.email

                        // Cache the first name so the Home Screen greeting can use it!
                        sharedPreferences.edit().putString("userFirstName", firstName).apply()
                    }
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {}
            })

            // 2. Button Listeners
            btnEditProfile.setOnClickListener {
                Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
                // You can launch an EditProfileActivity here later!
            }

            btnUpdatePassword.setOnClickListener {
                val current = etCurrentPassword.text.toString()
                val newPass = etNewPassword.text.toString()

                if (current.isNotEmpty() && newPass.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Connecting to backend...", Toast.LENGTH_SHORT).show()
                    // We will add the Retrofit call for the /password endpoint in the next step!
                    etCurrentPassword.text.clear()
                    etNewPassword.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show()
                }
            }

            btnManageBilling.setOnClickListener {
                // Launch the Billing Activity!
                val intent = Intent(requireContext(), BillingActivity::class.java)
                startActivity(intent)
            }
        }

        // 3. Handle Logout
        btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}