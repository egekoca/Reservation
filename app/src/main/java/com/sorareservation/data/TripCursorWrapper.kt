package com.sorareservation.data

import android.database.Cursor
import android.database.CursorWrapper
import com.sorareservation.model.Trip
import java.util.Date
import java.util.UUID

/**
 * CursorWrapper for reading Trip objects from database
 * Note: Seats are loaded separately via SeatCursorWrapper
 */
class TripCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    
    /**
     * Get Trip object from current cursor position
     * Seats list should be loaded separately and passed to this method
     */
    fun getTrip(seats: MutableList<com.sorareservation.model.Seat> = mutableListOf()): Trip {
        val idString = getString(getColumnIndex(SeferDbSchema.TripTable.Cols.ID))
        val departureCity = getString(getColumnIndex(SeferDbSchema.TripTable.Cols.DEPARTURE_CITY))
        val arrivalCity = getString(getColumnIndex(SeferDbSchema.TripTable.Cols.ARRIVAL_CITY))
        val departureDateLong = getLong(getColumnIndex(SeferDbSchema.TripTable.Cols.DEPARTURE_DATE))
        val departureTime = getString(getColumnIndex(SeferDbSchema.TripTable.Cols.DEPARTURE_TIME))
        val price = getDouble(getColumnIndex(SeferDbSchema.TripTable.Cols.PRICE))
        val totalSeats = getInt(getColumnIndex(SeferDbSchema.TripTable.Cols.TOTAL_SEATS))
        
        return Trip(
            id = UUID.fromString(idString),
            departureCity = departureCity,
            arrivalCity = arrivalCity,
            departureDate = Date(departureDateLong),
            departureTime = departureTime,
            price = price,
            totalSeats = totalSeats,
            seats = seats
        )
    }
}

