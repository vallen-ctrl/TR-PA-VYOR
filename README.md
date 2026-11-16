<div align="center">

# TR-PA-VYOR

**Sistem kasir (point of sale) berbasis Java dengan QRIS dinamis, manajemen supplier, pengingat kedaluwarsa produk, dan voucher ulang tahun pelanggan.**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Apache Ant](https://img.shields.io/badge/Apache%20Ant-A81C7D?style=for-the-badge&logo=apacheant&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apachenetbeanside&logoColor=white)
![SQL](https://img.shields.io/badge/Database-SQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Status](https://img.shields.io/badge/status-in%20development-yellow?style=for-the-badge)

[Tentang](#-tentang-proyek) •
[Fitur](#-fitur-utama) •
[Teknologi](#-teknologi) •
[Struktur](#-struktur-proyek) •
[Aturan](#-aturan-penulisan) •
[Instalasi](#-instalasi) •
[Konfigurasi](#-konfigurasi) •
[Tim](#-tim)

</div>

---

## 📖 Tentang Proyek

TR-PA-VYOR adalah aplikasi kasir yang membantu pemilik toko mengelola transaksi, pemasok, dan stok produk dalam satu sistem. Aplikasi ini menyediakan dua tampilan, yaitu tampilan **Kasir** dan tampilan **Pelanggan**, serta pembayaran lewat QRIS dinamis yang dibuat unik untuk setiap transaksi.

Data produk juga divalidasi sebelum masuk ke database, dan pemilik toko mendapat pengingat sebelum produk kedaluwarsa.

> **[ISI: tambahkan konteks proyek, misalnya tugas mata kuliah apa atau untuk keperluan apa.]**

---

## ✨ Fitur Utama

| No | Fitur | Deskripsi |
| -- | ----- | --------- |
| 1 | **Transaction Management** | Membantu pemilik mengelola transaksi dan melihat laporan ringkasnya. |
| 2 | **Supplier Management** | Mengelola data supplier dan melacak pembelian dari masing-masing supplier. |
| 3 | **Dual Display** | Mendukung dua tampilan, yaitu **Customer** dan **Cashier**. |
| 4 | **Dynamic QRIS** | Mempermudah pembayaran dengan membuat kode QR unik untuk setiap transaksi. |
| 5 | **Expiry Reminder** | Melacak tanggal kedaluwarsa tiap batch produk dan memberi tahu pemilik sebelum barang kedaluwarsa, sehingga bisa dijual atau dibuang tepat waktu. |
| 6 | **Birthday Voucher** | Memberikan voucher diskon otomatis kepada pelanggan di hari ulang tahunnya, dengan masa berlaku terbatas. |
| 7 | **Checksum Validation** | Menghitung dan membandingkan checksum (EAN-13, EAN-8, Luhn) pada data masuk sebelum disimpan ke database. |

---

## 🛠 Teknologi

| Kategori | Teknologi |
| -------- | --------- |
| Bahasa | Java 26 |
| IDE | Apache NetBeans |
| Build tool | Apache Ant (`build.xml`) |
| Database | MySQL |
| Library | MySQL Connector/J 9.7.0 (satu-satunya dependency) |

**Library di folder `lib/`:**

| Library | Versi | Fungsi |
| ------- | ----- | ------ |
| MySQL Connector/J | 9.7.0 | Driver JDBC untuk koneksi ke MySQL |

Tidak ada library tambahan lain. Fitur seperti validasi checksum dan pembuatan QR dikerjakan tanpa dependency eksternal.

---

## 📂 Struktur Proyek

```
TR-PA-VYOR/
├── lib/              # Dependency (file .jar)
├── nbproject/        # Konfigurasi proyek NetBeans
├── src/              # Source code aplikasi
│   ├── config/       # Semua konfigurasi aplikasi
│   ├── controller/   # Jalur data antara view dan model
│   ├── model/        # Koneksi dan akses ke database
│   │   └── interfaces/   # Daftar kolom dari setiap data
│   ├── view/         # Tampilan (JForm)
│   │   └── components/   # Komponen tampilan yang dipakai ulang
│   ├── utils/        # Fungsi bantu
│   └── test/         # Kelas uji coba
├── .conf.example     # Template konfigurasi (salin menjadi .conf)
├── .gitignore        # Daftar file yang diabaikan git
├── build.xml         # Skrip build Apache Ant
├── manifest.mf       # Manifest untuk file JAR
├── opencode.json     # Konfigurasi tool AI coding
└── scheme.sql        # Skema database
```

---

## 📐 Aturan Penulisan

Bagian ini adalah patokan untuk semua anggota tim. Setiap folder punya satu tugas, jadi kode harus ditaruh di folder yang sesuai.

### Ringkasan

| Folder | Tugasnya |
| ------ | -------- |
| `config/` | Menyimpan semua pengaturan aplikasi |
| `model/` | Terhubung ke database (lewat `config`) |
| `model/interfaces/` | Mendaftar kolom dari setiap data |
| `controller/` | Mengatur perpindahan data antara `view` dan `model` |
| `view/` | Berisi JForm, yaitu tampilan aplikasi |
| `view/components/` | Berisi komponen tampilan yang dipakai ulang |
| `utils/` | Berisi fungsi bantu |
| `test/` | Berisi kelas uji coba |

### Alur data

Data selalu bergerak lewat jalur yang sama. `view` tidak boleh langsung bicara ke `model`, dan `model` tidak boleh langsung bicara ke `view`.

```
view  ⇄  controller  ⇄  model  ──►  config  ──►  database
```

Contohnya, saat kasir menekan tombol simpan: `view` mengirim data ke `controller`, `controller` meneruskannya ke `model`, lalu `model` menyimpannya ke database memakai koneksi dari `config`. Hasilnya kembali lewat jalur yang sama.

---

### `config/`

Folder ini berisi **semua konfigurasi aplikasi**, misalnya alamat database, port, username, dan password.

**Aturan utama: jangan pernah hardcode.**

- Jangan menulis nilai pengaturan langsung di dalam kode.
- Taruh nilainya di file `.conf`, lalu baca lewat folder `config/`.
- Setiap kali menambah pengaturan baru di `.conf`, **wajib menambahkannya juga di `.conf.example`**.
- Isi `.conf.example` hanya boleh kosong atau contoh yang aman. Jangan pernah mengisinya dengan nilai asli, apalagi password.
- File `.conf` tidak boleh ikut di-commit.

Contohnya:

```
# .conf  (hanya di komputer sendiri, tidak di-commit)
db.password=password-asli-saya

# .conf.example  (ikut di-commit)
db.password=
```

### `model/`

Folder ini adalah **bagian yang terhubung ke database**. Semua kode yang mengambil, menyimpan, mengubah, atau menghapus data ada di sini.

- Koneksi ke database **harus lewat `config/`**. Model tidak boleh membuat koneksi sendiri dengan host atau password yang ditulis langsung.
- Hanya folder ini yang boleh menulis query SQL.
- Query wajib memakai `PreparedStatement` supaya aman dari SQL injection.
- Tidak boleh ada kode tampilan di sini.

#### Aturan penulisan class di `model/`

**Setiap class di `model/` wajib `extends Model`.**

File `Model.java` adalah class induk yang sudah berisi fungsi-fungsi umum. Fungsi itu otomatis diturunkan ke setiap class model, jadi tidak perlu ditulis ulang.

```java
// Contoh bentuk penulisan
public class Customer extends Model {
    // Isi class khusus untuk data pelanggan saja
}
```

- Jangan menulis ulang fungsi yang sudah ada di `Model.java`. Pakai langsung fungsi turunannya.
- Kalau ada fungsi yang dibutuhkan oleh banyak class model, tambahkan di `Model.java`, jangan disalin ke tiap class.
- Isi class turunan hanya hal yang khusus untuk data itu saja.
- Nama class dan nama file harus sama persis, termasuk huruf besar dan kecilnya (`Model` di dalam `Model.java`).

**Fungsi yang diturunkan dari `Model.java`:**

| Fungsi | Kegunaan |
| ------ | -------- |
| `[ISI: nama fungsi]` | `[ISI: penjelasan singkat]` |
| `[ISI: nama fungsi]` | `[ISI: penjelasan singkat]` |

#### `model/interfaces/`

Folder ini berisi **daftar kolom dari setiap data**. Fungsinya seperti "bentuk" dari satu data, sehingga semua anggota tim tahu kolom apa saja yang dimiliki data itu tanpa harus membuka database.

Contohnya, untuk data pelanggan (`customers`), daftarnya berisi: `id`, `kode`, `nama`, `tanggal_lahir`, `nomor_hp`, `tanggal_daftar`, dan `dihapus`.

**Bentuk penulisan:**

```java
package Model.interfaces;

public class categoriesInterface {
    public Integer id;
    public String name;
    public Boolean dihapus;

    // Untuk data baru (id dibuat otomatis oleh database)
    public categoriesInterface(String name, boolean dihapus) {
        this.name = name;
        this.dihapus = dihapus;
    }

    // Untuk data yang diambil dari database
    public categoriesInterface(Integer id, String name, boolean dihapus) {
        this.id = id;
        this.name = name;
        this.dihapus = dihapus;
    }
}
```

Cara membacanya:

- Satu class untuk satu tabel, dan namanya berakhiran `Interface`.
- Satu field untuk setiap kolom di tabel.
- Ada **dua constructor**. Yang tanpa `id` dipakai untuk data baru yang belum disimpan, karena `id` dibuat otomatis oleh database. Yang memakai `id` dipakai untuk data yang diambil dari database.

**Aturan:**

- Satu interface untuk satu tabel.
- Kalau kolom di `scheme.sql` berubah, interface-nya juga harus diubah.
- Nama package memakai `interfaces`, bukan `interface`, karena `interface` adalah kata khusus di Java dan tidak boleh dipakai sebagai nama package.

### `controller/`

Folder ini adalah **jalur perpindahan data antara `view` dan `model`**.

- Menerima aksi dari `view`, lalu meneruskannya ke `model`.
- Mengambil hasil dari `model`, lalu mengirimkannya kembali ke `view`.
- Pengecekan data sebelum disimpan, seperti validasi checksum, dilakukan di sini.
- Tidak boleh menulis query SQL. Semua urusan database diserahkan ke `model`.
- Tidak boleh membuat atau mengatur tampilan secara langsung.

### `view/`

Folder ini berisi **JForm**, yaitu semua tampilan aplikasi, baik tampilan Kasir maupun tampilan Pelanggan.

- Tugasnya hanya menampilkan data dan menerima input dari pengguna.
- Setiap aksi pengguna, seperti klik tombol, langsung diteruskan ke `controller`.
- Tidak boleh menulis query SQL dan tidak boleh memanggil `model` secara langsung.

#### `view/components/`

Folder ini berisi **komponen yang dipakai di banyak tampilan**, misalnya tombol, kolom input, atau tabel dengan gaya khusus.

- Kalau sebuah komponen dipakai di lebih dari satu JForm, buat di sini. Jangan disalin ke setiap form.
- Dengan begitu, kalau tampilannya perlu diubah, cukup diubah di satu tempat.

### `utils/`

Folder ini berisi **fungsi bantu** yang berdiri sendiri, misalnya pengecekan checksum (EAN-13, EAN-8, Luhn), format Rupiah, dan pembuat kode QRIS.

- Tidak bergantung pada folder lain.
- Satu fungsi hanya mengerjakan satu hal.

### `test/`

Folder ini berisi **kelas uji coba** untuk memastikan bagian `model`, `controller`, dan `utils` berjalan benar.

- Satu kelas uji coba untuk satu kelas yang diuji.
- Jangan memakai database yang dipakai untuk data asli.

---

## 🚀 Instalasi

### Prasyarat

Pastikan perangkat kamu sudah memiliki:

- **JDK 26**
- **Apache Ant** (sudah termasuk di NetBeans, atau install terpisah untuk command line)
- **MySQL** yang sedang berjalan
- **Git**
- *(Opsional)* **Apache NetBeans** untuk membuka proyek lewat IDE

### 1. Clone repository

```bash
git clone https://github.com/vallen-ctrl/TR-PA-VYOR.git
cd TR-PA-VYOR
```

### 2. Siapkan database

Buat database baru, lalu impor skema dari `scheme.sql`:

```bash
mysql -u root -p -e "CREATE DATABASE [ISI: nama_database];"
mysql -u root -p [ISI: nama_database] < scheme.sql
```

### 3. Atur konfigurasi

Salin template konfigurasi, lalu isi dengan kredensial milikmu sendiri:

```bash
cp .conf.example .conf
```

Detail setiap pengaturan ada di bagian [Konfigurasi](#-konfigurasi).

### 4. Build dan jalankan

**Lewat command line:**

```bash
ant clean jar
java -jar dist/[ISI: NamaProject].jar
```

**Lewat NetBeans:**

1. Buka **File → Open Project**, lalu pilih folder repo ini.
2. Pastikan semua jar di folder `lib/` terbaca di **Libraries**.
3. Klik kanan proyek, pilih **Run**.

---

## ⚙️ Konfigurasi

File `.conf` **tidak boleh di-commit** karena berisi kredensial. Pastikan `.conf` tercantum di `.gitignore`.

Formatnya `key=value`, satu pengaturan per baris:

```properties
db.host=127.0.0.1
db.port=3306
db.name=
db.username=
db.password=
```

| Key | Deskripsi | Nilai bawaan |
| --- | --------- | ------------ |
| `db.host` | Alamat server MySQL | `127.0.0.1` |
| `db.port` | Port MySQL | `3306` |
| `db.name` | Nama database tujuan impor `scheme.sql` | *(wajib diisi)* |
| `db.username` | Username MySQL | *(wajib diisi)* |
| `db.password` | Password MySQL | *(wajib diisi, jangan dibagikan)* |

> ⚠️ Jangan pernah menaruh password asli di `.conf.example`. File itu hanya boleh berisi placeholder.

---

## 🗄 Skema Database

Skema lengkap ada di [`scheme.sql`](scheme.sql). Database terdiri dari **10 tabel**. Format di bawah: `kolom -> tipe data`, diikuti constraint di dalam kurung siku.

### Data master

#### `customers`

Data pelanggan. Tanggal lahir dipakai untuk voucher ulang tahun. Dipakai oleh `vouchers` dan `transactions`.

```
id             -> INT           [PK, AUTO_INCREMENT]
kode           -> VARCHAR(10)
nama           -> VARCHAR(100)  [NOT NULL]
tanggal_lahir  -> DATE          [NOT NULL]
nomor_hp       -> VARCHAR(20)   [NOT NULL, UNIQUE]
tanggal_daftar -> TIMESTAMP     [NOT NULL, default waktu sekarang]
dihapus        -> SMALLINT      [NOT NULL, default 0]
```

#### `suppliers`

Data pemasok barang. Dipakai oleh `products` dan `product_batches`.

```
id       -> INT           [PK, AUTO_INCREMENT]
nama     -> VARCHAR(150)  [NOT NULL]
alamat   -> VARCHAR(255)  [NOT NULL]
nomor_hp -> VARCHAR(20)   [NOT NULL, UNIQUE]
dihapus  -> SMALLINT      [NOT NULL, default 0]
```

#### `categories`

Kategori produk. Dipakai oleh `products`.

```
id      -> INT          [PK, AUTO_INCREMENT]
nama    -> VARCHAR(50)  [NOT NULL, UNIQUE]
dihapus -> SMALLINT     [NOT NULL, default 0]
```

#### `products`

Data produk yang dijual. Terhubung ke `categories` dan `suppliers`.

```
id                -> INT            [PK, AUTO_INCREMENT]
nomor             -> VARCHAR(13)    [NOT NULL, UNIQUE (nomor barcode)]
nama              -> VARCHAR(150)   [NOT NULL]
category_id       -> INT            [NOT NULL, FK -> categories.id]
supplier_id       -> INT            [NOT NULL, FK -> suppliers.id]
satuan            -> VARCHAR(20)    [NOT NULL, default 'pcs']
harga_beli        -> DECIMAL(12,2)  [NOT NULL, default 0]
harga_jual        -> DECIMAL(12,2)  [NOT NULL, default 0]
stok              -> INT            [NOT NULL, default 0]
terakhir_disuplai -> DATE           [NOT NULL]
dihapus           -> SMALLINT       [NOT NULL, default 0]
```

### Stok dan kedaluwarsa

#### `product_batches`

Satu baris per batch barang masuk. Menjadi dasar fitur Expiry Reminder dan riwayat pembelian dari supplier. Terhubung ke `products` dan `suppliers`.

```
id                 -> INT            [PK, AUTO_INCREMENT]
product_id         -> INT            [NOT NULL, FK -> products.id]
supplier_id        -> INT            [NOT NULL, FK -> suppliers.id]
harga_beli         -> DECIMAL(12,2)  [NOT NULL]
qty                -> INT            [NOT NULL]
qty_sisa           -> INT            [NOT NULL]
tanggal_pemesanan  -> DATE           [NOT NULL]
tanggal_masuk      -> DATE           [NOT NULL]
tanggal_kadaluarsa -> DATE           [NOT NULL]
ditangani          -> BOOLEAN        [NOT NULL, default FALSE]
```

### Voucher

#### `vouchers`

Voucher diskon per pelanggan. Dipakai oleh fitur Birthday Voucher. Terhubung ke `customers`.

```
id             -> INT                                    [PK, AUTO_INCREMENT]
customer_id    -> INT                                    [NOT NULL, FK -> customers.id]
kode           -> VARCHAR(5)                             [NOT NULL, ascii_bin]
jenis          -> VARCHAR(255)                           [NOT NULL, default 'ulang_tahun']
persen_diskon  -> DECIMAL(5,2)                           [NOT NULL, default 10.00]
berlaku_dari   -> DATE                                   [NOT NULL]
berlaku_sampai -> DATE                                   [NOT NULL]
status         -> ENUM('aktif','dipakai','kedaluwarsa')  [NOT NULL, default 'aktif']
```

### Transaksi

#### `transactions`

Header transaksi penjualan. Terhubung ke `customers` dan `vouchers` (keduanya opsional).

```
id             -> BIGINT                                 [PK, AUTO_INCREMENT]
kode           -> VARCHAR(20)
waktu          -> TIMESTAMP                              [NOT NULL, default waktu sekarang]
customer_id    -> INT                                    [NULL, FK -> customers.id]
voucher_id     -> INT                                    [NULL, FK -> vouchers.id (SET NULL saat voucher dihapus)]
sub_total      -> DECIMAL(14,2)                          [NOT NULL, default 0]
diskon_voucher -> DECIMAL(14,2)                          [NOT NULL, default 0]
pajak_persen   -> DECIMAL(5,2)                           [NOT NULL, default 11.00]
pajak          -> DECIMAL(14,2)                          [NOT NULL, default 0]
grand_total    -> DECIMAL(14,2)                          [NOT NULL, default 0]
bayar          -> DECIMAL(14,2)                          [NOT NULL, default 0]
kembalian      -> DECIMAL(14,2)                          [NOT NULL, default 0]
status         -> ENUM('selesai','dibatalkan','refund')  [NOT NULL, default 'selesai']
```

#### `transaction_items`

Rincian barang per transaksi. Kode, nama, dan harga disalin saat transaksi sehingga laporan lama tidak berubah walau data produk diubah. Terhubung ke `transactions` dan `products`.

```
id             -> BIGINT         [PK, AUTO_INCREMENT]
transaction_id -> BIGINT         [NOT NULL, FK -> transactions.id]
product_id     -> INT            [NOT NULL, FK -> products.id]
kode           -> VARCHAR(13)    [NOT NULL]
nama           -> VARCHAR(150)   [NOT NULL]
jumlah         -> INT            [NOT NULL]
satuan         -> VARCHAR(20)    [NOT NULL]
harga          -> DECIMAL(12,2)  [NOT NULL]
total          -> DECIMAL(14,2)
```

#### `returns`

Header retur barang. Terhubung ke `transactions`.

```
id             -> BIGINT         [PK, AUTO_INCREMENT]
transaction_id -> BIGINT         [NOT NULL, FK -> transactions.id]
waktu          -> TIMESTAMP      [NOT NULL, default waktu sekarang]
alasan         -> VARCHAR(255)   [NOT NULL]
total_refund   -> DECIMAL(14,2)  [NOT NULL, default 0]
```

#### `return_items`

Rincian barang yang diretur. Terhubung ke `returns` dan `transaction_items`.

```
id                  -> BIGINT  [PK, AUTO_INCREMENT]
return_id           -> BIGINT  [NOT NULL, FK -> returns.id]
transaction_item_id -> BIGINT  [NOT NULL, FK -> transaction_items.id]
jumlah              -> INT     [NOT NULL]
```

### Catatan desain

- Penghapusan data master memakai **soft delete** lewat kolom `dihapus`, jadi data tidak benar-benar hilang dari database.
- Semua relasi memakai `ON DELETE RESTRICT`, kecuali `transactions.voucher_id` yang menjadi `NULL` jika voucher dihapus.
- Seluruh nilai uang memakai tipe `DECIMAL`, bukan `FLOAT`, supaya tidak ada selisih pembulatan.

---

## 🧪 Troubleshooting

| Masalah | Penyebab umum | Solusi |
| ------- | ------------- | ------ |
| `ClassNotFoundException` untuk driver database | Jar driver belum terbaca | Pastikan jar ada di `lib/` dan terdaftar di Libraries proyek |
| Koneksi database gagal | Kredensial atau host salah | Periksa isi `.conf` dan pastikan server database berjalan |
| `ant: command not found` | Ant belum terpasang | Install Apache Ant atau jalankan build lewat NetBeans |

---

## 👥 Tim

| Nama | Peran | GitHub |
| ---- | ----- | ------ |
| [ISI: Nama] | [ISI: Peran] | [@vallen-ctrl](https://github.com/vallen-ctrl) |
| [ISI: Nama] | [ISI: Peran] | [ISI] |

---

## 📄 Lisensi

[ISI: pilih lisensi, misalnya MIT, atau tulis "Proyek ini dibuat untuk keperluan akademik."]

---

<div align="center">

Dibuat dengan ☕ dan Java

</div>
