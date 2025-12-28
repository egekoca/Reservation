package com.sorareservation.model

/**
 * Seat status enum
 */
enum class SeatStatus {
    AVAILABLE,    // Seat is available for booking
    OCCUPIED,     // Seat is already booked
    SELECTED      // Seat is selected by current user (temporary state)
}

/**
 * Seat model representing a seat in a trip
 */
data class Seat(
    val number: Int,
    var status: SeatStatus = SeatStatus.AVAILABLE
) {
    /**
     * Check if seat is available for booking
     */
    fun isAvailable(): Boolean = status == SeatStatus.AVAILABLE
    
    /**
     * Check if seat is selected by current user
     */
    fun isSelected(): Boolean = status == SeatStatus.SELECTED
    
    /**
     * Check if seat is occupied/booked
     */
    fun isOccupied(): Boolean = status == SeatStatus.OCCUPIED
}

