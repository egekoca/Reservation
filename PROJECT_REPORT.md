# SoraReservation - Proje Raporu

## 📋 İçindekiler

1. [Sistem Özellikleri](#sistem-özellikleri)
2. [Kaynak Kodları](#kaynak-kodları)
3. [Ekran Görüntüleri](#ekran-görüntüleri)

---

## Sistem Özellikleri

### Genel Bakış

**SoraReservation**, Kamilkoç/Obilet tarzı otobüs bileti rezervasyon sistemi mobil uygulamasıdır. Kotlin ile geliştirilmiş, SQLite veritabanı kullanan modern bir Android uygulamasıdır.

### Teknoloji Stack
- **Dil**: Kotlin
- **UI Framework**: XML Layout + ViewBinding
- **Veritabanı**: SQLite (SQLiteOpenHelper, CursorWrapper)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34

### Ana Özellikler

#### 1. Kullanıcı Yönetimi
- **Kayıt Olma**: Email, şifre, ad-soyad, telefon ile kayıt
- **Giriş Yapma**: Email ve şifre ile giriş
- **Admin Yetkisi**: Admin kullanıcılar sefer ekleyip silebilir

#### 2. Sefer Yönetimi
- **Sefer Listeleme**: Tüm seferleri görüntüleme
- **Sefer Arama**: Kalkış ve varış şehirlerine göre filtreleme (81 Türk şehri)
- **Sefer Detayları**: Sefer bilgileri, koltuk durumları, fiyat

#### 3. Koltuk Seçimi
- **2+1 Otobüs Düzeni**: Gerçekçi otobüs görünümü (sol: 2 koltuk, sağ: 1 koltuk)
- **Görsel Koltuk Durumları**: 
  - Müsait (yeşil)
  - Dolu (gri, alpha 0.6)
  - Seçili (mavi/pembe, cinsiyete göre)
- **Cinsiyet Bazlı Seçim**: Erkek ve kadın yolcular yan yana oturamaz
- **Komşu Koltuk Kontrolü**: Seçim sırasında cinsiyet çakışması kontrolü

#### 4. Rezervasyon Yönetimi
- **Rezervasyon Oluşturma**: Seçili koltuklar ile rezervasyon yapma
- **Rezervasyon Listesi**: Kullanıcının tüm rezervasyonlarını görüntüleme
- **Rezervasyon İptali**: Rezervasyon iptal etme (koltuklar tekrar müsait olur)
- **Rezervasyon Paylaşma**: Implicit Intent ile başka uygulamalara paylaşma

#### 5. Admin Paneli
- **Sefer Ekleme**: Yeni sefer ekleme (tarih, saat, fiyat, koltuk sayısı)
- **Sefer Silme**: Mevcut seferleri silme (CASCADE DELETE ile koltuklar ve rezervasyonlar da silinir)

### Kullanılan Widget'lar ve Teknolojiler

#### RecyclerView (6 Farklı Yerde)
1. **Trip List**: Sefer listesi (LinearLayoutManager)
2. **Seat Selection - Sol**: Sol taraftaki çift koltuklar (GridLayoutManager, 2 sütun)
3. **Seat Selection - Sağ**: Sağ taraftaki tek koltuklar (LinearLayoutManager, 1 sütun)
4. **Reservation List**: Rezervasyon listesi (LinearLayoutManager)
5. **Selected Seats**: Seçili koltuklar (LinearLayoutManager, horizontal)
6. **Admin Panel Trip List**: Admin sefer listesi (LinearLayoutManager)

#### Implicit Intent
- **Rezervasyon Paylaşma**: `Intent.ACTION_SEND` ile sistem uygun uygulamaları bulur (WhatsApp, Gmail, SMS, vb.)

#### Diğer Widget'lar
- **AutoCompleteTextView**: Şehir seçimi (81 Türk şehri)
- **MaterialButton**: Tüm butonlar
- **MaterialCardView**: Kart görünümleri
- **TextInputLayout**: Input alanları (OutlinedBox style)
- **RadioGroup**: Cinsiyet seçimi
- **BottomNavigationView**: Ana navigasyon
- **Toolbar**: Başlık ve geri butonu
- **DatePickerDialog**: Tarih seçimi
- **TimePickerDialog**: Saat seçimi
- **MaterialAlertDialogBuilder**: Özel uyarı dialogu

### Mimari Yapı

#### Single Fragment Activity Pattern
- Tüm Activity'ler `SingleFragmentActivity` abstract sınıfından türer
- Activity'ler sadece container görevi görür
- İş mantığı Fragment'lerde bulunur

#### Singleton Pattern (SeferLab)
- Tüm veri işlemleri `SeferLab` object'inden yapılır
- Veritabanı bağlantısı tek bir yerde yönetilir
- Kod tekrarı önlenir

#### Intent Encapsulation
- Her Activity'de `companion object` içinde `newIntent()` metodu
- String key'ler hardcode edilmez

### Veritabanı Yapısı

#### Tablolar
1. **users**: Kullanıcı bilgileri (id, email, password, full_name, phone, is_admin)
2. **trips**: Sefer bilgileri (id, departure_city, arrival_city, departure_date, departure_time, price, total_seats)
3. **seats**: Koltuk bilgileri (id, trip_id, seat_number, status, gender)
4. **reservations**: Rezervasyon bilgileri (id, user_id, trip_id, seat_numbers, total_price, reservation_date)

#### İlişkiler
- `seats.trip_id` → `trips.id` (ON DELETE CASCADE)
- `reservations.user_id` → `users.id` (ON DELETE CASCADE)
- `reservations.trip_id` → `trips.id` (ON DELETE CASCADE)

#### Index'ler
- `idx_users_email`: Email araması için
- `idx_trips_departure`: Kalkış şehri araması için
- `idx_trips_arrival`: Varış şehri araması için
- `idx_seats_trip`: Koltuk yükleme için
- `idx_reservations_user`: Kullanıcı rezervasyonları için

### State Preservation (Ekran Döndürme)

Tüm Fragment'lerde `onSaveInstanceState()` kullanılarak:
- **LoginFragment**: Email, password
- **RegisterFragment**: Tüm form alanları
- **TripListFragment**: Departure, arrival şehirleri
- **TripDetailFragment**: Seçili koltuk numaraları
- **AdminPanelFragment**: Form alanları

### Tasarım Özellikleri

- **Renk Teması**: Turuncu-beyaz (orange-white)
- **Material Design**: Material Components kullanımı
- **Responsive**: Farklı ekran boyutlarına uyumlu
- **Modern UI**: Gradient background, card view'lar, rounded corners
- **Görsel İkonlar**: Cinsiyet ikonları (M/F), otobüs düzeni

---

## Kaynak Kodları

> **Not**: Tüm kaynak kodlar detaylı Türkçe yorum satırları ile açıklanmıştır. Kod dosyaları proje içinde bulunmaktadır.

### Önemli Dosyalar ve Açıklamaları

#### 1. Mimari Yapı

**SingleFragmentActivity.kt**
- Tüm Activity'lerin temel sınıfı
- Fragment container yönetimi
- Bottom navigation bar yönetimi
- Activity stack yönetimi (FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_NEW_TASK)

**SeferLab.kt**
- Singleton pattern ile veri yönetimi
- Veritabanı işlemleri (CRUD)
- User, Trip, Reservation, Seat işlemleri
- Transaction yönetimi (atomik işlemler)

#### 2. Veritabanı

**SeferDbHelper.kt**
- SQLiteOpenHelper implementasyonu
- Tablo oluşturma (onCreate)
- Index oluşturma
- Foreign key constraints

**SeferDbSchema.kt**
- Tablo ve sütun isimleri
- Type-safe schema tanımları

**CursorWrapper Sınıfları**
- UserCursorWrapper.kt: Cursor'dan User objesine dönüşüm
- TripCursorWrapper.kt: Cursor'dan Trip objesine dönüşüm
- SeatCursorWrapper.kt: Cursor'dan Seat objesine dönüşüm (enum dönüşümleri)
- ReservationCursorWrapper.kt: Cursor'dan Reservation objesine dönüşüm

#### 3. Model Sınıfları

**User.kt**
- Kullanıcı modeli
- Email, password, fullName, phone, isAdmin

**Trip.kt**
- Sefer modeli
- Koltuk yönetimi (select, deselect, book)
- Cinsiyet bazlı koltuk seçimi kontrolü
- Komşu koltuk hesaplama (2+1 düzen)

**Seat.kt**
- Koltuk modeli
- Status enum (AVAILABLE, OCCUPIED, SELECTED)
- Gender enum (MALE, FEMALE)

**Reservation.kt**
- Rezervasyon modeli
- Paylaşma için summary metodu

#### 4. Fragment'ler

**LoginFragment.kt**
- Kullanıcı girişi
- Email/password validasyonu
- State preservation

**RegisterFragment.kt**
- Kullanıcı kaydı
- Form validasyonu
- State preservation

**TripListFragment.kt**
- Sefer listesi
- AutoCompleteTextView ile şehir seçimi
- Filtreleme
- RecyclerView kullanımı

**TripDetailFragment.kt**
- Sefer detayları
- 2+1 koltuk düzeni (2 RecyclerView)
- Cinsiyet seçimi
- Koltuk seçimi ve validasyonu
- Gender warning dialog

**ConfirmationFragment.kt**
- Rezervasyon onayı
- Seçili koltuklar gösterimi
- Fiyat hesaplama
- Rezervasyon oluşturma

**ReservationListFragment.kt**
- Rezervasyon listesi
- Paylaşma (Implicit Intent)
- İptal etme

**AdminPanelFragment.kt**
- Admin erişim kontrolü
- Sefer ekleme formu
- Sefer silme

#### 5. Adapter'lar

**TripAdapter.kt**
- Sefer listesi için RecyclerView adapter
- Item click listener

**SeatAdapter.kt**
- Koltuk seçimi için RecyclerView adapter
- Koltuk durumuna göre renk kodlama
- Gender icon gösterimi

**ReservationAdapter.kt**
- Rezervasyon listesi için RecyclerView adapter
- Share ve Cancel butonları

**SelectedSeatAdapter.kt**
- Seçili koltuklar için RecyclerView adapter
- Horizontal layout

#### 6. Activity'ler

Tüm Activity'ler `SingleFragmentActivity`'den türer:
- **LoginActivity**: LoginFragment container
- **RegisterActivity**: RegisterFragment container
- **TripListActivity**: TripListFragment container
- **TripDetailActivity**: TripDetailFragment container
- **ConfirmationActivity**: ConfirmationFragment container
- **ReservationListActivity**: ReservationListFragment container
- **AdminPanelActivity**: AdminPanelFragment container

Her Activity'de `companion object` içinde `newIntent()` metodu bulunur.

#### 7. Layout Dosyaları

**activity_fragment.xml**
- Base layout (tüm Activity'ler için)
- FragmentContainerView
- BottomNavigationView

**fragment_*.xml**
- Her Fragment için özel layout
- Material Design components
- ViewBinding ile bağlanır

**item_*.xml**
- RecyclerView item layout'ları
- MaterialCardView kullanımı

**dialog_*.xml**
- Custom dialog layout'ları

### Kod Yorumları

Tüm kaynak kodlarda detaylı Türkçe yorum satırları bulunmaktadır:

- **Sınıf açıklamaları**: Her sınıfın amacı
- **Metod açıklamaları**: Her metodun ne yaptığı
- **Parametre açıklamaları**: Parametrelerin anlamı
- **Dönüş değeri açıklamaları**: Dönüş değerlerinin anlamı
- **Kritik kod blokları**: Önemli işlemlerin açıklamaları
- **Algoritma açıklamaları**: Karmaşık işlemlerin adım adım açıklamaları

**Örnek Yorum Yapısı**:
```kotlin
/**
 * Kullanıcı girişi yapar
 * @param email Kullanıcı email adresi
 * @param password Kullanıcı şifresi
 * @return Giriş başarılı ise User objesi, değilse null
 */
fun login(email: String, password: String): User? {
    // Veritabanından kullanıcı sorgula
    // Email ve şifre eşleşiyorsa User objesi döndür
    // Eşleşmiyorsa null döndür
}
```

---

## Ekran Görüntüleri

> **Not**: Bu bölüm, uygulama çalıştırıldığında ekran görüntüleri ile doldurulacaktır.

### 1. Login Ekranı
- Logo ve uygulama adı
- Email ve şifre input alanları
- Login butonu
- Register linki

### 2. Register Ekranı
- Kayıt formu (ad-soyad, email, telefon, şifre)
- Register butonu
- Login linki

### 3. Trip List Ekranı
- Kalkış ve varış şehir seçimi (AutoCompleteTextView)
- Sefer listesi (RecyclerView)
- Boş durum gösterimi

### 4. Trip Detail Ekranı
- Sefer bilgileri (kalkış, varış, tarih, saat, fiyat)
- Cinsiyet seçimi (RadioGroup)
- Koltuk düzeni (2+1, 2 RecyclerView)
- Seçili koltuklar özeti
- Book Now butonu

### 5. Confirmation Ekranı
- Sefer bilgileri
- Seçili koltuklar (horizontal RecyclerView)
- Fiyat detayı
- Confirm and Continue butonu

### 6. Reservation List Ekranı
- Kullanıcının rezervasyonları (RecyclerView)
- Her rezervasyon için paylaş ve iptal butonları
- Boş durum gösterimi

### 7. Admin Panel Ekranı
- Sefer ekleme formu
- Tarih ve saat seçimi (DatePickerDialog, TimePickerDialog)
- Sefer listesi (silme ile)

### 8. Gender Warning Dialog
- Cinsiyet çakışması uyarı mesajı
- Custom dialog tasarımı

### 9. Bottom Navigation Bar
- Ana ekranlarda görünen navigasyon bar
- Home, My Reservations, Admin Panel menü öğeleri

### 10. Share Dialog
- Implicit Intent ile uygulama seçme dialogu
- WhatsApp, Gmail, SMS gibi seçenekler

---

## Sonuç

**SoraReservation** uygulaması, modern Android geliştirme pratikleri kullanılarak geliştirilmiştir. Uygulama:

- ✅ **Tasarım**: Modern, profesyonel, Material Design prensiplerine uygun
- ✅ **Özellikler**: Kapsamlı rezervasyon sistemi, admin paneli, cinsiyet bazlı koltuk seçimi
- ✅ **Kod Kalitesi**: Detaylı Türkçe yorumlar, clean code prensipleri
- ✅ **Widget Kullanımı**: RecyclerView (6 farklı yerde), Implicit Intent, AutoCompleteTextView, Material Components
- ✅ **Veritabanı**: SQLite ile kalıcı veri saklama, transaction yönetimi
- ✅ **State Management**: onSaveInstanceState ile ekran döndürme desteği
- ✅ **Mimari**: Single Fragment Activity, Singleton Pattern, Intent Encapsulation

Uygulama, ödev gereksinimlerini tam olarak karşılamakta ve profesyonel bir seviyede geliştirilmiştir.

---

**Rapor Tarihi**: 2024  
**Proje**: SoraReservation - Otobüs Bileti Rezervasyon Sistemi  
**Dil**: Kotlin  
**Platform**: Android
