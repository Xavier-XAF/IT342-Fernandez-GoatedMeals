package edu.cit.fernandez.goatedmeals.mobile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // NEW
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // NEW
import edu.cit.fernandez.goatedmeals.mobile.api.Meal

class MealAdapter(private val mealList: List<Meal>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMealImage: ImageView = itemView.findViewById(R.id.ivMealImage) // NEW
        val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)
        val tvMealPrice: TextView = itemView.findViewById(R.id.tvMealPrice)
        val tvMealDescription: TextView = itemView.findViewById(R.id.tvMealDescription)
        val btnOrder: Button = itemView.findViewById(R.id.btnOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]

        holder.tvMealName.text = meal.name
        holder.tvMealDescription.text = meal.description

        // --- NEW: Display Credits instead of PHP currency! ---
        holder.tvMealPrice.text = "1 Credit"

        // Tell Glide to load the URL into the ImageView
        // We use clearColorFilter() to remove the grey tint we added to the placeholder in XML
        // 1. Remove the grey XML tint so the image colors can shine through!
        holder.ivMealImage.clearColorFilter()

        // 2. Tell Glide to load the URL into the ImageView
        Glide.with(holder.itemView.context)
            .load(meal.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Shows while loading
            .into(holder.ivMealImage)

        holder.btnOrder.setOnClickListener {
            val intent = Intent(holder.itemView.context, ScheduleActivity::class.java)
            intent.putExtra("MEAL_ID", meal.id)
            intent.putExtra("MEAL_NAME", meal.name)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = mealList.size
}