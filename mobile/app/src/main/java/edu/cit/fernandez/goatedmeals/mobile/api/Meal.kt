package edu.cit.fernandez.goatedmeals.mobile.api

data class Meal(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val category: String?,
    val isAvailable: Boolean = true
)