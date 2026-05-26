package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.fernandez.goatedmeals.mobile.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillingActivity : AppCompatActivity() {

    private lateinit var tvCurrentCredits: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        tvCurrentCredits = findViewById(R.id.tvCurrentCredits)
        val btnWeekly = findViewById<Button>(R.id.btnWeeklyPlan)
        val btnMonthly = findViewById<Button>(R.id.btnMonthlyPlan)

        // 1. Fetch current credits on load
        fetchSubscriptionStatus()

        // 2. Setup Purchase Buttons
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
        RetrofitClient.getInstance(this).getMySubscription().enqueue(object : Callback<SubscriptionResponse> {
            override fun onResponse(call: Call<SubscriptionResponse>, response: Response<SubscriptionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val credits = response.body()!!.availableCredits
                    tvCurrentCredits.text = "Available Credits: $credits"
                }
            }
            override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
                tvCurrentCredits.text = "Available Credits: Error"
            }
        })
    }

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
                        // THIS IS THE MAGIC: Open the URL in the phone's web browser!
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