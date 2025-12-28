# SoraReservation Database Schema

## Overview
SQLite veritabanı şeması. Tüm tablolar ve kolonlar burada tanımlanmıştır.

## Tables

### 1. users
Kullanıcı bilgilerini saklar.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID string formatında kullanıcı ID |
| email | TEXT | UNIQUE, NOT NULL | Kullanıcı email adresi |
| password | TEXT | NOT NULL | Kullanıcı şifresi (plain text - production'da hash'lenmeli) |
| full_name | TEXT | NOT NULL | Kullanıcının tam adı |
| phone | TEXT | | Telefon numarası |
| is_admin | INTEGER | DEFAULT 0 | Admin kullanıcı mı? (0: false, 1: true) |

**Index:**
- `email` üzerinde UNIQUE index

---

### 2. trips
Sefer bilgilerini saklar.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID string formatında sefer ID |
| departure_city | TEXT | NOT NULL | Kalkış şehri |
| arrival_city | TEXT | NOT NULL | Varış şehri |
| departure_date | INTEGER | NOT NULL | Kalkış tarihi (Unix timestamp - milliseconds) |
| departure_time | TEXT | NOT NULL | Kalkış saati (HH:mm formatında) |
| price | REAL | NOT NULL | Sefer fiyatı (TL) |
| total_seats | INTEGER | NOT NULL, DEFAULT 45 | Toplam koltuk sayısı |

**Index:**
- `departure_city` üzerinde index (arama performansı için)
- `arrival_city` üzerinde index (arama performansı için)
- `departure_date` üzerinde index (tarih sıralaması için)

---

### 3. seats
Koltuk bilgilerini saklar. Her koltuk bir sefere aittir.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID string formatında koltuk ID |
| trip_id | TEXT | NOT NULL, FOREIGN KEY | Hangi sefere ait (trips.id referansı) |
| seat_number | INTEGER | NOT NULL | Koltuk numarası (1-45 arası) |
| status | INTEGER | NOT NULL, DEFAULT 0 | Koltuk durumu: 0=AVAILABLE, 1=OCCUPIED, 2=SELECTED |

**Index:**
- `trip_id` üzerinde index (sefer bazlı sorgular için)
- `(trip_id, seat_number)` üzerinde UNIQUE index (bir seferde aynı koltuk numarası tekrar edemez)

**Foreign Key:**
- `trip_id` → `trips.id` (ON DELETE CASCADE)

---

### 4. reservations
Rezervasyon bilgilerini saklar.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | TEXT | PRIMARY KEY | UUID string formatında rezervasyon ID |
| user_id | TEXT | NOT NULL, FOREIGN KEY | Hangi kullanıcıya ait (users.id referansı) |
| trip_id | TEXT | NOT NULL, FOREIGN KEY | Hangi sefere ait (trips.id referansı) |
| seat_numbers | TEXT | NOT NULL | Rezerve edilen koltuk numaraları (virgülle ayrılmış: "10,11,12") |
| total_price | REAL | NOT NULL | Toplam fiyat (TL) |
| reservation_date | INTEGER | NOT NULL | Rezervasyon tarihi (Unix timestamp - milliseconds) |

**Index:**
- `user_id` üzerinde index (kullanıcı bazlı sorgular için)
- `trip_id` üzerinde index (sefer bazlı sorgular için)
- `reservation_date` üzerinde index (tarih sıralaması için)

**Foreign Key:**
- `user_id` → `users.id` (ON DELETE CASCADE)
- `trip_id` → `trips.id` (ON DELETE CASCADE)

---

## Relationships

```
users (1) ────< (many) reservations
trips (1) ────< (many) seats
trips (1) ────< (many) reservations
```

- Bir kullanıcı birden fazla rezervasyon yapabilir
- Bir sefer birden fazla koltuk içerir
- Bir sefer birden fazla rezervasyon içerebilir

---

## SQL CREATE Statements

```sql
-- Users Table
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    full_name TEXT NOT NULL,
    phone TEXT,
    is_admin INTEGER DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);

-- Trips Table
CREATE TABLE trips (
    id TEXT PRIMARY KEY,
    departure_city TEXT NOT NULL,
    arrival_city TEXT NOT NULL,
    departure_date INTEGER NOT NULL,
    departure_time TEXT NOT NULL,
    price REAL NOT NULL,
    total_seats INTEGER NOT NULL DEFAULT 45
);

CREATE INDEX idx_trips_departure ON trips(departure_city);
CREATE INDEX idx_trips_arrival ON trips(arrival_city);
CREATE INDEX idx_trips_date ON trips(departure_date);

-- Seats Table
CREATE TABLE seats (
    id TEXT PRIMARY KEY,
    trip_id TEXT NOT NULL,
    seat_number INTEGER NOT NULL,
    status INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    UNIQUE(trip_id, seat_number)
);

CREATE INDEX idx_seats_trip ON seats(trip_id);

-- Reservations Table
CREATE TABLE reservations (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    trip_id TEXT NOT NULL,
    seat_numbers TEXT NOT NULL,
    total_price REAL NOT NULL,
    reservation_date INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_trip ON reservations(trip_id);
CREATE INDEX idx_reservations_date ON reservations(reservation_date);
```

---

## Data Types

- **TEXT**: String değerler (UUID, email, isimler, vb.)
- **INTEGER**: Sayısal değerler (boolean, timestamp, koltuk numarası, durum kodları)
- **REAL**: Ondalıklı sayılar (fiyat)

---

## Status Codes

### Seat Status
- `0` = AVAILABLE (Müsait)
- `1` = OCCUPIED (Dolu/Rezerve)
- `2` = SELECTED (Seçili - geçici durum)

---

## Notes

1. **UUID Format**: Tüm ID'ler UUID string formatında saklanır (örn: "550e8400-e29b-41d4-a716-446655440000")
2. **Timestamps**: Tarihler Unix timestamp (milliseconds) olarak saklanır
3. **Seat Numbers**: Virgülle ayrılmış string olarak saklanır (örn: "10,11,12")
4. **Cascade Delete**: Bir sefer silindiğinde, o sefere ait tüm koltuklar ve rezervasyonlar otomatik silinir
5. **Password Security**: Şu an plain text saklanıyor, production'da hash'lenmeli (SHA-256 veya bcrypt)

