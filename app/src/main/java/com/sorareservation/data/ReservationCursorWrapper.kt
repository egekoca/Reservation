package com.sorareservation.data

import android.database.Cursor
import android.database.CursorWrapper
import com.sorareservation.model.Reservation
import com.sorareservation.model.Trip
import java.util.Date
import java.util.UUID

/**
 * CursorWrapper for reading Reservation objects from database
 */
class ReservationCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    
    /**
     * Get Reservation object from current cursor position
     * Trip object should be loaded separately and passed to this method
     */
    fun getReservation(trip: Trip? = null): Reservation {
        val idString = getString(getColumnIndex(SeferDbSchema.ReservationTable.Cols.ID))
        val userIdString = getString(getColumnIndex(SeferDbSchema.ReservationTable.Cols.USER_ID))
        val tripIdString = getString(getColumnIndex(SeferDbSchema.ReservationTable.Cols.TRIP_ID))
        val seatNumbersString = getString(getColumnIndex(SeferDbSchema.ReservationTable.Cols.SEAT_NUMBERS))
        val totalPrice = getDouble(getColumnIndex(SeferDbSchema.ReservationTable.Cols.TOTAL_PRICE))
        val reservationDateLong = getLong(getColumnIndex(SeferDbSchema.ReservationTable.Cols.RESERVATION_DATE))
        
        // Parse seat numbers from comma-separated string
        val seatNumbers = seatNumbersString.split(",")
            .map { it.trim().toInt() }
            .toList()
        
        return Reservation(
            id = UUID.fromString(idString),
            userId = UUID.fromString(userIdString),
            tripId = UUID.fromString(tripIdString),
            seatNumbers = seatNumbers,
            totalPrice = totalPrice,
            reservationDate = Date(reservationDateLong),
            trip = trip
        )
    }
}

