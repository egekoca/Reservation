package com.sorareservation.model

import java.util.Date
import java.util.UUID

/**
 * Trip model representing a bus trip
 */
data class Trip(
    val id: UUID = UUID.randomUUID(),
    val departureCity: String,
    val arrivalCity: String,
    val departureDate: Date,
    val departureTime: String, // Format: "HH:mm"
    val price: Double,
    val totalSeats: Int = 45, // Default bus has 45 seats
    val seats: MutableList<Seat> = mutableListOf()
) {
    init {
        // Initialize seats if empty
        if (seats.isEmpty()) {
            for (i in 1..totalSeats) {
                seats.add(Seat(number = i, status = SeatStatus.AVAILABLE))
            }
        }
    }
    
    /**
     * Get available seats count
     */
    fun getAvailableSeatsCount(): Int {
        return seats.count { it.isAvailable() }
    }
    
    /**
     * Get occupied seats count
     */
    fun getOccupiedSeatsCount(): Int {
        return seats.count { it.isOccupied() }
    }
    
    /**
     * Get selected seats (temporary selection)
     */
    fun getSelectedSeats(): List<Seat> {
        return seats.filter { it.isSelected() }
    }
    
    /**
     * Book seats (change status from SELECTED to OCCUPIED)
     */
    fun bookSeats(seatNumbers: List<Int>): Boolean {
        seatNumbers.forEach { seatNumber ->
            val seat = seats.find { it.number == seatNumber }
            if (seat != null && seat.isSelected()) {
                seat.status = SeatStatus.OCCUPIED
            } else {
                return false
            }
        }
        return true
    }
    
    /**
     * Select seats (change status from AVAILABLE to SELECTED)
     */
    fun selectSeats(seatNumbers: List<Int>): Boolean {
        seatNumbers.forEach { seatNumber ->
            val seat = seats.find { it.number == seatNumber }
            if (seat != null && seat.isAvailable()) {
                seat.status = SeatStatus.SELECTED
            } else {
                return false
            }
        }
        return true
    }
    
    /**
     * Deselect seats (change status from SELECTED to AVAILABLE)
     */
    fun deselectSeats(seatNumbers: List<Int>) {
        seatNumbers.forEach { seatNumber ->
            val seat = seats.find { it.number == seatNumber }
            if (seat != null && seat.isSelected()) {
                seat.status = SeatStatus.AVAILABLE
            }
        }
    }
    
    /**
     * Clear all selected seats
     */
    fun clearSelectedSeats() {
        seats.filter { it.isSelected() }.forEach { it.status = SeatStatus.AVAILABLE }
    }
}

