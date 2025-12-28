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
     * Gender information is preserved
     */
    fun bookSeats(seatNumbers: List<Int>): Boolean {
        seatNumbers.forEach { seatNumber ->
            val seat = seats.find { it.number == seatNumber }
            if (seat != null && seat.isSelected()) {
                seat.status = SeatStatus.OCCUPIED
                // Gender is already set during selection, keep it
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
     * Select seat with gender
     */
    fun selectSeatWithGender(seatNumber: Int, gender: Gender): Boolean {
        val seat = seats.find { it.number == seatNumber }
        if (seat != null && seat.isAvailable()) {
            // Check if adjacent seats have opposite gender
            if (!canSelectSeatWithGender(seatNumber, gender)) {
                return false
            }
            seat.status = SeatStatus.SELECTED
            seat.gender = gender
            return true
        }
        return false
    }
    
    /**
     * Check if seat can be selected with given gender
     * Rule: Men can sit next to men, women can sit next to women
     */
    fun canSelectSeatWithGender(seatNumber: Int, gender: Gender): Boolean {
        // Get adjacent seats (left and right in the same row)
        // Bus layout: typically 2-2 or 2-1 configuration
        // For simplicity, we'll check seats that are next to each other numerically
        // In a real bus: seats 1-2, 3-4, 5-6, etc. might be pairs
        
        val adjacentSeats = getAdjacentSeats(seatNumber)
        
        for (adjSeatNum in adjacentSeats) {
            val adjSeat = seats.find { it.number == adjSeatNum }
            if (adjSeat != null && adjSeat.isOccupied()) {
                val adjGender = adjSeat.gender
                if (adjGender != null && adjGender != gender) {
                    // Adjacent seat is occupied by opposite gender
                    return false
                }
            }
        }
        return true
    }
    
    /**
     * Get adjacent seat numbers (seats in the same row)
     * Bus layout: 2-2 configuration (most common)
     * Seats are arranged in pairs: (1,2), (3,4), (5,6), etc.
     */
    private fun getAdjacentSeats(seatNumber: Int): List<Int> {
        val adjacent = mutableListOf<Int>()
        
        // If seat is odd (left side of pair), check seat+1
        // If seat is even (right side of pair), check seat-1
        if (seatNumber % 2 == 1) {
            // Odd number (left side)
            if (seatNumber < totalSeats) {
                adjacent.add(seatNumber + 1)
            }
        } else {
            // Even number (right side)
            if (seatNumber > 1) {
                adjacent.add(seatNumber - 1)
            }
        }
        
        return adjacent
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
        seats.filter { it.isSelected() }.forEach {
            it.status = SeatStatus.AVAILABLE
            it.gender = null
        }
    }
}

