package edu.cit.fernandez.goatedmeals.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.fernandez.goatedmeals.mobile.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeDashboardFragment : Fragment(R.layout.fragment_home_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. UI Bindings
        val tvDashboardGreeting = view.findViewById<TextView>(R.id.tvDashboardGreeting)
        val tvDashboardCredits = view.findViewById<TextView>(R.id.tvDashboardCredits)

        val btnViewMenu = view.findViewById<Button>(R.id.btnViewMenu)
        val btnViewSchedule = view.findViewById<Button>(R.id.btnViewSchedule)
        val cardNextDelivery = view.findViewById<CardView>(R.id.cardNextDelivery)

        // Hero Card Elements
        val ivNextDeliveryImage = view.findViewById<ImageView>(R.id.ivNextDeliveryImage)
        val tvNextDeliveryDate = view.findViewById<TextView>(R.id.tvNextDeliveryDate)
        val tvNextDeliveryName = view.findViewById<TextView>(R.id.tvNextDeliveryName)
        val tvNextDeliveryStatus = view.findViewById<TextView>(R.id.tvNextDeliveryStatus)

        // Menu Recycler View
        val rvDashboardMenu = view.findViewById<RecyclerView>(R.id.rvDashboardMenu)

        // Bind the Logout Icon ---
        val ivDashboardLogout = view.findViewById<ImageView>(R.id.ivDashboardLogout)

        // Ensure horizontal scrolling for the dashboard menu!
        rvDashboardMenu.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val sharedPreferences = requireActivity().getSharedPreferences("GoatedMealsPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)
        val authHeader = "Bearer $token"

        // The Quick Logout Logic ---
        ivDashboardLogout.setOnClickListener {
            // Erase the saved JWT token and user data
            sharedPreferences.edit().clear().apply()

            // Teleport back to MainActivity (Login Screen) and clear the back-stack
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        if (token != null) {
            // API CALL 1: Fetch Current User (Name)
            RetrofitClient.getInstance(requireContext()).getCurrentUser(authHeader).enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        tvDashboardGreeting.text = "${response.body()!!.firstname ?: "Goated"}!"
                    }
                }
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {}
            })

            // API CALL 2: Fetch Subscription (For the new Credits Badge!)
            RetrofitClient.getInstance(requireContext()).getMySubscription(authHeader).enqueue(object : Callback<SubscriptionResponse> {
                override fun onResponse(call: Call<SubscriptionResponse>, response: Response<SubscriptionResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        tvDashboardCredits.text = response.body()!!.availableCredits.toString()
                    }
                }
                override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {}
            })
        }

        // API CALL 3: Fetch Schedule Data (Hero Card)
        RetrofitClient.getInstance(requireContext()).getMySchedules().enqueue(object : Callback<List<MealSchedule>> {
            override fun onResponse(call: Call<List<MealSchedule>>, response: Response<List<MealSchedule>>) {
                if (response.isSuccessful && response.body() != null) {
                    val nextMeal = response.body()!!.firstOrNull { it.status == "SCHEDULED" || it.status == "PREPARING" }

                    if (nextMeal != null) {
                        // Clear the tint so the real image's colors shine through
                        ivNextDeliveryImage.clearColorFilter()

                        tvNextDeliveryDate.text = "${nextMeal.deliveryDay?.uppercase() ?: "TBD"} • ${nextMeal.deliveryTime ?: "TBD"}"
                        tvNextDeliveryName.text = nextMeal.meal.name
                        tvNextDeliveryStatus.text = "Status: ${nextMeal.status}"

                        Glide.with(requireContext())
                            .load(nextMeal.meal.imageUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivNextDeliveryImage)

                    } else {
                        tvNextDeliveryDate.text = "NO UPCOMING DELIVERIES"
                        tvNextDeliveryName.text = "Your schedule is clear"
                        tvNextDeliveryStatus.text = "Tap here to book a meal"
                    }
                }
            }
            override fun onFailure(call: Call<List<MealSchedule>>, t: Throwable) {
                tvNextDeliveryName.text = "Offline Mode"
            }
        })

        // API CALL 4: Fetch Live Menu (Bottom Carousel)
        RetrofitClient.getInstance(requireContext()).getMealCatalog().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Reuse your exact MealAdapter!
                    val adapter = MealAdapter(response.body()!!)
                    rvDashboardMenu.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {}
        })

        // Navigation
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        btnViewMenu.setOnClickListener { bottomNav.selectedItemId = R.id.nav_menu }
        btnViewSchedule.setOnClickListener { bottomNav.selectedItemId = R.id.nav_schedule }
        cardNextDelivery.setOnClickListener { bottomNav.selectedItemId = R.id.nav_schedule }
    }
}