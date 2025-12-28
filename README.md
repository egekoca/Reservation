# SoraReservation - Otobüs Bileti Rezervasyon Sistemi

Kamilkoç/Obilet tarzı otobüs bileti rezervasyon sistemi mobil uygulaması.

## Özellikler

- Kullanıcı kayıt/giriş sistemi
- Sefer listeleme ve arama
- Koltuk seçimi (görsel gösterim)
- Rezervasyon oluşturma/iptal etme
- Admin paneli (sefer ekleme/silme)
- Rezervasyon paylaşımı

## Teknik Detaylar

- **Dil:** Kotlin
- **UI:** XML Layout (ViewBinding)
- **Mimari:** Single Fragment Activity Pattern
- **Veri Yönetimi:** Singleton Pattern (SeferLab)
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34

## Android Studio'da Açma ve Çalıştırma

### 1. Projeyi Android Studio'da Açın

1. Android Studio'yu açın
2. "Open" veya "Open an Existing Project" seçeneğini tıklayın
3. `/Users/ege/Desktop/projects/Reservation` klasörünü seçin
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

## Test Kullanıcıları

### Admin Kullanıcı
- **Email:** admin@sorareservation.com
- **Password:** admin123

### Normal Kullanıcılar
- **Email:** user1@example.com
- **Password:** user123

- **Email:** user2@example.com
- **Password:** user123

## Proje Yapısı

```
app/src/main/java/com/sorareservation/
├── data/
│   └── SeferLab.kt          # Singleton veri yönetimi
├── model/
│   ├── User.kt
│   ├── Trip.kt
│   ├── Seat.kt
│   └── Reservation.kt
└── ui/
    ├── SingleFragmentActivity.kt
    ├── login/
    ├── register/
    ├── triplist/
    ├── tripdetail/
    ├── reservationlist/
    └── admin/
```

## Önemli Notlar

- Uygulama şu anda **mock data** ile çalışmaktadır
- Veritabanı entegrasyonu sonraki aşamada yapılacaktır
- Tüm ekranlar Fragment olarak tasarlanmıştır
- Activity'ler sadece container görevi görmektedir
- Intent encapsulation pattern kullanılmıştır
- Ekran döndürme desteği tüm Fragment'lerde mevcuttur

## Sorun Giderme

### Gradle Sync Hatası
- "File" > "Invalidate Caches / Restart" > "Invalidate and Restart" yapın

### Emülatör Açılmıyor
- Android Studio'yu yeniden başlatın
- Emülatörü Device Manager'dan silip yeniden oluşturun

### Build Hatası
- "Build" > "Clean Project" yapın
- Ardından "Build" > "Rebuild Project" yapın

