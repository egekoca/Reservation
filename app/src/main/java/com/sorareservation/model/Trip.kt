package com.sorareservation.model

import java.util.Date
import java.util.UUID

/**
 * Otobüs seferi modeli
 * 
 * Bu data class, bir otobüs seferinin tüm bilgilerini tutar:
 * - Sefer bilgileri (kalkış, varış, tarih, saat, fiyat)
 * - Koltuk listesi (her koltuk Seat objesi olarak)
 * - Koltuk yönetimi metodları (seçim, iptal, rezervasyon)
 */
data class Trip(
    val id: UUID = UUID.randomUUID(), // Benzersiz sefer ID'si
    val departureCity: String, // Kalkış şehri
    val arrivalCity: String, // Varış şehri
    val departureDate: Date, // Kalkış tarihi
    val departureTime: String, // Kalkış saati (Format: "HH:mm")
    val price: Double, // Sefer fiyatı (TL)
    val totalSeats: Int = 45, // Toplam koltuk sayısı (varsayılan: 45)
    val seats: MutableList<Seat> = mutableListOf() // Koltuk listesi
) {
    /**
     * Constructor sonrası çalışan init bloğu
     * 
     * Eğer koltuk listesi boşsa, totalSeats kadar koltuk oluşturur.
     * Her koltuk başlangıçta AVAILABLE (müsait) durumundadır.
     */
    init {
        // Eğer koltuk listesi boşsa, koltukları oluştur
        if (seats.isEmpty()) {
            // 1'den totalSeats'e kadar koltuk numaraları oluştur
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
     * Koltuk seçilebilir mi kontrol eder (cinsiyet bazlı)
     * 
     * Kural: Erkekler sadece erkeklerin yanına, kadınlar sadece kadınların yanına oturabilir.
     * 
     * Bu metod, hem OCCUPIED (dolu) hem de SELECTED (seçili) koltukları kontrol eder.
     * Böylece seçim sırasında bile cinsiyet çakışması önlenir.
     * 
     * @param seatNumber Seçilmek istenen koltuk numarası
     * @param gender Seçilecek kişinin cinsiyeti
     * @return true ise seçilebilir, false ise komşu koltukta karşı cinsiyet var
     */
    fun canSelectSeatWithGender(seatNumber: Int, gender: Gender): Boolean {
        // Komşu koltuk numaralarını al (2+1 düzenine göre)
        val adjacentSeats = getAdjacentSeats(seatNumber)
        
        // Her komşu koltuk için kontrol yap
        for (adjSeatNum in adjacentSeats) {
            val adjSeat = seats.find { it.number == adjSeatNum }
            if (adjSeat != null) {
                // Hem dolu (OCCUPIED) hem seçili (SELECTED) koltukları kontrol et
                // SELECTED kontrolü önemli: Kullanıcı seçim yaparken bile çakışma olmamalı
                if (adjSeat.isOccupied() || adjSeat.isSelected()) {
                    val adjGender = adjSeat.gender
                    // Eğer komşu koltukta karşı cinsiyet varsa, seçim yapılamaz
                    if (adjGender != null && adjGender != gender) {
                        // Komşu koltukta karşı cinsiyet var, seçim yapılamaz
                        return false
                    }
                }
            }
        }
        // Tüm komşu koltuklar uygun, seçim yapılabilir
        return true
    }
    
    /**
     * Get adjacent seat numbers for 2+1 bus layout
     * Layout: Each row has 3 seats - 2 on left, 1 on right (separated by aisle)
     * Example rows: (1,2 | aisle | 3), (4,5 | aisle | 6), (7,8 | aisle | 9), etc.
     * 
     * Adjacency rules:
     * - Seats in left pair (1,2) are adjacent to each other
     * - Right side seat (3) has no adjacent seats (aisle separates it)
     */
    private fun getAdjacentSeats(seatNumber: Int): List<Int> {
        val adjacent = mutableListOf<Int>()
        
        // Calculate position in row (0, 1, or 2)
        // 0 = first seat in left pair
        // 1 = second seat in left pair  
        // 2 = right side single seat
        val positionInRow = (seatNumber - 1) % 3
        
        when (positionInRow) {
            0 -> {
                // First seat in left pair (e.g., 1, 4, 7) - adjacent to second seat
                if (seatNumber < totalSeats) {
                    adjacent.add(seatNumber + 1)
                }
            }
            1 -> {
                // Second seat in left pair (e.g., 2, 5, 8) - adjacent to first seat
                if (seatNumber > 1) {
                    adjacent.add(seatNumber - 1)
                }
            }
            2 -> {
                // Right side single seat (e.g., 3, 6, 9) - no adjacent seats (aisle separates)
                // No adjacent seats in 2+1 layout
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
                seat.gender = null // Clear gender when deselected
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

