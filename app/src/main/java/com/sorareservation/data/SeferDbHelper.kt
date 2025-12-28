package com.sorareservation.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper for SoraReservation database
 * Manages database creation and version management
 */
class SeferDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    VERSION
) {
    
    companion object {
        private const val DATABASE_NAME = "sorareservation.db"
        private const val VERSION = 1
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys = ON")
        
        // Create users table
        db.execSQL("""
            CREATE TABLE ${SeferDbSchema.UserTable.NAME} (
                ${SeferDbSchema.UserTable.Cols.ID} TEXT PRIMARY KEY,
                ${SeferDbSchema.UserTable.Cols.EMAIL} TEXT UNIQUE NOT NULL,
                ${SeferDbSchema.UserTable.Cols.PASSWORD} TEXT NOT NULL,
                ${SeferDbSchema.UserTable.Cols.FULL_NAME} TEXT NOT NULL,
                ${SeferDbSchema.UserTable.Cols.PHONE} TEXT,
                ${SeferDbSchema.UserTable.Cols.IS_ADMIN} INTEGER DEFAULT 0
            )
        """.trimIndent())
        
        // Create index on email for faster lookups
        db.execSQL("""
            CREATE INDEX idx_users_email ON ${SeferDbSchema.UserTable.NAME}(${SeferDbSchema.UserTable.Cols.EMAIL})
        """.trimIndent())
        
        // Create trips table
        db.execSQL("""
            CREATE TABLE ${SeferDbSchema.TripTable.NAME} (
                ${SeferDbSchema.TripTable.Cols.ID} TEXT PRIMARY KEY,
                ${SeferDbSchema.TripTable.Cols.DEPARTURE_CITY} TEXT NOT NULL,
                ${SeferDbSchema.TripTable.Cols.ARRIVAL_CITY} TEXT NOT NULL,
                ${SeferDbSchema.TripTable.Cols.DEPARTURE_DATE} INTEGER NOT NULL,
                ${SeferDbSchema.TripTable.Cols.DEPARTURE_TIME} TEXT NOT NULL,
                ${SeferDbSchema.TripTable.Cols.PRICE} REAL NOT NULL,
                ${SeferDbSchema.TripTable.Cols.TOTAL_SEATS} INTEGER NOT NULL DEFAULT 45
            )
        """.trimIndent())
        
        // Create indexes on trips table for faster searches
        db.execSQL("""
            CREATE INDEX idx_trips_departure ON ${SeferDbSchema.TripTable.NAME}(${SeferDbSchema.TripTable.Cols.DEPARTURE_CITY})
        """.trimIndent())
        db.execSQL("""
            CREATE INDEX idx_trips_arrival ON ${SeferDbSchema.TripTable.NAME}(${SeferDbSchema.TripTable.Cols.ARRIVAL_CITY})
        """.trimIndent())
        db.execSQL("""
            CREATE INDEX idx_trips_date ON ${SeferDbSchema.TripTable.NAME}(${SeferDbSchema.TripTable.Cols.DEPARTURE_DATE})
        """.trimIndent())
        
        // Create seats table
        db.execSQL("""
            CREATE TABLE ${SeferDbSchema.SeatTable.NAME} (
                ${SeferDbSchema.SeatTable.Cols.ID} TEXT PRIMARY KEY,
                ${SeferDbSchema.SeatTable.Cols.TRIP_ID} TEXT NOT NULL,
                ${SeferDbSchema.SeatTable.Cols.SEAT_NUMBER} INTEGER NOT NULL,
                ${SeferDbSchema.SeatTable.Cols.STATUS} INTEGER NOT NULL DEFAULT 0,
                ${SeferDbSchema.SeatTable.Cols.GENDER} INTEGER,
                FOREIGN KEY (${SeferDbSchema.SeatTable.Cols.TRIP_ID}) REFERENCES ${SeferDbSchema.TripTable.NAME}(${SeferDbSchema.TripTable.Cols.ID}) ON DELETE CASCADE,
                UNIQUE(${SeferDbSchema.SeatTable.Cols.TRIP_ID}, ${SeferDbSchema.SeatTable.Cols.SEAT_NUMBER})
            )
        """.trimIndent())
        
        // Create index on trip_id for faster seat lookups
        db.execSQL("""
            CREATE INDEX idx_seats_trip ON ${SeferDbSchema.SeatTable.NAME}(${SeferDbSchema.SeatTable.Cols.TRIP_ID})
        """.trimIndent())
        
        // Create reservations table
        db.execSQL("""
            CREATE TABLE ${SeferDbSchema.ReservationTable.NAME} (
                ${SeferDbSchema.ReservationTable.Cols.ID} TEXT PRIMARY KEY,
                ${SeferDbSchema.ReservationTable.Cols.USER_ID} TEXT NOT NULL,
                ${SeferDbSchema.ReservationTable.Cols.TRIP_ID} TEXT NOT NULL,
                ${SeferDbSchema.ReservationTable.Cols.SEAT_NUMBERS} TEXT NOT NULL,
                ${SeferDbSchema.ReservationTable.Cols.TOTAL_PRICE} REAL NOT NULL,
                ${SeferDbSchema.ReservationTable.Cols.RESERVATION_DATE} INTEGER NOT NULL,
                FOREIGN KEY (${SeferDbSchema.ReservationTable.Cols.USER_ID}) REFERENCES ${SeferDbSchema.UserTable.NAME}(${SeferDbSchema.UserTable.Cols.ID}) ON DELETE CASCADE,
                FOREIGN KEY (${SeferDbSchema.ReservationTable.Cols.TRIP_ID}) REFERENCES ${SeferDbSchema.TripTable.NAME}(${SeferDbSchema.TripTable.Cols.ID}) ON DELETE CASCADE
            )
        """.trimIndent())
        
        // Create indexes on reservations table
        db.execSQL("""
            CREATE INDEX idx_reservations_user ON ${SeferDbSchema.ReservationTable.NAME}(${SeferDbSchema.ReservationTable.Cols.USER_ID})
        """.trimIndent())
        db.execSQL("""
            CREATE INDEX idx_reservations_trip ON ${SeferDbSchema.ReservationTable.NAME}(${SeferDbSchema.ReservationTable.Cols.TRIP_ID})
        """.trimIndent())
        db.execSQL("""
            CREATE INDEX idx_reservations_date ON ${SeferDbSchema.ReservationTable.NAME}(${SeferDbSchema.ReservationTable.Cols.RESERVATION_DATE})
        """.trimIndent())
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle future database migrations here
        // For now, we're at version 1, so no migration needed
    }
}

