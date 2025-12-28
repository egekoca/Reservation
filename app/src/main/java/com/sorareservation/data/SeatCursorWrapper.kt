package com.sorareservation.data

import android.database.Cursor
import android.database.CursorWrapper
import com.sorareservation.model.Gender
import com.sorareservation.model.Seat
import com.sorareservation.model.SeatStatus

/**
 * CursorWrapper for reading Seat objects from database
 */
class SeatCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    
    /**
     * Get Seat object from current cursor position
     */
    fun getSeat(): Seat {
        val seatNumber = getInt(getColumnIndex(SeferDbSchema.SeatTable.Cols.SEAT_NUMBER))
        val statusInt = getInt(getColumnIndex(SeferDbSchema.SeatTable.Cols.STATUS))
        val genderInt = if (isNull(getColumnIndex(SeferDbSchema.SeatTable.Cols.GENDER))) {
            null
        } else {
            getInt(getColumnIndex(SeferDbSchema.SeatTable.Cols.GENDER))
        }
        
        // Convert status integer to enum
        val status = when (statusInt) {
            0 -> SeatStatus.AVAILABLE
            1 -> SeatStatus.OCCUPIED
            2 -> SeatStatus.SELECTED
            else -> SeatStatus.AVAILABLE
        }
        
        // Convert gender integer to enum (null = available, 0 = MALE, 1 = FEMALE)
        val gender = when (genderInt) {
            null -> null
            0 -> Gender.MALE
            1 -> Gender.FEMALE
            else -> null
        }
        
        return Seat(
            number = seatNumber,
            status = status,
            gender = gender
        )
    }
}

