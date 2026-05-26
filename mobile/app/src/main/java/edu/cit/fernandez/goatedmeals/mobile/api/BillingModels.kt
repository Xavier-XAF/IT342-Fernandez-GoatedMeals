package edu.cit.fernandez.goatedmeals.mobile.api

// For GET /api/v1/subscriptions/me
data class SubscriptionResponse(
    val hasSubscription: Boolean,
    val planTier: String?,
    val availableCredits: Int
)

// For POST /api/v1/subscriptions/pay
data class PaymentRequest(
    val planTier: String,
    val amount: Double
)

data class PaymentResponse(
    val success: Boolean,
    val data: PaymentData?
)

data class PaymentData(
    val checkoutUrl: String
)