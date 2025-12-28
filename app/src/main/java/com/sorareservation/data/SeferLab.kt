package com.sorareservation.data

import com.sorareservation.model.*
import java.util.*

/**
 * Singleton class for managing trips, users, and reservations
 * Similar to CrimeLab pattern from Android Programming book
 */
object SeferLab {
    
    // Mock data storage
    private val users = mutableListOf<User>()
    private val trips = mutableListOf<Trip>()
    private val reservations = mutableListOf<Reservation>()
    
    // Current logged in user
    private var currentUser: User? = null
    
    init {
        // Initialize with mock data
        initializeMockData()
    }
    
    /**
     * Initialize mock data for testing
     */
    private fun initializeMockData() {
        // Create pre-made users
        val adminUser = User(
            email = "admin@sorareservation.com",
            password = "admin123",
            fullName = "Admin User",
            phone = "555-0001",
            isAdmin = true
        )
        
        val regularUser1 = User(
            email = "user1@example.com",
            password = "user123",
            fullName = "John Doe",
            phone = "555-1001"
        )
        
        val regularUser2 = User(
            email = "user2@example.com",
            password = "user123",
            fullName = "Jane Smith",
            phone = "555-1002"
        )
        
        users.addAll(listOf(adminUser, regularUser1, regularUser2))
        
        // Create pre-made trips
        val calendar = Calendar.getInstance()
        
        // Trip 1: Istanbul to Ankara (today)
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        val trip1 = Trip(
            departureCity = "Istanbul",
            arrivalCity = "Ankara",
            departureDate = calendar.time,
            departureTime = "10:00",
            price = 250.0
        )
        
        // Trip 2: Ankara to Izmir (today)
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 30)
        val trip2 = Trip(
            departureCity = "Ankara",
            arrivalCity = "Izmir",
            departureDate = calendar.time,
            departureTime = "14:30",
            price = 300.0
        )
        
        // Trip 3: Istanbul to Antalya (tomorrow)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        val trip3 = Trip(
            departureCity = "Istanbul",
            arrivalCity = "Antalya",
            departureDate = calendar.time,
            departureTime = "08:00",
            price = 350.0
        )
        
        // Trip 4: Izmir to Ankara (tomorrow)
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 0)
        val trip4 = Trip(
            departureCity = "Izmir",
            arrivalCity = "Ankara",
            departureDate = calendar.time,
            departureTime = "16:00",
            price = 280.0
        )
        
        // Trip 5: Ankara to Istanbul (day after tomorrow)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        val trip5 = Trip(
            departureCity = "Ankara",
            arrivalCity = "Istanbul",
            departureDate = calendar.time,
            departureTime = "12:00",
            price = 250.0
        )
        
        // Book some seats in trip1 for testing (with gender)
        trip1.seats[0].status = SeatStatus.OCCUPIED
        trip1.seats[0].gender = Gender.MALE
        trip1.seats[1].status = SeatStatus.OCCUPIED
        trip1.seats[1].gender = Gender.MALE
        trip1.seats[2].status = SeatStatus.OCCUPIED
        trip1.seats[2].gender = Gender.FEMALE
        
        // Book some seats in trip2 for testing (with gender)
        trip2.seats[5].status = SeatStatus.OCCUPIED
        trip2.seats[5].gender = Gender.MALE
        trip2.seats[6].status = SeatStatus.OCCUPIED
        trip2.seats[6].gender = Gender.FEMALE
        
        trips.addAll(listOf(trip1, trip2, trip3, trip4, trip5))
        
        // Create some pre-made reservations for user1
        val reservation1 = Reservation(
            userId = regularUser1.id,
            tripId = trip1.id,
            seatNumbers = listOf(10, 11),
            totalPrice = 500.0,
            trip = trip1
        )
        
        val reservation2 = Reservation(
            userId = regularUser1.id,
            tripId = trip2.id,
            seatNumbers = listOf(20),
            totalPrice = 300.0,
            trip = trip2
        )
        
        reservations.addAll(listOf(reservation1, reservation2))
    }
    
    // ========== User Operations ==========
    
    /**
     * Login user with email and password
     */
    fun login(email: String, password: String): User? {
        val user = users.find { it.email == email && it.password == password }
        if (user != null) {
            currentUser = user
        }
        return user
    }
    
    /**
     * Register a new user
     */
    fun register(email: String, password: String, fullName: String, phone: String): User? {
        // Check if user already exists
        if (users.any { it.email == email }) {
            return null
        }
        
        val newUser = User(
            email = email,
            password = password,
            fullName = fullName,
            phone = phone
        )
        users.add(newUser)
        return newUser
    }
    
    /**
     * Get current logged in user
     */
    fun getCurrentUser(): User? = currentUser
    
    /**
     * Logout current user
     */
    fun logout() {
        currentUser = null
    }
    
    /**
     * Check if current user is admin
     */
    fun isCurrentUserAdmin(): Boolean {
        return currentUser?.isAdmin == true
    }
    
    // ========== Trip Operations ==========
    
    /**
     * Get all trips
     */
    fun getTrips(): List<Trip> {
        return trips.toList()
    }
    
    /**
     * Get trip by ID
     */
    fun getTrip(id: UUID): Trip? {
        return trips.find { it.id == id }
    }
    
    /**
     * Add a new trip (admin only)
     */
    fun addTrip(trip: Trip): Boolean {
        if (!isCurrentUserAdmin()) {
            return false
        }
        trips.add(trip)
        return true
    }
    
    /**
     * Delete a trip (admin only)
     */
    fun deleteTrip(tripId: UUID): Boolean {
        if (!isCurrentUserAdmin()) {
            return false
        }
        val trip = trips.find { it.id == tripId }
        if (trip != null) {
            trips.remove(trip)
            // Also remove related reservations
            reservations.removeAll { it.tripId == tripId }
            return true
        }
        return false
    }
    
    /**
     * Search trips by departure and arrival cities
     */
    fun searchTrips(departureCity: String?, arrivalCity: String?): List<Trip> {
        return trips.filter { trip ->
            (departureCity.isNullOrBlank() || trip.departureCity.contains(departureCity, ignoreCase = true)) &&
            (arrivalCity.isNullOrBlank() || trip.arrivalCity.contains(arrivalCity, ignoreCase = true))
        }
    }
    
    // ========== Reservation Operations ==========
    
    /**
     * Get all reservations for current user
     */
    fun getReservationsForCurrentUser(): List<Reservation> {
        val userId = currentUser?.id ?: return emptyList()
        return reservations.filter { it.userId == userId }.map { reservation ->
            // Load trip information
            reservation.copy(trip = getTrip(reservation.tripId))
        }
    }
    
    /**
     * Get reservation by ID
     */
    fun getReservation(id: UUID): Reservation? {
        return reservations.find { it.id == id }?.let { reservation ->
            reservation.copy(trip = getTrip(reservation.tripId))
        }
    }
    
    /**
     * Create a new reservation
     */
    fun createReservation(tripId: UUID, seatNumbers: List<Int>): Reservation? {
        val user = currentUser ?: return null
        val trip = getTrip(tripId) ?: return null
        
        // Validate seats
        val invalidSeats = seatNumbers.filter { seatNum ->
            val seat = trip.seats.find { it.number == seatNum }
            seat == null || !seat.isSelected()
        }
        
        if (invalidSeats.isNotEmpty()) {
            return null
        }
        
        // Calculate total price
        val totalPrice = trip.price * seatNumbers.size
        
        // Book the seats
        trip.bookSeats(seatNumbers)
        
        // Create reservation
        val reservation = Reservation(
            userId = user.id,
            tripId = tripId,
            seatNumbers = seatNumbers,
            totalPrice = totalPrice,
            trip = trip
        )
        
        reservations.add(reservation)
        return reservation
    }
    
    /**
     * Cancel a reservation
     */
    fun cancelReservation(reservationId: UUID): Boolean {
        val user = currentUser ?: return false
        val reservation = reservations.find { it.id == reservationId && it.userId == user.id }
            ?: return false
        
        // Free up the seats
        val trip = getTrip(reservation.tripId)
        trip?.let {
            reservation.seatNumbers.forEach { seatNumber ->
                val seat = it.seats.find { s -> s.number == seatNumber }
                seat?.status = SeatStatus.AVAILABLE
            }
        }
        
        reservations.remove(reservation)
        return true
    }
}

