package edu.cit.fernandez.goatedmeals.mobile.api

data class ScheduleResponse(
    val message: String?,
    val error: String?,
    val remainingCredits: Int?
)