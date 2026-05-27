package edu.cit.fernandez.goatedmeals.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.cit.fernandez.goatedmeals.mobile.api.CancelResponse
import edu.cit.fernandez.goatedmeals.mobile.api.MealSchedule
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private lateinit var rvSchedules: RecyclerView
    private lateinit var adapter: ScheduleAdapter
    private lateinit var tvEmptySchedule: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSchedules = view.findViewById(R.id.rvSchedule)
        tvEmptySchedule = view.findViewById(R.id.tvEmptySchedule)

        rvSchedules.layoutManager = LinearLayoutManager(requireContext())

        fetchSchedules()
    }

    private fun fetchSchedules() {
        RetrofitClient.getInstance(requireContext()).getMySchedules().enqueue(object : Callback<List<MealSchedule>> {
            override fun onResponse(call: Call<List<MealSchedule>>, response: Response<List<MealSchedule>>) {
                if (response.isSuccessful && response.body() != null) {
                    val rawSchedules = response.body()!!

                    if (rawSchedules.isEmpty()) {
                        rvSchedules.visibility = View.GONE
                        tvEmptySchedule.visibility = View.VISIBLE
                    } else {
                        rvSchedules.visibility = View.VISIBLE
                        tvEmptySchedule.visibility = View.GONE

                        // --- 1. THE SORTING ALGORITHM ---
                        // Define priority weights (Lower number = Higher at the top of the screen)
                        val statusWeights = mapOf(
                            "SCHEDULED" to 1,
                            "PREPARING" to 2,
                            "DELIVERING" to 3,
                            "DELIVERED" to 4
                        )

                        // Sort the list based on the weights above (If status is weird/null, it goes to the bottom '5')
                        val sortedSchedules = rawSchedules.sortedBy {
                            statusWeights[it.status?.uppercase()] ?: 5
                        }

                        adapter = ScheduleAdapter(sortedSchedules.toMutableList()) { scheduleId, position ->
                            cancelOrder(scheduleId, position)
                        }
                        rvSchedules.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<MealSchedule>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelOrder(scheduleId: Long, position: Int) {
        RetrofitClient.getInstance(requireContext()).cancelSchedule(scheduleId).enqueue(object : Callback<CancelResponse> {
            override fun onResponse(call: Call<CancelResponse>, response: Response<CancelResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Canceled! 1 Credit Refunded.", Toast.LENGTH_LONG).show()
                    adapter.removeItem(position)
                } else {
                    Toast.makeText(requireContext(), "Cannot cancel this order.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<CancelResponse>, t: Throwable) {}
        })
    }
}

// --- THE UPDATED ADAPTER ---
class ScheduleAdapter(
    private val schedules: MutableList<MealSchedule>,
    private val onCancelClick: (Long, Int) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivScheduledMealImage: ImageView = view.findViewById(R.id.ivScheduledMealImage) // NEW
        val tvDeliveryDay: TextView = view.findViewById(R.id.tvDeliveryDay)
        val tvDeliveryTime: TextView = view.findViewById(R.id.tvDeliveryTime)
        val tvScheduledMealName: TextView = view.findViewById(R.id.tvScheduledMealName)
        val tvScheduledStatus: TextView = view.findViewById(R.id.tvScheduledStatus)
        val btnCancelSchedule: ImageView = view.findViewById(R.id.btnCancelSchedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scheduled_meal, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]

        holder.tvScheduledMealName.text = schedule.meal.name
        holder.tvScheduledStatus.text = "Status: ${schedule.status}"

        // 1. Safely extract the raw strings
        val rawDay = schedule.deliveryDay ?: "TBD"
        val rawTime = schedule.deliveryTime ?: "TBD"

        // 2. The Date Fix: Convert "2026-05-13" into "MAY 13"
        val displayDate = try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            val parsedDate = inputFormat.parse(rawDay)
            if (parsedDate != null) outputFormat.format(parsedDate).uppercase() else "TBD"
        } catch (e: Exception) {
            rawDay.take(6).uppercase() // Fallback just in case!
        }

        // 3. The Time Fix: Catch the word "null"
        val displayTime = if (rawTime == "null" || rawTime.isEmpty()) "TBD" else rawTime

        // Apply the formatted texts
        holder.tvDeliveryDay.text = displayDate
        holder.tvDeliveryTime.text = displayTime

        // 4. LOAD THE IMAGE
        holder.ivScheduledMealImage.clearColorFilter()
        com.bumptech.glide.Glide.with(holder.itemView.context)
            .load(schedule.meal.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivScheduledMealImage)

        // 5. THE CANCELLATION CONSTRAINT
        if (schedule.status?.uppercase() == "SCHEDULED") {
            holder.btnCancelSchedule.visibility = View.VISIBLE
            holder.btnCancelSchedule.setOnClickListener { onCancelClick(schedule.id, position) }
            holder.tvScheduledStatus.setTextColor(android.graphics.Color.parseColor("#0FFF50")) // Green for active!
        } else {
            holder.btnCancelSchedule.visibility = View.GONE
            holder.tvScheduledStatus.setTextColor(android.graphics.Color.parseColor("#A0A0A0")) // Grey for past/processing
        }
    }

    override fun getItemCount() = schedules.size

    fun removeItem(position: Int) {
        schedules.removeAt(position)
        notifyItemRemoved(position)
    }
}