package edu.cit.fernandez.goatedmeals.mobile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.fernandez.goatedmeals.mobile.api.Meal
import edu.cit.fernandez.goatedmeals.mobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var rvMeals: RecyclerView
    private lateinit var adapter: MealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Hook into the UI
        rvMeals = view.findViewById(R.id.rvMeals)
        rvMeals.layoutManager = GridLayoutManager(requireContext(), 2)

        // 2. Fetch real data instead of dummy data!
        fetchRealMenu()
    }

    private fun fetchRealMenu() {
        RetrofitClient.getInstance(requireContext()).getMealCatalog().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful && response.body() != null) {
                    val realMeals = response.body()!!

                    // Attach the live database meals to your existing adapter
                    adapter = MealAdapter(realMeals)
                    rvMeals.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to load menu from server.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}