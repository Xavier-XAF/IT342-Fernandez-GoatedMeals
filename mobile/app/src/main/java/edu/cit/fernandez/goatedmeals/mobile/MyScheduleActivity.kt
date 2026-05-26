package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.fernandez.goatedmeals.mobile.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScheduleActivity : AppCompatActivity() {

    private lateinit var rvSchedules: RecyclerView
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)

        rvSchedules = findViewById(R.id.rvSchedules)
        rvSchedules.layoutManager = LinearLayoutManager(this)

        setupBottomNav()
        fetchSchedules()
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationViewSchedule).selectedItemId = R.id.nav_cart
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationViewSchedule)
        bottomNav.selectedItemId = R.id.nav_cart // Highlight the Cart icon!

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    false
                }
                R.id.nav_cart -> true
                // We will link Profile in the next step!
                else -> false


            }
        }
    }

    private fun fetchSchedules() {
        RetrofitClient.getInstance(this).getMySchedules().enqueue(object : Callback<List<MealSchedule>> {
            override fun onResponse(call: Call<List<MealSchedule>>, response: Response<List<MealSchedule>>) {
                if (response.isSuccessful && response.body() != null) {
                    val schedules = response.body()!!
                    adapter = ScheduleAdapter(schedules.toMutableList()) { scheduleId, position ->
                        cancelOrder(scheduleId, position)
                    }
                    rvSchedules.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<MealSchedule>>, t: Throwable) {
                Toast.makeText(this@MyScheduleActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelOrder(scheduleId: Long, position: Int) {
        RetrofitClient.getInstance(this).cancelSchedule(scheduleId).enqueue(object : Callback<CancelResponse> {
            override fun onResponse(call: Call<CancelResponse>, response: Response<CancelResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MyScheduleActivity, "Canceled! 1 Credit Refunded.", Toast.LENGTH_LONG).show()
                    adapter.removeItem(position)
                } else {
                    Toast.makeText(this@MyScheduleActivity, "Cannot cancel this order.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<CancelResponse>, t: Throwable) {}
        })
    }
}

// --- The Adapter to display the list ---
class ScheduleAdapter(
    private val schedules: MutableList<MealSchedule>,
    private val onCancelClick: (Long, Int) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderMealName: TextView = view.findViewById(R.id.tvOrderMealName)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderDetails: TextView = view.findViewById(R.id.tvOrderDetails)
        val btnCancelOrder: Button = view.findViewById(R.id.btnCancelOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.tvOrderMealName.text = schedule.meal.name
        holder.tvOrderStatus.text = schedule.status
        holder.tvOrderDetails.text = "${schedule.deliveryDay} at ${schedule.deliveryTime}\nMethod: ${schedule.deliveryMethod}"

        // Only show Cancel button if status is SCHEDULED (just like your Spring Boot rule!)
        if (schedule.status == "SCHEDULED") {
            holder.btnCancelOrder.visibility = View.VISIBLE
            holder.btnCancelOrder.setOnClickListener { onCancelClick(schedule.id, position) }
        } else {
            holder.btnCancelOrder.visibility = View.GONE
        }
    }

    override fun getItemCount() = schedules.size

    fun removeItem(position: Int) {
        schedules.removeAt(position)
        notifyItemRemoved(position)
    }
}