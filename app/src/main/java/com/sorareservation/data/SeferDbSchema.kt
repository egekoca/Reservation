package com.sorareservation.data

/**
 * Database schema for SoraReservation app
 * Defines all table names and column names
 */
object SeferDbSchema {
    
    /**
     * Users table schema
     */
    object UserTable {
        const val NAME = "users"
        
        object Cols {
            const val ID = "id"
            const val EMAIL = "email"
            const val PASSWORD = "password"
            const val FULL_NAME = "full_name"
            const val PHONE = "phone"
            const val IS_ADMIN = "is_admin"
        }
    }
    
    /**
     * Trips table schema
     */
    object TripTable {
        const val NAME = "trips"
        
        object Cols {
            const val ID = "id"
            const val DEPARTURE_CITY = "departure_city"
            const val ARRIVAL_CITY = "arrival_city"
            const val DEPARTURE_DATE = "departure_date"
            const val DEPARTURE_TIME = "departure_time"
            const val PRICE = "price"
            const val TOTAL_SEATS = "total_seats"
        }
    }
    
    /**
     * Seats table schema
     * Each seat belongs to a trip
     */
    object SeatTable {
        const val NAME = "seats"
        
        object Cols {
            const val ID = "id"
            const val TRIP_ID = "trip_id"
            const val SEAT_NUMBER = "seat_number"
            const val STATUS = "status" // 0: AVAILABLE, 1: OCCUPIED, 2: SELECTED
            const val GENDER = "gender" // NULL: available, 0: MALE, 1: FEMALE
        }
    }
    
    /**
     * Reservations table schema
     */
    object ReservationTable {
        const val NAME = "reservations"
        
        object Cols {
            const val ID = "id"
            const val USER_ID = "user_id"
            const val TRIP_ID = "trip_id"
            const val SEAT_NUMBERS = "seat_numbers" // Comma-separated seat numbers: "10,11,12"
            const val TOTAL_PRICE = "total_price"
            const val RESERVATION_DATE = "reservation_date"
        }
    }
}

