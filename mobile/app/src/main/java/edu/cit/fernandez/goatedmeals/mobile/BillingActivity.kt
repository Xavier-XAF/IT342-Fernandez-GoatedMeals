package edu.cit.fernandez.goatedmeals.mobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.fernandez.goatedmeals.mobile.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillingActivity : AppCompatActivity() {

    // New Premium Card UI elements
    private lateinit var tvPlanName: TextView
    private lateinit var tvPlanStatus: TextView
    private lateinit var tvRenewalDate: TextView
    private lateinit var tvCredits: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        // Bind UI Elements
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        tvPlanName = findViewById(R.id.tvPlanName)
        tvPlanStatus = findViewById(R.id.tvPlanStatus)
        tvRenewalDate = findViewById(R.id.tvRenewalDate)
        tvCredits = findViewById(R.id.tvCredits)

        val btnWeekly = findViewById<Button>(R.id.btnWeeklyPlan)
        val btnMonthly = findViewById<Button>(R.id.btnMonthlyPlan)

        // Close the screen when tapping Back
        btnBack.setOnClickListener { finish() }

        // Fetch current credits on load
        fetchSubscriptionStatus()

        // Setup PayMongo Purchase Buttons
        btnWeekly.setOnClickListener {
            initiateCheckout("WEEKLY", 1490.00, btnWeekly)
        }

        btnMonthly.setOnClickListener {
            initiateCheckout("MONTHLY", 5000.00, btnMonthly)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh credits when user returns to this screen from the browser
        fetchSubscriptionStatus()
    }

    private fun fetchSubscriptionStatus() {
        val sharedPreferences = getSharedPreferences("GoatedMealsPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token != null) {
            val authHeader = "Bearer $token"

            RetrofitClient.getInstance(this).getMySubscription(authHeader).enqueue(object : Callback<SubscriptionResponse> {
                override fun onResponse(call: Call<SubscriptionResponse>, response: Response<SubscriptionResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val sub = response.body()!!

                        // Update the Premium Hero Card
                        if (sub.hasSubscription && sub.planTier != null) {
                            tvPlanName.text = "${sub.planTier} Plan"
                            tvPlanStatus.text = "STATUS: ${sub.status?.uppercase() ?: "ACTIVE"}"
                        } else {
                            tvPlanName.text = "No Active Plan"
                            tvPlanStatus.text = "STATUS: INACTIVE"
                            tvPlanStatus.setTextColor(android.graphics.Color.parseColor("#FF5252")) // Red if inactive
                        }

                        tvCredits.text = sub.availableCredits.toString()
                        tvRenewalDate.text = "Next renewal: ${sub.nextRenewalDate ?: "--"}"
                    }
                }
                override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
                    tvPlanName.text = "Offline Mode"
                    tvCredits.text = "Error"
                }
            })
        } // <-- This was the missing closing brace for the 'if' statement!
    } // <-- This was the missing closing brace for the 'fetchSubscriptionStatus' function!

    private fun initiateCheckout(planTier: String, amount: Double, clickedButton: Button) {
        clickedButton.isEnabled = false
        clickedButton.text = "Loading..."

        val request = PaymentRequest(planTier, amount)

        RetrofitClient.getInstance(this).initiatePayment(request).enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                clickedButton.isEnabled = true
                clickedButton.text = if (planTier == "WEEKLY") "Subscribe Weekly" else "Subscribe Monthly"

                if (response.isSuccessful && response.body()?.success == true) {
                    val checkoutUrl = response.body()?.data?.checkoutUrl

                    if (checkoutUrl != null) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                        startActivity(browserIntent)
                    }
                } else {
                    Toast.makeText(this@BillingActivity, "Failed to connect to PayMongo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                clickedButton.isEnabled = true
                clickedButton.text = if (planTier == "WEEKLY") "Subscribe Weekly" else "Subscribe Monthly"
                Toast.makeText(this@BillingActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}