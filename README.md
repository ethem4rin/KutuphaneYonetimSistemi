# Kütüphane Yönetim Sistemi

## Proje Hakkında
Bu proje, Java Programlama Dili kullanılarak geliştirilmiş bir **Kütüphane Yönetim Sistemi**dir.
Projenin amacı; kütüphanedeki kitapların, kullanıcıların ve ödünç alma süreçlerinin
nesne yönelimli programlama (OOP) prensipleri ve tasarım desenleri kullanılarak
etkin bir şekilde yönetilmesini sağlamaktır.

Uygulama, **Üye** ve **Personel** olmak üzere iki farklı kullanıcı rolüne sahiptir
ve rol bazlı yetkilendirme mantığıyla çalışmaktadır.

---

## Kullanılan Teknolojiler
- Java
- JavaFX (GUI)
- SQLite
- Maven
- Nesne Yönelimli Programlama (OOP)
- MVC Mimari Yapısı
- Git & GitHub

---

## Kullanılan Tasarım Desenleri
Projede hocanın zorunlu tuttuğu ve ek olarak kullanılan tasarım desenleri aşağıda açıklanmıştır:

### 1. Singleton Pattern
Veritabanı bağlantısının uygulama genelinde tek bir nesne üzerinden yönetilmesi amacıyla kullanılmıştır.
Bu sayede kaynak yönetimi sağlanmış ve gereksiz bağlantı oluşturulmasının önüne geçilmiştir.

### 2. Factory Pattern
Kullanıcı türlerine (Üye / Personel) göre nesne üretimini merkezi bir yapıdan gerçekleştirmek
amacıyla kullanılmıştır. Böylece nesne oluşturma işlemleri kontrol altına alınmıştır.

### 3. Observer Pattern
Ödünç alma ve iade gibi veri değişikliklerinde, arayüzün otomatik olarak güncellenmesini
sağlamak amacıyla kullanılmıştır. Veri değiştiğinde ilgili ekranlar bilgilendirilmektedir.

### 4. State Pattern
Üyelerin veya ödünç işlemlerinin durum yönetimini sağlamak amacıyla kullanılmıştır.
Bir üyenin veya ödünç kaydının farklı durumlara (aktif, askıya alınmış vb.) sahip olması
State tasarım deseni ile modellenmiştir. Bu sayede durum geçişleri daha düzenli ve
genişletilebilir bir şekilde yönetilmiştir.

### 5. Strategy Pattern
Kullanıcı rollerine göre yetkilendirme ve davranış farklılıklarının dinamik olarak
belirlenmesi amacıyla kullanılmıştır. Üye ve personel için farklı stratejiler uygulanmıştır.

### 6. Repository Pattern
Veritabanı işlemlerinin (CRUD) controller katmanından ayrılması ve daha temiz,
bakımı kolay bir mimari oluşturulması amacıyla kullanılmıştır.

---

## Projede Gerçekleştirilen İşlevler

### Kullanıcı İşlemleri
- Üye ve personel giriş sistemi
- Rol bazlı yetkilendirme
- Kullanıcı ekleme, listeleme, güncelleme ve silme

### Kitap İşlemleri
- Kitap ekleme
- Kitap listeleme
- Kitap güncelleme
- Kitap silme
- Kitap adı, yazar, ISBN ve kategoriye göre arama

### Ödünç Alma İşlemleri
- Kitap ödünç verme
- Kitap iade etme
- Aktif ödünç kayıtlarını görüntüleme
- Üye ve ödünç durumlarının State Pattern ile yönetilmesi

---

## CRUD İşlemleri
Projede **Create, Read, Update, Delete (CRUD)** işlemleri aşağıdaki varlıklar için
eksiksiz bir şekilde uygulanmıştır:
- Kitaplar
- Kullanıcılar (Üye / Personel)
- Ödünç kayıtları

Tüm CRUD işlemleri repository katmanında gerçekleştirilmiştir.

---

## Proje Yapısı
- `model` : Veri modelleri
- `repository` : Veritabanı işlemleri
- `controller` : İş mantığı ve kullanıcı arayüzü
- `observer` : Observer tasarım deseni yapıları
- `strategy` : Yetkilendirme stratejileri
- `state` : Durum yönetimi (State Pattern)
- `db` : Veritabanı bağlantı yönetimi
- `diagrams` : Use Case, ER, Abstract Class ve Sequence diyagramları

---

## Diyagramlar
Projede aşağıdaki diyagramlar hazırlanmıştır:
- Use Case Diagram
- ER Diagram
- Abstract Class Diagram
- Sequence Diagram

Tüm diyagramlar proje içerisinde `diagrams` klasöründe yer almaktadır.

---

## Kurulum ve Çalıştırma
1. Projeyi bilgisayarınıza indirin veya klonlayın.
2. Java ve JavaFX kurulu olduğundan emin olun.
3. Projeyi bir IDE (IntelliJ IDEA veya Eclipse) üzerinden açın.
4. `MainApplication.java` dosyasını çalıştırarak uygulamayı başlatın.

---

## Proje Ekibi
- Miraç Altındağ  
- İbrahim Ethem Arın
- Okan Özkaya

---

## Not
Bu proje, **Java Programlama Dersi** kapsamında akademik amaçlarla geliştirilmiştir.
