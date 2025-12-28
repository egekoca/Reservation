# SQLite Veritabanını Görüntüleme Rehberi

## Veritabanı Konumu

SQLite veritabanı dosyası Android cihazında/emülatörde şu konumda saklanır:
```
/data/data/com.sorareservation/databases/sorareservation.db
```

## Yöntem 1: Android Studio Database Inspector (ÖNERİLEN)

### Adımlar:
1. Android Studio'da uygulamayı emülatörde çalıştırın
2. Android Studio'da **View > Tool Windows > App Inspection** menüsünü açın
3. Sol panelde **Database Inspector** sekmesini seçin
4. Açılan listede `sorareservation.db` dosyasını bulun
5. Veritabanını genişletip tabloları görebilirsiniz:
   - `users` - Kullanıcılar
   - `trips` - Seferler
   - `seats` - Koltuklar
   - `reservations` - Rezervasyonlar
6. Her tabloya tıklayarak verileri görebilir, sorgu çalıştırabilirsiniz

### Özellikler:
- Gerçek zamanlı veri görüntüleme
- SQL sorguları çalıştırma
- Veri düzenleme (debug için)
- Tablo yapısını görüntüleme

## Yöntem 2: ADB Shell ile

### Adımlar:
1. Terminal'de emülatörün bağlı olduğundan emin olun:
   ```bash
   adb devices
   ```

2. Emülatöre bağlanın:
   ```bash
   adb shell
   ```

3. Veritabanı dosyasına erişin:
   ```bash
   cd /data/data/com.sorareservation/databases
   ls -la
   ```

4. SQLite shell'i başlatın:
   ```bash
   sqlite3 sorareservation.db
   ```

5. SQL sorguları çalıştırın:
   ```sql
   .tables                    -- Tüm tabloları listele
   SELECT * FROM users;       -- Kullanıcıları göster
   SELECT * FROM trips;       -- Seferleri göster
   SELECT * FROM seats;       -- Koltukları göster
   SELECT * FROM reservations; -- Rezervasyonları göster
   .schema                    -- Tablo yapılarını göster
   .quit                      -- Çıkış
   ```

## Yöntem 3: Veritabanı Dosyasını Bilgisayara İndirme

### Adımlar:
1. Veritabanı dosyasını bilgisayara indirin:
   ```bash
   adb pull /data/data/com.sorareservation/databases/sorareservation.db ~/Desktop/
   ```

2. SQLite Browser (DB Browser for SQLite) ile açın:
   - İndir: https://sqlitebrowser.org/
   - Dosyayı açın ve tabloları görüntüleyin

## Yöntem 4: Uygulama İçinde Debug Ekranı (Geliştirme için)

Admin panelinde bir "View Database" butonu ekleyebiliriz. Bu özellik sadece debug build'lerde aktif olabilir.

## Veritabanı Tabloları

### 1. users
- id (TEXT, PRIMARY KEY)
- email (TEXT, UNIQUE)
- password (TEXT)
- full_name (TEXT)
- phone (TEXT)
- is_admin (INTEGER, 0 veya 1)

### 2. trips
- id (TEXT, PRIMARY KEY)
- departure_city (TEXT)
- arrival_city (TEXT)
- departure_date (INTEGER, Unix timestamp)
- departure_time (TEXT, "HH:mm")
- price (REAL)
- total_seats (INTEGER)

### 3. seats
- id (TEXT, PRIMARY KEY)
- trip_id (TEXT, FOREIGN KEY)
- seat_number (INTEGER)
- status (INTEGER, 0=AVAILABLE, 1=OCCUPIED, 2=SELECTED)
- gender (INTEGER, NULL veya 0=MALE, 1=FEMALE)

### 4. reservations
- id (TEXT, PRIMARY KEY)
- user_id (TEXT, FOREIGN KEY)
- trip_id (TEXT, FOREIGN KEY)
- seat_numbers (TEXT, "10,11,12" formatında)
- total_price (REAL)
- reservation_date (INTEGER, Unix timestamp)

## Örnek SQL Sorguları

```sql
-- Tüm kullanıcıları göster
SELECT * FROM users;

-- Admin kullanıcıları göster
SELECT * FROM users WHERE is_admin = 1;

-- Tüm seferleri göster
SELECT * FROM trips ORDER BY departure_date;

-- Belirli bir seferin koltuklarını göster
SELECT * FROM seats WHERE trip_id = 'TRIP_UUID' ORDER BY seat_number;

-- Dolu koltukları göster
SELECT * FROM seats WHERE status = 1;

-- Bir kullanıcının rezervasyonlarını göster
SELECT * FROM reservations WHERE user_id = 'USER_UUID';

-- Rezervasyonları sefer bilgisiyle birlikte göster
SELECT r.*, t.departure_city, t.arrival_city 
FROM reservations r 
JOIN trips t ON r.trip_id = t.id;
```

## Notlar

- SQLite bir **sunucu değil**, yerel dosya tabanlı veritabanıdır
- Veritabanı dosyası uygulama silinene kadar cihazda kalır
- Uygulama verilerini temizlerseniz (`Settings > Apps > SoraReservation > Clear Data`), veritabanı da silinir
- Production'da veritabanı şifreleme (SQLCipher) kullanılmalıdır

