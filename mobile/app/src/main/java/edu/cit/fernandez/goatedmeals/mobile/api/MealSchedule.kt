package edu.cit.fernandez.goatedmeals.mobile.api

data class MealSchedule(
    val id: Long,
    val meal: Meal, // Re-uses your existing Meal data class!
    val deliveryDay: String?,
    val deliveryTime: String?,
    val status: String,
    val deliveryMethod: String?,
    val deliveryAddress: String?
)

data class CancelResponse(
    val message: String?,
    val error: String?
)