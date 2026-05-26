package edu.cit.fernandez.goatedmeals.mobile.api

data class ScheduleRequest(
    val mealId: Long,
    val deliveryDate: String,
    val deliveryTime: String,
    val deliveryMethod: String,
    val deliveryAddress: String
)