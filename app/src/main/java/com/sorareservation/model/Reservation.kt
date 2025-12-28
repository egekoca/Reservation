package com.sorareservation.model

import java.util.Date
import java.util.UUID

/**
 * Reservation model representing a user's booking
 */
data class Reservation(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val tripId: UUID,
    val seatNumbers: List<Int>,
    val totalPrice: Double,
    val reservationDate: Date = Date(),
    val trip: Trip? = null // Optional: can be loaded from SeferLab when needed
) {
    /**
     * Get formatted seat numbers string
     */
    fun getSeatNumbersString(): String {
        return seatNumbers.sorted().joinToString(", ")
    }
    
    /**
     * Get reservation summary for sharing
     */
    fun getSummary(): String {
        val tripInfo = trip?.let {
            "${it.departureCity} → ${it.arrivalCity}\n" +
            "Date: ${android.text.format.DateFormat.format("dd/MM/yyyy", it.departureDate)}\n" +
            "Time: ${it.departureTime}\n"
        } ?: ""
        
        return "Reservation ID: ${id}\n" +
               tripInfo +
               "Seats: ${getSeatNumbersString()}\n" +
               "Total Price: $totalPrice TL"
    }
}

