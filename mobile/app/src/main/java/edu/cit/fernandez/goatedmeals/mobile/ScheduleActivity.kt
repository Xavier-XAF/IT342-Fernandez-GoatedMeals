package edu.cit.fernandez.goatedmeals.mobile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import edu.cit.fernandez.goatedmeals.mobile.api.ScheduleRequest
import edu.cit.fernandez.goatedmeals.mobile.api.ScheduleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private var selectedMealId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        selectedMealId = intent.getLongExtra("MEAL_ID", -1)
        val mealName = intent.getStringExtra("MEAL_NAME") ?: "Unknown Meal"

        val tvTitle = findViewById<TextView>(R.id.tvScheduleTitle)
        val etDate = findViewById<EditText>(R.id.etDeliveryDate)
        val etTime = findViewById<EditText>(R.id.etDeliveryTime)
        val spinnerMethod = findViewById<Spinner>(R.id.spinnerMethod) // Changed to Spinner
        val etAddress = findViewById<EditText>(R.id.etDeliveryAddress)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmSchedule)

        tvTitle.text = "Schedule:\n$mealName"

        // --- 1. SPINNER (Dropdown) SETUP ---
        val methods = arrayOf("Delivery", "Pickup")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, methods)
        spinnerMethod.adapter = adapter

        // --- 2. DATE PICKER (Calendar) SETUP ---
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format for your Spring Boot backend (YYYY-MM-DD)
                val formattedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
                etDate.setText(formattedDate)
            }, year, month, day).show()
        }

        // --- 3. TIME PICKER (Clock) SETUP ---
        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Format nicely
                val formattedTime = "${String.format("%02d", selectedHour)}:${String.format("%02d", selectedMinute)}"
                etTime.setText(formattedTime)
            }, hour, minute, false).show() // 'false' means 12-hour AM/PM format
        }


        // --- 4. SUBMIT ORDER ---
        btnConfirm.setOnClickListener {
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val method = spinnerMethod.selectedItem.toString() // Get dropdown value
            val address = etAddress.text.toString().trim()

            if (selectedMealId == -1L || date.isEmpty() || time.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill out all delivery details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ScheduleRequest(selectedMealId, date, time, method, address)

            btnConfirm.isEnabled = false
            btnConfirm.text = "Booking..."

            RetrofitClient.getInstance(this).bookMeal(request).enqueue(object : Callback<ScheduleResponse> {
                override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm Booking"

                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@ScheduleActivity, "Success! Meal Scheduled.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ScheduleActivity, "Booking failed. Ensure you have credits.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm Booking"
                    Toast.makeText(this@ScheduleActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}