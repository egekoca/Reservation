# SoraReservation - Otobüs Bileti Rezervasyon Sistemi

Kamilkoç/Obilet tarzı otobüs bileti rezervasyon sistemi mobil uygulaması. Kotlin ile geliştirilmiş, SQLite veritabanı kullanan modern bir Android uygulaması.

## 📱 Uygulama Hakkında

SoraReservation, kullanıcıların otobüs seferlerini görüntüleyip, koltuk seçerek rezervasyon yapmalarını sağlayan bir mobil uygulamadır. Admin kullanıcılar sefer ekleyebilir ve silebilir. Uygulama, cinsiyet bazlı koltuk seçimi özelliği ile erkek ve kadın yolcuların yan yana oturmamasını garanti eder.

## ✨ Özellikler

### Kullanıcı Özellikleri
- **Kullanıcı Kayıt/Giriş Sistemi**: Email ve şifre ile kayıt olma ve giriş yapma
- **Sefer Listeleme ve Arama**: Kalkış ve varış şehirlerine göre sefer arama (81 Türk şehri)
- **Görsel Koltuk Seçimi**: 2+1 otobüs düzeninde interaktif koltuk seçimi
- **Cinsiyet Bazlı Koltuk Seçimi**: Erkek ve kadın yolcuların yan yana oturmaması garantisi
- **Rezervasyon Yönetimi**: Rezervasyon oluşturma, görüntüleme ve iptal etme
- **Rezervasyon Paylaşımı**: Rezervasyon detaylarını diğer uygulamalarla paylaşma

### Admin Özellikleri
- **Sefer Yönetimi**: Yeni sefer ekleme ve mevcut seferleri silme
- **Sefer Listesi Görüntüleme**: Tüm seferleri görüntüleme ve yönetme

## 🏗️ Mimari ve Teknik Detaylar

### Teknoloji Stack
- **Programlama Dili**: Kotlin
- **UI Framework**: XML Layout + ViewBinding
- **Veritabanı**: SQLite (SQLiteOpenHelper, CursorWrapper)
- **Mimari Pattern**: Single Fragment Activity Pattern
- **Veri Yönetimi**: Singleton Pattern (SeferLab)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34

### Mimari Yapı

#### Single Fragment Activity Pattern
- Tüm ekranlar Fragment olarak tasarlanmıştır
- Activity'ler sadece container görevi görmektedir
- `SingleFragmentActivity` abstract sınıfı tüm Activity'lerin temelidir
- İş mantığı (Business Logic) Activity'lerde değil, Fragment'lerde bulunur

#### Singleton Pattern (SeferLab)
- Tüm veri işlemleri `SeferLab` singleton sınıfı üzerinden yapılır
- Veritabanı işlemleri tek bir noktadan yönetilir
- CRUD (Create, Read, Update, Delete) işlemleri merkezi olarak yönetilir

#### Intent Encapsulation
- Her Activity'de `companion object` içinde `newIntent()` metodu bulunur
- Intent key'leri hardcoded string olarak değil, companion object içinde tanımlanır

#### State Management
- Ekran döndürme desteği: `onSaveInstanceState` override edilmiştir
- Fragment lifecycle'a uygun state yönetimi

## 🗄️ Veritabanı Yapısı

Uygulama SQLite veritabanı kullanmaktadır. Veritabanı dosyası Android cihazında şu konumda saklanır:
```
/data/data/com.sorareservation/databases/sorareservation.db
```

### Tablolar

#### 1. users (Kullanıcılar)
Kullanıcı bilgilerini saklar.

| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | TEXT (PRIMARY KEY) | UUID formatında kullanıcı ID |
| email | TEXT (UNIQUE, NOT NULL) | Kullanıcı email adresi |
| password | TEXT (NOT NULL) | Kullanıcı şifresi |
| full_name | TEXT (NOT NULL) | Kullanıcının tam adı |
| phone | TEXT | Telefon numarası |
| is_admin | INTEGER (DEFAULT 0) | Admin kullanıcı mı? (0: false, 1: true) |

**Index**: `email` üzerinde UNIQUE index

#### 2. trips (Seferler)
Sefer bilgilerini saklar.

| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | TEXT (PRIMARY KEY) | UUID formatında sefer ID |
| departure_city | TEXT (NOT NULL) | Kalkış şehri |
| arrival_city | TEXT (NOT NULL) | Varış şehri |
| departure_date | INTEGER (NOT NULL) | Kalkış tarihi (Unix timestamp - milliseconds) |
| departure_time | TEXT (NOT NULL) | Kalkış saati (HH:mm formatında) |
| price | REAL (NOT NULL) | Sefer fiyatı (TL) |
| total_seats | INTEGER (NOT NULL, DEFAULT 45) | Toplam koltuk sayısı |

**Index'ler**: 
- `departure_city` üzerinde index
- `arrival_city` üzerinde index
- `departure_date` üzerinde index

#### 3. seats (Koltuklar)
Koltuk bilgilerini saklar. Her koltuk bir sefere aittir.

| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | TEXT (PRIMARY KEY) | UUID formatında koltuk ID |
| trip_id | TEXT (NOT NULL, FOREIGN KEY) | Hangi sefere ait (trips.id referansı) |
| seat_number | INTEGER (NOT NULL) | Koltuk numarası (1-45 arası) |
| status | INTEGER (NOT NULL, DEFAULT 0) | Koltuk durumu: 0=AVAILABLE, 1=OCCUPIED, 2=SELECTED |
| gender | INTEGER | Cinsiyet: NULL=available, 0=MALE, 1=FEMALE |

**Index'ler**:
- `trip_id` üzerinde index
- `(trip_id, seat_number)` üzerinde UNIQUE index

**Foreign Key**: `trip_id` → `trips.id` (ON DELETE CASCADE)

#### 4. reservations (Rezervasyonlar)
Rezervasyon bilgilerini saklar.

| Kolon | Tip | Açıklama |
|-------|-----|----------|
| id | TEXT (PRIMARY KEY) | UUID formatında rezervasyon ID |
| user_id | TEXT (NOT NULL, FOREIGN KEY) | Hangi kullanıcıya ait (users.id referansı) |
| trip_id | TEXT (NOT NULL, FOREIGN KEY) | Hangi sefere ait (trips.id referansı) |
| seat_numbers | TEXT (NOT NULL) | Rezerve edilen koltuk numaraları (virgülle ayrılmış: "10,11,12") |
| total_price | REAL (NOT NULL) | Toplam fiyat (TL) |
| reservation_date | INTEGER (NOT NULL) | Rezervasyon tarihi (Unix timestamp - milliseconds) |

**Index'ler**:
- `user_id` üzerinde index
- `trip_id` üzerinde index
- `reservation_date` üzerinde index

**Foreign Keys**:
- `user_id` → `users.id` (ON DELETE CASCADE)
- `trip_id` → `trips.id` (ON DELETE CASCADE)

### İlişkiler

```
users (1) ────< (many) reservations
trips (1) ────< (many) seats
trips (1) ────< (many) reservations
```

- Bir kullanıcı birden fazla rezervasyon yapabilir
- Bir sefer birden fazla koltuk içerir (varsayılan 45 koltuk)
- Bir sefer birden fazla rezervasyon içerebilir

## 🔄 Uygulama Çalışma Mantığı

### 1. Uygulama Başlatma
1. `SoraReservationApplication.onCreate()` çağrılır
2. `SeferLab.init(context)` ile veritabanı bağlantısı kurulur
3. Veritabanı boşsa (ilk açılış), örnek başlangıç verileri (seed data) otomatik olarak veritabanına yüklenir
4. Bu veriler veritabanında **kalıcı olarak saklanır** ve uygulama her açıldığında aynı veriler gelir

### 2. Kullanıcı Kayıt/Giriş
- **Kayıt**: Yeni kullanıcı bilgileri `users` tablosuna INSERT edilir
- **Giriş**: Email ve şifre `users` tablosundan SELECT ile kontrol edilir
- Giriş başarılı olursa, kullanıcı bilgisi memory'de (`currentUser`) saklanır

### 3. Sefer Listeleme
- Tüm seferler `trips` tablosundan SELECT ile çekilir
- Her sefer için koltuklar `seats` tablosundan JOIN ile yüklenir
- Müsait koltuk sayısı hesaplanır (`getAvailableSeatsCount()`)
- Şehir bazlı filtreleme yapılabilir

### 4. Koltuk Seçimi
- Kullanıcı önce cinsiyet seçer (Erkek/Kadın)
- Koltuk tıklandığında:
  - Koltuk AVAILABLE ise ve cinsiyet uygunsa → SELECTED yapılır
  - Yan yana koltuklarda cinsiyet çakışması varsa → Uyarı gösterilir
  - SELECTED durumu sadece memory'de tutulur (veritabanında değil)

### 5. Rezervasyon Oluşturma
1. Kullanıcı koltukları seçer ve "Book Now" butonuna tıklar
2. Confirmation ekranına yönlendirilir
3. "Confirm and Continue" butonuna tıklanınca:
   - `reservations` tablosuna INSERT yapılır
   - Seçili koltukların `status` değeri OCCUPIED olarak UPDATE edilir
   - Koltukların `gender` bilgisi kaydedilir
   - Müsait koltuk sayısı otomatik olarak azalır

### 6. Rezervasyon İptal
1. Kullanıcı "My Reservations" ekranından rezervasyonu iptal eder
2. `reservations` tablosundan DELETE yapılır
3. İptal edilen koltukların `status` değeri AVAILABLE olarak UPDATE edilir
4. `gender` bilgisi NULL yapılır
5. Müsait koltuk sayısı otomatik olarak artar

### 7. Admin Sefer Ekleme
1. Admin kullanıcı "Admin Panel" ekranına gider
2. Sefer bilgilerini doldurur ve "Add Trip" butonuna tıklar
3. `trips` tablosuna INSERT yapılır
4. Otomatik olarak 45 koltuk `seats` tablosuna INSERT edilir (tümü AVAILABLE)

### 8. Admin Sefer Silme
1. Admin kullanıcı sefer listesinden bir seferi siler
2. `trips` tablosundan DELETE yapılır
3. CASCADE DELETE sayesinde:
   - İlgili tüm koltuklar (`seats`) otomatik silinir
   - İlgili tüm rezervasyonlar (`reservations`) otomatik silinir

## 👥 Test Kullanıcıları

Uygulama **ilk açılışta** (veritabanı boşsa) otomatik olarak aşağıdaki test kullanıcıları ve seferler **veritabanına kalıcı olarak yüklenir**. Bu veriler veritabanında saklanır ve uygulama her açıldığında aynı veriler gelir. Uygulama verilerini temizlemediğiniz sürece bu kullanıcılar ve seferler her zaman mevcut olacaktır.

### Admin Kullanıcı
- **Email**: `admin@sorareservation.com`
- **Şifre**: `admin123`
- **Ad**: Admin User
- **Telefon**: 555-0001
- **Yetki**: Admin (sefer ekleme/silme yetkisi var)

### Normal Kullanıcılar

#### Kullanıcı 1
- **Email**: `user1@example.com`
- **Şifre**: `user123`
- **Ad**: John Doe
- **Telefon**: 555-1001
- **Yetki**: Normal kullanıcı
- **Mevcut Rezervasyonlar**: 
  - Istanbul → Ankara seferinde 2 koltuk (10, 11)
  - Ankara → Izmir seferinde 1 koltuk (20)

#### Kullanıcı 2
- **Email**: `user2@example.com`
- **Şifre**: `user123`
- **Ad**: Jane Smith
- **Telefon**: 555-1002
- **Yetki**: Normal kullanıcı

## 🚌 Örnek Seferler

Uygulama **ilk açılışta** (veritabanı boşsa) 5 örnek sefer **veritabanına kalıcı olarak yüklenir**. Bu seferler veritabanında saklanır ve uygulama her açıldığında aynı seferler görünür. Uygulama verilerini temizlemediğiniz sürece bu seferler her zaman mevcut olacaktır.

1. **Istanbul → Ankara**
   - Tarih: Bugün
   - Saat: 10:00
   - Fiyat: 250.0 TL
   - Dolu Koltuklar: 12 koltuk (6 erkek, 6 kadın)

2. **Ankara → Izmir**
   - Tarih: Bugün
   - Saat: 14:30
   - Fiyat: 300.0 TL
   - Dolu Koltuklar: 10 koltuk (5 erkek, 5 kadın)

3. **Istanbul → Antalya**
   - Tarih: Yarın
   - Saat: 08:00
   - Fiyat: 350.0 TL
   - Dolu Koltuklar: 5 koltuk (4 erkek, 1 kadın)

4. **Izmir → Ankara**
   - Tarih: Yarın
   - Saat: 16:00
   - Fiyat: 280.0 TL
   - Dolu Koltuklar: 4 koltuk (4 kadın)

5. **Ankara → Istanbul**
   - Tarih: Öbür gün
   - Saat: 12:00
   - Fiyat: 250.0 TL
   - Dolu Koltuklar: 0 koltuk (tamamen boş)

## 🎨 Koltuk Renk Kodları

- **Yeşil (#4CAF50)**: Müsait koltuklar (AVAILABLE)
- **Mavi (#2196F3)**: Erkek tarafından dolu/seçili koltuklar
- **Pembe (#FFC1CB)**: Kadın tarafından dolu/seçili koltuklar
- **Gri (#E0E0E0)**: Cinsiyet bilgisi olmayan dolu koltuklar
- **Turuncu (#FF6B35)**: Seçili koltuklar (cinsiyet seçilmemişse)

## 📂 Proje Yapısı

```
app/src/main/java/com/sorareservation/
├── data/
│   ├── SeferLab.kt              # Singleton veri yönetimi
│   ├── SeferDbHelper.kt         # SQLiteOpenHelper
│   ├── SeferDbSchema.kt         # Veritabanı şema tanımları
│   ├── UserCursorWrapper.kt     # User için Cursor wrapper
│   ├── TripCursorWrapper.kt     # Trip için Cursor wrapper
│   ├── SeatCursorWrapper.kt    # Seat için Cursor wrapper
│   └── ReservationCursorWrapper.kt # Reservation için Cursor wrapper
├── model/
│   ├── User.kt                  # Kullanıcı modeli
│   ├── Trip.kt                  # Sefer modeli
│   ├── Seat.kt                  # Koltuk modeli
│   └── Reservation.kt          # Rezervasyon modeli
├── ui/
│   ├── SingleFragmentActivity.kt # Base Activity sınıfı
│   ├── login/                   # Giriş ekranı
│   ├── register/                # Kayıt ekranı
│   ├── triplist/                # Sefer listesi ekranı
│   ├── tripdetail/              # Sefer detay ve koltuk seçimi
│   ├── reservationlist/         # Rezervasyon listesi
│   ├── confirmation/            # Rezervasyon onay ekranı
│   └── admin/                   # Admin paneli
└── SoraReservationApplication.kt # Application sınıfı
```

## 🚀 Android Studio'da Açma ve Çalıştırma

### 1. Projeyi Android Studio'da Açın

1. Android Studio'yu açın
2. "Open" veya "Open an Existing Project" seçeneğini tıklayın
3. Proje klasörünü seçin
4. Android Studio projeyi import edecek ve Gradle sync yapacak

### 2. Gradle Sync

Android Studio otomatik olarak Gradle sync yapacaktır. Eğer yapmazsa:
- "File" > "Sync Project with Gradle Files" menüsünü seçin
- Veya üstteki "Sync Now" butonuna tıklayın

### 3. Emülatör Kurulumu

1. Android Studio'da "Tools" > "Device Manager" (veya "AVD Manager") menüsünü açın
2. "Create Device" butonuna tıklayın
3. Bir cihaz seçin (örneğin: Pixel 4)
4. "Next" butonuna tıklayın
5. Sistem görüntüsü seçin (örneğin: Android 11 - API 30 veya üzeri)
6. Eğer sistem görüntüsü yoksa "Download" butonuna tıklayarak indirin
7. "Next" ve "Finish" butonlarına tıklayın

### 4. Emülatörü Başlatın

1. Device Manager'da oluşturduğunuz emülatörün yanındaki ▶ (Play) butonuna tıklayın
2. Emülatör açılacaktır (ilk açılış biraz zaman alabilir)

### 5. Uygulamayı Çalıştırın

1. Android Studio'da üst menüden "Run" > "Run 'app'" seçeneğini seçin
2. Veya yeşil ▶ (Run) butonuna tıklayın
3. Açılan pencerede oluşturduğunuz emülatörü seçin
4. "OK" butonuna tıklayın
5. Uygulama build edilecek ve emülatörde çalışacaktır

## 🔍 Veritabanını Görüntüleme

### Android Studio Database Inspector

1. Uygulamayı emülatörde çalıştırın
2. Android Studio'da **View > Tool Windows > App Inspection** menüsünü açın
3. **Database Inspector** sekmesini seçin
4. `sorareservation.db` dosyasını bulun
5. Tabloları genişletip verileri görüntüleyin

### ADB Shell ile

```bash
# Emülatöre bağlan
adb shell

# Veritabanı dosyasına eriş
cd /data/data/com.sorareservation/databases
sqlite3 sorareservation.db

# SQL sorguları çalıştır
.tables
SELECT * FROM users;
SELECT * FROM trips;
SELECT * FROM seats;
SELECT * FROM reservations;
```

## ⚠️ Önemli Notlar

- **Veritabanı**: Tüm veriler SQLite veritabanında **kalıcı olarak** saklanır
- **Başlangıç Verileri**: İlk açılışta (veritabanı boşsa) örnek kullanıcılar ve seferler otomatik olarak veritabanına yüklenir ve **kalıcı olarak saklanır**
- **Veri Kalıcılığı**: Uygulama kapatılıp tekrar açıldığında tüm veriler (kullanıcılar, seferler, rezervasyonlar) aynen gelir
- **Veri Temizleme**: Uygulama verilerini temizlerseniz (`Settings > Apps > SoraReservation > Clear Data`), veritabanı da silinir ve ilk açılışta tekrar başlangıç verileri yüklenir
- **Cinsiyet Bazlı Koltuk Seçimi**: Erkek ve kadın yolcular yan yana oturamaz
- **SELECTED Durumu**: Sadece memory'de tutulur (veritabanında sadece AVAILABLE/OCCUPIED)
- **CASCADE DELETE**: Sefer silindiğinde ilgili koltuklar ve rezervasyonlar otomatik silinir
- **Ekran Döndürme**: Tüm Fragment'lerde `onSaveInstanceState` ile state korunur
- **Thread Safety**: SQLite thread-safe, ancak write işlemleri için dikkatli olunmalı

## 🐛 Sorun Giderme

### Gradle Sync Hatası
- "File" > "Invalidate Caches / Restart" > "Invalidate and Restart" yapın

### Emülatör Açılmıyor
- Android Studio'yu yeniden başlatın
- Emülatörü Device Manager'dan silip yeniden oluşturun

### Build Hatası
- "Build" > "Clean Project" yapın
- Ardından "Build" > "Rebuild Project" yapın

### Veritabanı Görünmüyor
- Uygulamayı en az bir kez çalıştırdığınızdan emin olun
- Database Inspector'da "Refresh" butonuna tıklayın

## 📝 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
