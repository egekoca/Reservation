package com.sorareservation.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.sorareservation.model.*
import java.util.*

/**
 * Singleton class for managing trips, users, and reservations
 * Similar to CrimeLab pattern from Android Programming book
 * Now uses SQLite database instead of mock data
 */
object SeferLab {
    
    private var context: Context? = null
    private var database: SQLiteDatabase? = null
    
    // Current logged in user (stored in memory for session management)
    private var currentUser: User? = null
    
    /**
     * Initialize SeferLab with application context
     * Must be called once in Application.onCreate()
     */
    fun init(context: Context) {
        this.context = context.applicationContext
        this.database = SeferDbHelper(this.context!!).writableDatabase
        
        // Check if database is empty, if so load mock data
        if (isDatabaseEmpty()) {
            initializeMockData()
        }
    }
    
    /**
     * Check if database is empty (no users)
     */
    private fun isDatabaseEmpty(): Boolean {
        val cursor = database?.query(
            SeferDbSchema.UserTable.NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            "1"
        )
        val isEmpty = cursor?.count == 0
        cursor?.close()
        return isEmpty
    }
    
    /**
     * Initialize initial/seed data for testing
     * Only called if database is empty (first launch)
     * These data are permanently stored in the database
     */
    private fun initializeMockData() {
        val db = database ?: return
        
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
        
        // Insert users into database
        insertUser(adminUser)
        insertUser(regularUser1)
        insertUser(regularUser2)
        
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
        
        // Insert trips into database
        insertTrip(trip1)
        insertTrip(trip2)
        insertTrip(trip3)
        insertTrip(trip4)
        insertTrip(trip5)
        
        // Book some seats in trip1 for testing (with gender) - More seats occupied
        // Male seats (left side pairs)
        updateSeatStatus(trip1.id, 1, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip1.id, 2, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip1.id, 4, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip1.id, 5, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip1.id, 7, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip1.id, 8, SeatStatus.OCCUPIED, Gender.MALE)
        // Female seats
        updateSeatStatus(trip1.id, 3, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip1.id, 6, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip1.id, 9, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip1.id, 12, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip1.id, 13, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip1.id, 15, SeatStatus.OCCUPIED, Gender.FEMALE)
        
        // Book some seats in trip2 for testing (with gender) - More seats occupied
        // Male seats
        updateSeatStatus(trip2.id, 1, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip2.id, 2, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip2.id, 4, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip2.id, 5, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip2.id, 6, SeatStatus.OCCUPIED, Gender.MALE)
        // Female seats
        updateSeatStatus(trip2.id, 7, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip2.id, 8, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip2.id, 9, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip2.id, 10, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip2.id, 11, SeatStatus.OCCUPIED, Gender.FEMALE)
        
        // Book some seats in trip3 for testing
        updateSeatStatus(trip3.id, 1, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip3.id, 2, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip3.id, 3, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip3.id, 4, SeatStatus.OCCUPIED, Gender.MALE)
        updateSeatStatus(trip3.id, 5, SeatStatus.OCCUPIED, Gender.MALE)
        
        // Book some seats in trip4 for testing
        updateSeatStatus(trip4.id, 6, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip4.id, 7, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip4.id, 8, SeatStatus.OCCUPIED, Gender.FEMALE)
        updateSeatStatus(trip4.id, 9, SeatStatus.OCCUPIED, Gender.FEMALE)
        
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
        
        // Insert reservations into database
        insertReservation(reservation1)
        insertReservation(reservation2)
        
        // Update seats for reservations (mark as OCCUPIED)
        reservation1.seatNumbers.forEach { seatNum ->
            updateSeatStatus(trip1.id, seatNum, SeatStatus.OCCUPIED, null)
        }
        reservation2.seatNumbers.forEach { seatNum ->
            updateSeatStatus(trip2.id, seatNum, SeatStatus.OCCUPIED, null)
        }
    }
    
    // ========== Helper Methods for Database Operations ==========
    
    /**
     * Insert user into database
     */
    private fun insertUser(user: User) {
        val values = ContentValues().apply {
            put(SeferDbSchema.UserTable.Cols.ID, user.id.toString())
            put(SeferDbSchema.UserTable.Cols.EMAIL, user.email)
            put(SeferDbSchema.UserTable.Cols.PASSWORD, user.password)
            put(SeferDbSchema.UserTable.Cols.FULL_NAME, user.fullName)
            put(SeferDbSchema.UserTable.Cols.PHONE, user.phone)
            put(SeferDbSchema.UserTable.Cols.IS_ADMIN, if (user.isAdmin) 1 else 0)
        }
        database?.insert(SeferDbSchema.UserTable.NAME, null, values)
    }
    
    /**
     * Insert trip into database (with seats)
     */
    private fun insertTrip(trip: Trip) {
        val values = ContentValues().apply {
            put(SeferDbSchema.TripTable.Cols.ID, trip.id.toString())
            put(SeferDbSchema.TripTable.Cols.DEPARTURE_CITY, trip.departureCity)
            put(SeferDbSchema.TripTable.Cols.ARRIVAL_CITY, trip.arrivalCity)
            put(SeferDbSchema.TripTable.Cols.DEPARTURE_DATE, trip.departureDate.time)
            put(SeferDbSchema.TripTable.Cols.DEPARTURE_TIME, trip.departureTime)
            put(SeferDbSchema.TripTable.Cols.PRICE, trip.price)
            put(SeferDbSchema.TripTable.Cols.TOTAL_SEATS, trip.totalSeats)
        }
        database?.insert(SeferDbSchema.TripTable.NAME, null, values)
        
        // Insert seats for this trip
        for (i in 1..trip.totalSeats) {
            val seatValues = ContentValues().apply {
                put(SeferDbSchema.SeatTable.Cols.ID, UUID.randomUUID().toString())
                put(SeferDbSchema.SeatTable.Cols.TRIP_ID, trip.id.toString())
                put(SeferDbSchema.SeatTable.Cols.SEAT_NUMBER, i)
                put(SeferDbSchema.SeatTable.Cols.STATUS, SeatStatus.AVAILABLE.ordinal)
                putNull(SeferDbSchema.SeatTable.Cols.GENDER)
            }
            database?.insert(SeferDbSchema.SeatTable.NAME, null, seatValues)
        }
    }
    
    /**
     * Update seat status and gender in database
     */
    private fun updateSeatStatus(tripId: UUID, seatNumber: Int, status: SeatStatus, gender: Gender?) {
        val values = ContentValues().apply {
            put(SeferDbSchema.SeatTable.Cols.STATUS, status.ordinal)
            if (gender != null) {
                put(SeferDbSchema.SeatTable.Cols.GENDER, gender.ordinal)
            } else {
                putNull(SeferDbSchema.SeatTable.Cols.GENDER)
            }
        }
        database?.update(
            SeferDbSchema.SeatTable.NAME,
            values,
            "${SeferDbSchema.SeatTable.Cols.TRIP_ID} = ? AND ${SeferDbSchema.SeatTable.Cols.SEAT_NUMBER} = ?",
            arrayOf(tripId.toString(), seatNumber.toString())
        )
    }
    
    /**
     * Insert reservation into database
     * Returns true if successful, false otherwise
     */
    private fun insertReservation(reservation: Reservation): Boolean {
        val values = ContentValues().apply {
            put(SeferDbSchema.ReservationTable.Cols.ID, reservation.id.toString())
            put(SeferDbSchema.ReservationTable.Cols.USER_ID, reservation.userId.toString())
            put(SeferDbSchema.ReservationTable.Cols.TRIP_ID, reservation.tripId.toString())
            put(SeferDbSchema.ReservationTable.Cols.SEAT_NUMBERS, reservation.seatNumbers.joinToString(","))
            put(SeferDbSchema.ReservationTable.Cols.TOTAL_PRICE, reservation.totalPrice)
            put(SeferDbSchema.ReservationTable.Cols.RESERVATION_DATE, reservation.reservationDate.time)
        }
        val result = database?.insert(SeferDbSchema.ReservationTable.NAME, null, values)
        return result != null && result != -1L
    }
    
    /**
     * Load seats for a trip from database
     */
    private fun loadSeatsForTrip(tripId: UUID): MutableList<Seat> {
        val seats = mutableListOf<Seat>()
        val cursor = database?.query(
            SeferDbSchema.SeatTable.NAME,
            null,
            "${SeferDbSchema.SeatTable.Cols.TRIP_ID} = ?",
            arrayOf(tripId.toString()),
            null,
            null,
            "${SeferDbSchema.SeatTable.Cols.SEAT_NUMBER} ASC"
        )
        
        cursor?.use {
            it.moveToFirst()
            val wrapper = SeatCursorWrapper(it)
            while (!it.isAfterLast) {
                seats.add(wrapper.getSeat())
                it.moveToNext()
            }
        }
        
        return seats
    }
    
    // ========== User Operations ==========
    
    /**
     * Login user with email and password
     */
    fun login(email: String, password: String): User? {
        val cursor = database?.query(
            SeferDbSchema.UserTable.NAME,
            null,
            "${SeferDbSchema.UserTable.Cols.EMAIL} = ? AND ${SeferDbSchema.UserTable.Cols.PASSWORD} = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val wrapper = UserCursorWrapper(it)
                val user = wrapper.getUser()
                currentUser = user
                return user
            }
        }
        return null
    }
    
    /**
     * Register a new user
     */
    fun register(email: String, password: String, fullName: String, phone: String): User? {
        // Check if user already exists
        val existingCursor = database?.query(
            SeferDbSchema.UserTable.NAME,
            null,
            "${SeferDbSchema.UserTable.Cols.EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null,
            "1"
        )
        
        existingCursor?.use {
            if (it.moveToFirst()) {
                return null // User already exists
            }
        }
        
        // Create new user
        val newUser = User(
            email = email,
            password = password,
            fullName = fullName,
            phone = phone
        )
        
        insertUser(newUser)
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
        val trips = mutableListOf<Trip>()
        val cursor = database?.query(
            SeferDbSchema.TripTable.NAME,
            null,
            null,
            null,
            null,
            null,
            "${SeferDbSchema.TripTable.Cols.DEPARTURE_DATE} ASC"
        )
        
        cursor?.use {
            it.moveToFirst()
            val wrapper = TripCursorWrapper(it)
            while (!it.isAfterLast) {
                val tripId = UUID.fromString(it.getString(it.getColumnIndex(SeferDbSchema.TripTable.Cols.ID)))
                val seats = loadSeatsForTrip(tripId)
                trips.add(wrapper.getTrip(seats))
                it.moveToNext()
            }
        }
        
        return trips
    }
    
    /**
     * Get trip by ID
     */
    fun getTrip(id: UUID): Trip? {
        val cursor = database?.query(
            SeferDbSchema.TripTable.NAME,
            null,
            "${SeferDbSchema.TripTable.Cols.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val wrapper = TripCursorWrapper(it)
                val seats = loadSeatsForTrip(id)
                return wrapper.getTrip(seats)
            }
        }
        return null
    }
    
    /**
     * Add a new trip (admin only)
     */
    fun addTrip(trip: Trip): Boolean {
        if (!isCurrentUserAdmin()) {
            return false
        }
        insertTrip(trip)
        return true
    }
    
    /**
     * Delete a trip (admin only)
     * CASCADE DELETE will automatically remove seats and reservations
     */
    fun deleteTrip(tripId: UUID): Boolean {
        if (!isCurrentUserAdmin()) {
            return false
        }
        
        val deleted = database?.delete(
            SeferDbSchema.TripTable.NAME,
            "${SeferDbSchema.TripTable.Cols.ID} = ?",
            arrayOf(tripId.toString())
        ) ?: 0
        
        return deleted > 0
    }
    
    /**
     * Search trips by departure and arrival cities
     */
    fun searchTrips(departureCity: String?, arrivalCity: String?): List<Trip> {
        val whereClause = StringBuilder()
        val whereArgs = mutableListOf<String>()
        
        if (!departureCity.isNullOrBlank()) {
            whereClause.append("${SeferDbSchema.TripTable.Cols.DEPARTURE_CITY} LIKE ?")
            whereArgs.add("%$departureCity%")
        }
        
        if (!arrivalCity.isNullOrBlank()) {
            if (whereClause.isNotEmpty()) {
                whereClause.append(" AND ")
            }
            whereClause.append("${SeferDbSchema.TripTable.Cols.ARRIVAL_CITY} LIKE ?")
            whereArgs.add("%$arrivalCity%")
        }
        
        val trips = mutableListOf<Trip>()
        val cursor = database?.query(
            SeferDbSchema.TripTable.NAME,
            null,
            if (whereClause.isEmpty()) null else whereClause.toString(),
            if (whereArgs.isEmpty()) null else whereArgs.toTypedArray(),
            null,
            null,
            "${SeferDbSchema.TripTable.Cols.DEPARTURE_DATE} ASC"
        )
        
        cursor?.use {
            it.moveToFirst()
            val wrapper = TripCursorWrapper(it)
            while (!it.isAfterLast) {
                val tripId = UUID.fromString(it.getString(it.getColumnIndex(SeferDbSchema.TripTable.Cols.ID)))
                val seats = loadSeatsForTrip(tripId)
                trips.add(wrapper.getTrip(seats))
                it.moveToNext()
            }
        }
        
        return trips
    }
    
    // ========== Reservation Operations ==========
    
    /**
     * Get all reservations for current user
     */
    fun getReservationsForCurrentUser(): List<Reservation> {
        val userId = currentUser?.id ?: return emptyList()
        
        val reservations = mutableListOf<Reservation>()
        val cursor = database?.query(
            SeferDbSchema.ReservationTable.NAME,
            null,
            "${SeferDbSchema.ReservationTable.Cols.USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${SeferDbSchema.ReservationTable.Cols.RESERVATION_DATE} DESC"
        )
        
        cursor?.use {
            it.moveToFirst()
            val wrapper = ReservationCursorWrapper(it)
            while (!it.isAfterLast) {
                val tripId = UUID.fromString(it.getString(it.getColumnIndex(SeferDbSchema.ReservationTable.Cols.TRIP_ID)))
                val trip = getTrip(tripId)
                reservations.add(wrapper.getReservation(trip))
                it.moveToNext()
            }
        }
        
        return reservations
    }
    
    /**
     * Get reservation by ID
     */
    fun getReservation(id: UUID): Reservation? {
        val cursor = database?.query(
            SeferDbSchema.ReservationTable.NAME,
            null,
            "${SeferDbSchema.ReservationTable.Cols.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val wrapper = ReservationCursorWrapper(it)
                val tripId = UUID.fromString(it.getString(it.getColumnIndex(SeferDbSchema.ReservationTable.Cols.TRIP_ID)))
                val trip = getTrip(tripId)
                return wrapper.getReservation(trip)
            }
        }
        return null
    }
    
    /**
     * Create a new reservation
     * Note: This method expects seats to be available (not occupied)
     * It will update them to OCCUPIED in database
     */
    fun createReservation(tripId: UUID, seatNumbers: List<Int>, gender: Gender?): Reservation? {
        val user = currentUser ?: return null
        val trip = getTrip(tripId) ?: return null
        
        // Validate seats (check if they exist and are not occupied)
        // Note: Seats might be SELECTED in memory (from ConfirmationFragment) but AVAILABLE in database
        val invalidSeats = seatNumbers.filter { seatNum ->
            val seat = trip.seats.find { it.number == seatNum }
            seat == null || seat.isOccupied()
        }
        
        if (invalidSeats.isNotEmpty()) {
            return null
        }
        
        // Calculate total price
        val totalPrice = trip.price * seatNumbers.size
        
        // Create reservation
        val reservation = Reservation(
            userId = user.id,
            tripId = tripId,
            seatNumbers = seatNumbers,
            totalPrice = totalPrice
        )
        
        // Insert reservation into database
        val insertSuccess = insertReservation(reservation)
        if (!insertSuccess) {
            return null
        }
        
        // Update seats in database: set status to OCCUPIED and set gender
        seatNumbers.forEach { seatNumber ->
            updateSeatStatus(tripId, seatNumber, SeatStatus.OCCUPIED, gender)
            // Also update in memory
            val seat = trip.seats.find { it.number == seatNumber }
            seat?.status = SeatStatus.OCCUPIED
            seat?.gender = gender
        }
        
        return reservation.copy(trip = trip)
    }
    
    /**
     * Cancel a reservation
     */
    fun cancelReservation(reservationId: UUID): Boolean {
        val user = currentUser ?: return false
        
        // Get reservation first
        val reservation = getReservation(reservationId) ?: return false
        
        // Check if reservation belongs to current user
        if (reservation.userId != user.id) {
            return false
        }
        
        // Delete reservation from database
        val deleted = database?.delete(
            SeferDbSchema.ReservationTable.NAME,
            "${SeferDbSchema.ReservationTable.Cols.ID} = ?",
            arrayOf(reservationId.toString())
        ) ?: 0
        
        if (deleted > 0) {
            // Free up the seats in database
            reservation.seatNumbers.forEach { seatNumber ->
                updateSeatStatus(reservation.tripId, seatNumber, SeatStatus.AVAILABLE, null)
            }
            
            // Also update in memory if trip is loaded
            val trip = getTrip(reservation.tripId)
            trip?.let {
                reservation.seatNumbers.forEach { seatNumber ->
                    val seat = it.seats.find { s -> s.number == seatNumber }
                    seat?.status = SeatStatus.AVAILABLE
                    seat?.gender = null
                }
            }
            
            return true
        }
        
        return false
    }
}
