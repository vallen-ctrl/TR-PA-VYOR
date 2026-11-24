/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.Color;
import utils.TimeNow;

/**
 *
 * @author vallen
 */
public class Transaksi extends javax.swing.JFrame {

    private BigDecimal JumlahDibayar = new BigDecimal(0);
    private String HexSelected = "#9999FF";
    private String HexUnselected = "#FFFFFF";
    private boolean isTunai = true;
    private final NumberFormat nf
            = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Transaksi.class.getName());
    private TimeNow TIME;
    
//  Variabel penampung perhitungan transaksi
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal pajak = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private Layar2 layarPembeli;
    private Integer activeCustomerId = null;
    private Integer activeVoucherId = null;
    private BigDecimal activeDiskonVoucher = BigDecimal.ZERO;
    
    private void SetMetodePembayaran(boolean isTunai) {
        this.isTunai = isTunai;

        if (this.isTunai) {
            this.ButtonSetCash.setBackground(Color.decode(HexSelected));
            this.ButtonSetQris.setBackground(Color.decode(HexUnselected));
        } else {
            this.ButtonSetCash.setBackground(Color.decode(HexUnselected));
            this.ButtonSetQris.setBackground(Color.decode(HexSelected));
        }
    }
    
    private void toProduk() {
        produk next = new produk();
        next.setLocation(this.getLocation());
        next.setSize(this.getSize());
        next.setVisible(true);

        if (this.layarPembeli != null) {
            this.layarPembeli.tutupLayar();
            this.layarPembeli = null;
        }

        for (java.awt.Window w : java.awt.Window.getWindows()) {
            if (w instanceof Layar2 && w.isDisplayable()) {
                w.dispose();
            }
        }

        if (this.TIME != null) {
            this.TIME.stopTime();
        }
        this.dispose();
    }
    
    private void mulaiJam() {
        this.TIME.startTime(this.TextDateTimeNow, this.TextDayNow);
    }

    private String formatUang(BigDecimal num) {
        this.nf.setMaximumFractionDigits(0);
        return "Rp. " + this.nf.format(num);
    }

    private void renderUang() {
        System.out.println("Nilai -> " + formatUang(this.JumlahDibayar));
        this.InputTextUangBayar.setText(formatUang(this.JumlahDibayar));
    }
    
    private void lakukanPembayaran(){
        this.ButtonBayarSekarang.doClick();
    }
    
    private void setupShortcut() {
        javax.swing.KeyStroke enterKey = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_ENTER, 0);
        javax.swing.KeyStroke f1Key = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F1, 0);
        javax.swing.KeyStroke f2Key = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F2, 0);
        javax.swing.KeyStroke f4Key = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F4, 0);
        javax.swing.KeyStroke f5Key = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F5, 0);

        this.getRootPane().registerKeyboardAction(
                e -> this.lakukanPembayaran(),
                enterKey,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        this.getRootPane().registerKeyboardAction(
                e -> new RiwayatTransaksi().setVisible(true),
                f1Key,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        this.getRootPane().registerKeyboardAction(
                e -> this.toProduk(),
                f2Key,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        this.getRootPane().registerKeyboardAction(
                e -> new RiwayatTransaksi().setVisible(true),
                f4Key,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        this.getRootPane().registerKeyboardAction(
                e -> this.bukaLayar2(),
                f5Key,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }


    // =========================================================================
    // PEMBAYARAN, QRIS, & KALKULASI TOTAL]
    // Catatan: Pastikan kolom ke-8 (indeks 7) pada JTable 'Tabel'
    // selalu berisi nilai Total harga per baris agar kalkulasi berjalan otomatis.
    // =========================================================================

    /**
     * Menghitung Sub Total dari seluruh baris di JTable, menambahkan Pajak 11%,
     * dan memperbarui tampilan Grand Total di panel kiri bawah maupun kanan atas.
     */
    
    public void hitungTotalBelanja() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) Tabel.getModel();
        subTotal = BigDecimal.ZERO;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object valTotal = model.getValueAt(i, 7); // Kolom indeks 7 = "Total"
            if (valTotal != null) {
                String clean = valTotal.toString().replaceAll("[^0-9]", "");
                if (!clean.isEmpty()) {
                    subTotal = subTotal.add(new BigDecimal(clean));
                }
            }
        }

        // Hitung Pajak 11% (pembulatan ke atas/terdekat) dan Grand Total
        pajak = subTotal.multiply(new BigDecimal("0.11")).setScale(0, java.math.RoundingMode.HALF_UP);
        grandTotal = subTotal.add(pajak);

        // Update label ringkasan di panel kiri bawah
        TextResultSubTotal.setText(formatUang(subTotal));
        TextResultPajak.setText(formatUang(pajak));
        jLabel40.setText(formatUang(grandTotal));

        // Update tampilan angka besar di kanan atas (tanpa prefix "Rp. " karena sudah ada di jLabel19)
        this.nf.setMaximumFractionDigits(0);
        TextGrandTotal.setText(this.nf.format(grandTotal));
        if (this.layarPembeli != null) {
         this.layarPembeli.updateKeranjang(
                 model,
                 formatUang(subTotal),
                 formatUang(pajak),
                 formatUang(grandTotal)
         );
     }
    }

    /**
     * Membersihkan keranjang belanja, input member/voucher, dan nominal bayar
     * setelah transaksi selesai disimpan.
     */
    private void resetTransaksi() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) Tabel.getModel();
        model.setRowCount(0);
        this.JumlahDibayar = BigDecimal.ZERO;
        this.renderUang();
        this.InputTextIdMember.setText("");
        this.InputTextVoucerKode.setText("");
        this.hitungTotalBelanja();
        
        Layar2 targetLayar2 = dapatkanLayarPembeli(false);
        if (targetLayar2 != null && targetLayar2.isDisplayable()){
            targetLayar2.resetLayar();
        }
    }

    /**
     * Memvalidasi metode pembayaran yang dipilih kasir (Cash atau QRIS)
     * saat tombol "Bayar Sekarang" atau tombol Enter ditekan.
     */
    private void prosesBayarSekarang() {
        if (grandTotal.compareTo(BigDecimal.ZERO) <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (this.isTunai) {
            // Alur Pembayaran Tunai (Cash)
            if (JumlahDibayar.compareTo(grandTotal) < 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Uang yang dibayarkan kurang dari Grand Total!", "Pembayaran Gagal", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            BigDecimal kembalian = JumlahDibayar.subtract(grandTotal);
            selesaikanTransaksi("CASH", JumlahDibayar, kembalian);
        } else {
            // Alur Pembayaran Non-Tunai (Simulasi QRIS)
            tampilkanDialogQris();
        }
    }

    /**
     * Menampilkan popup simulasi QRIS dengan kode unik, countdown timer 15 menit,
     * serta tombol simulasi status pembayaran (Berhasil / Gagal).
     */
    
    private void tampilkanDialogQris() {
        javax.swing.JDialog dialogQris = new javax.swing.JDialog(this, "Pembayaran QRIS", true);
        dialogQris.setSize(380, 450);
        dialogQris.setLocationRelativeTo(this);
        dialogQris.setLayout(new java.awt.BorderLayout(10, 10));

        String kodeQrisUnik = "QRIS-" + System.currentTimeMillis();
        javax.swing.JLabel lblHeader = new javax.swing.JLabel(
            "<html><center><h3>Scan QRIS untuk Membayar</h3>" +
            "Total Tagihan: <b>" + formatUang(grandTotal) + "</b><br>" +
            "ID Transaksi: " + kodeQrisUnik + "</center></html>",
            javax.swing.SwingConstants.CENTER
        );

        // Placeholder visual kode QR unik
        javax.swing.JLabel lblQrBox = new javax.swing.JLabel(
            "<html><center><b>[ KODE QR UNIK ]</b><br><br>" + kodeQrisUnik + "</center></html>",
            javax.swing.SwingConstants.CENTER
        );
        lblQrBox.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK, 2));

        // Timer mundur 15 menit (900 detik)
        javax.swing.JLabel lblTimer = new javax.swing.JLabel("Batas Waktu: 15:00", javax.swing.SwingConstants.CENTER);
        lblTimer.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 16));
        lblTimer.setForeground(Color.RED);

        final int[] sisaDetik = {15 * 60};
        javax.swing.Timer countdown = new javax.swing.Timer(1000, e -> {
            sisaDetik[0]--;
            int menit = sisaDetik[0] / 60;
            int detik = sisaDetik[0] % 60;
            lblTimer.setText(String.format("Batas Waktu: %02d:%02d", menit, detik));
            if (sisaDetik[0] <= 0) {
                ((javax.swing.Timer) e.getSource()).stop();
                javax.swing.JOptionPane.showMessageDialog(dialogQris, "Waktu pembayaran QRIS habis (Expired)!", "Timeout", javax.swing.JOptionPane.ERROR_MESSAGE);
                dialogQris.dispose();
            }
        });
        countdown.start();

        // Tombol simulasi untuk demo kasir
        javax.swing.JPanel panelTombol = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        javax.swing.JButton btnBerhasil = new javax.swing.JButton("Simulasi Berhasil");
        javax.swing.JButton btnGagal = new javax.swing.JButton("Simulasi Gagal");

        btnBerhasil.addActionListener(e -> {
            countdown.stop();
            dialogQris.dispose();
            selesaikanTransaksi("QRIS", grandTotal, BigDecimal.ZERO);
        });

        btnGagal.addActionListener(e -> {
            countdown.stop();
            dialogQris.dispose();
            javax.swing.JOptionPane.showMessageDialog(this, "Pembayaran QRIS gagal atau dibatalkan.", "QRIS Gagal", javax.swing.JOptionPane.ERROR_MESSAGE);
        });

        panelTombol.add(btnBerhasil);
        panelTombol.add(btnGagal);

        javax.swing.JPanel panelTengah = new javax.swing.JPanel(new java.awt.BorderLayout(5, 5));
        panelTengah.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelTengah.add(lblQrBox, java.awt.BorderLayout.CENTER);
        panelTengah.add(lblTimer, java.awt.BorderLayout.SOUTH);

        dialogQris.add(lblHeader, java.awt.BorderLayout.NORTH);
        dialogQris.add(panelTengah, java.awt.BorderLayout.CENTER);
        dialogQris.add(panelTombol, java.awt.BorderLayout.SOUTH);
        dialogQris.setVisible(true);
    }

    /**
     * Menyelesaikan transaksi setelah pembayaran valid:
     * 1. Simpan data ke tabel 'transactions' & 'transaction_items' (Modul C)
     * 2. Kurangi stok produk di database (Integrasi Modul A)
     * 3. Tandai voucher sebagai terpakai jika menggunakan voucher (Integrasi Modul B)
     */
    private void selesaikanTransaksi(String metode, BigDecimal bayar, BigDecimal kembalian) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) Tabel.getModel();
        if (model.getRowCount() == 0) return;

        try {
            // Ambil ID member jika kasir mengetikkan ID angka di kotak Id Member
            String teksMember = this.InputTextIdMember.getText().trim().replaceAll("[^0-9]", "");
            if (!teksMember.isEmpty() && this.activeCustomerId == null) {
                this.activeCustomerId = Integer.parseInt(teksMember);
            }

            Model.interfaces.transactionsInterface trx = new Model.interfaces.transactionsInterface(
                    this.activeCustomerId,
                    this.activeVoucherId,
                    this.subTotal,
                    this.activeDiskonVoucher,
                    new BigDecimal("11.00"),
                    this.pajak,
                    this.grandTotal,
                    bayar,
                    kembalian,
                    "selesai"
            );
            trx.kode = "TRX-" + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            long trxId = new Model.transactions().add(trx);
            if (trxId <= 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi ke database!", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            config.Database db = new config.Database();
            java.sql.Statement stm = db.start();
            java.sql.Connection conn = stm.getConnection();

            Model.transaction_items itemModel = new Model.transaction_items();
            for (int i = 0; i < model.getRowCount(); i++) {
                String kodeBarang = model.getValueAt(i, 0).toString();
                String namaBarang = model.getValueAt(i, 1).toString();
                int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
                String satuan = model.getValueAt(i, 3).toString();

                // Kolom indeks 6 = "Netto" (harga satuan setelah diskon item)
                String cleanHarga = model.getValueAt(i, 6).toString().replaceAll("[^0-9]", "");
                BigDecimal hargaSatuan = cleanHarga.isEmpty() ? BigDecimal.ZERO : new BigDecimal(cleanHarga);

                int productId = 1;
                try (java.sql.PreparedStatement psCari = conn.prepareStatement(
                        "SELECT id FROM products WHERE nomor = ? LIMIT 1")) {
                    psCari.setString(1, kodeBarang);
                    try (java.sql.ResultSet rs = psCari.executeQuery()) {
                        if (rs.next()) productId = rs.getInt("id");
                    }
                }

                Model.interfaces.transaction_itemInterface item =
                        new Model.interfaces.transaction_itemInterface(
                                trxId, productId, kodeBarang, namaBarang, qty, satuan, hargaSatuan
                        );
                itemModel.add(item);

                // Kurangi stok produk di database
                try (java.sql.PreparedStatement psStok = conn.prepareStatement(
                        "UPDATE products SET stok = stok - ? WHERE id = ?")) {
                    psStok.setInt(1, qty);
                    psStok.setInt(2, productId);
                    psStok.executeUpdate();
                }
            }

            // Tandai voucher sebagai 'dipakai' jika ada voucher aktif
            if (this.activeVoucherId != null) {
                try (java.sql.PreparedStatement psVoucher = conn.prepareStatement(
                        "UPDATE vouchers SET status = 'dipakai' WHERE id = ?")) {
                    psVoucher.setInt(1, this.activeVoucherId);
                    psVoucher.executeUpdate();
                }
            }
            db.close();

            // Perbarui status pembayaran di Layar 2 (Customer Display)
            if (this.layarPembeli != null) {
                this.layarPembeli.updatePembayaran(
                        metode,
                        formatUang(bayar),
                        formatUang(kembalian),
                        "Pembayaran Berhasil! Terima Kasih."
                );
            }

            String pesan = "Transaksi Berhasil Disimpan!\n"
                    + "No. Nota: " + trx.kode + "\n"
                    + "Metode: " + metode + "\n"
                    + "Grand Total: " + formatUang(grandTotal) + "\n"
                    + "Dibayar: " + formatUang(bayar) + "\n"
                    + "Kembalian: " + formatUang(kembalian);

            javax.swing.JOptionPane.showMessageDialog(this, pesan, "Sukses", javax.swing.JOptionPane.INFORMATION_MESSAGE);

            this.activeCustomerId = null;
            this.activeVoucherId = null;
            this.activeDiskonVoucher = BigDecimal.ZERO;
            resetTransaksi();
            if (this.layarPembeli != null) {
                this.layarPembeli.resetLayar();
            }

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error menyimpan transaksi: " + ex.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Layar2 dapatkanLayarPembeli(boolean tampilkanJikaBelum) {
        if (tampilkanJikaBelum && (layarPembeli == null || !layarPembeli.isDisplayable())) {
            layarPembeli = new Layar2();
        }
        if (layarPembeli != null && tampilkanJikaBelum) {
            if (!layarPembeli.isVisible()) {
                layarPembeli.setVisible(true);
            }
            javax.swing.table.DefaultTableModel model =
                    (javax.swing.table.DefaultTableModel) Tabel.getModel();
            layarPembeli.updateKeranjang(
                    model,
                    formatUang(subTotal),
                    formatUang(pajak),
                    formatUang(grandTotal)
            );
        }
        return layarPembeli;
    }
    
    
    private void bukaLayar2() {
        Layar2 target = dapatkanLayarPembeli(true);
        if (target != null) {
            target.toFront();
            target.requestFocus();
        }
    }
    
    private void tambahBarangKeTabel() {
        String kodeInput = this.kodeBarang.getText().trim();
        String qtyText = this.jumlahBarang.getText().trim().replaceAll("[^0-9]", "");

        if (kodeInput.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Masukkan kode barang terlebih dahulu!",
                    "Peringatan",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            this.kodeBarang.requestFocusInWindow();
            return;
        }

        int qty = qtyText.isEmpty() ? 0 : Integer.parseInt(qtyText);
        if (qty <= 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Jumlah barang harus lebih dari 0!",
                    "Peringatan",
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            this.jumlahBarang.requestFocusInWindow();
            return;
        }

        config.Database db = new config.Database();
        try {
            java.sql.Statement stm = db.start();
            java.sql.Connection conn = stm.getConnection();

            String sql = "SELECT nomor, nama, satuan, harga_jual, stok "
                       + "FROM products WHERE nomor = ? AND dihapus = 0 LIMIT 1";

            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, kodeInput);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        javax.swing.JOptionPane.showMessageDialog(
                                this,
                                "Barang dengan kode/nomor '" + kodeInput + "' tidak ditemukan!",
                                "Data Tidak Ditemukan",
                                javax.swing.JOptionPane.WARNING_MESSAGE
                        );
                        this.kodeBarang.requestFocusInWindow();
                        return;
                    }

                    int stokTersedia = rs.getInt("stok");
                    if (qty > stokTersedia) {
                        javax.swing.JOptionPane.showMessageDialog(
                                this,
                                "Stok tidak mencukupi! Stok tersedia: " + stokTersedia,
                                "Stok Kurang",
                                javax.swing.JOptionPane.WARNING_MESSAGE
                        );
                        this.jumlahBarang.requestFocusInWindow();
                        return;
                    }

                    String nomorProduk = rs.getString("nomor");
                    String namaProduk = rs.getString("nama");
                    String satuanProduk = rs.getString("satuan");
                    BigDecimal hargaJual = rs.getBigDecimal("harga_jual");
                    if (hargaJual == null) {
                        hargaJual = BigDecimal.ZERO;
                    }

                    // Tampilkan preview di field input sebelah kanan
                    this.NamaBarang.setText(namaProduk);
                    this.SatuanBarang.setText(satuanProduk);
                    this.hargaPersatuan.setText(formatUang(hargaJual));

                    BigDecimal diskonItem = BigDecimal.ZERO;
                    BigDecimal nettoItem = hargaJual.subtract(diskonItem);
                    if (nettoItem.compareTo(BigDecimal.ZERO) < 0) {
                        nettoItem = BigDecimal.ZERO;
                    }
                    BigDecimal totalBaris = nettoItem.multiply(BigDecimal.valueOf(qty));
                    this.jTextField9.setText(formatUang(totalBaris));

                    javax.swing.table.DefaultTableModel model =
                            (javax.swing.table.DefaultTableModel) this.Tabel.getModel();

                    // Kolom 0..7: Kode, Nama, Jumlah, Satuan, Harga, Diskon, Netto, Total
                    model.addRow(new Object[]{
                        nomorProduk,
                        namaProduk,
                        qty,
                        satuanProduk,
                        formatUang(hargaJual),
                        formatUang(diskonItem),
                        formatUang(nettoItem),
                        formatUang(totalBaris)
                    });

                    // Reset field input barang untuk item berikutnya
                    this.kodeBarang.setText("");
                    this.jumlahBarang.setText("0");
                    this.kodeBarang.requestFocusInWindow();
                }
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data barang: " + ex.getMessage(),
                    "Database Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (java.sql.SQLException ex) {
                logger.log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    /**
     * Creates new form Transaksi
     */
    public Transaksi() {
        initComponents();
        
        
//      Membungkus konten form dengan JScrollPane agar panel pembayaran di bagian
//      bawah dapat di-scroll pada layar resolusi standar, sekaligus memaksa lebar
//      form mengikuti lebar layar agar komponen di sisi kanan tidak terpotong.
        java.awt.Container content = this.getContentPane();
        javax.swing.JPanel wrapper = new javax.swing.JPanel(new java.awt.BorderLayout()) {
            @Override
            public java.awt.Dimension getPreferredSize() {
                java.awt.Dimension d = super.getPreferredSize();
                if (getParent() instanceof javax.swing.JViewport) {
                    d.width = getParent().getWidth();
                }
                return d;
            }
        };
        wrapper.add(content, java.awt.BorderLayout.CENTER);

        javax.swing.JScrollPane mainScroll = new javax.swing.JScrollPane(wrapper);
        mainScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        this.setContentPane(mainScroll);
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        this.TIME = new TimeNow();
        this.mulaiJam();
        this.setupShortcut();
        this.SetMetodePembayaran(this.isTunai);
        this.renderUang();
        
        
//      kosongkan baris dummy bawaan NeatBeans pada JTable saat aplikasi baru dibuka
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) Tabel.getModel();
        model.setRowCount(0);
        
//      Listener otomatis: setiap ada barang ditambah/diubah oleh Modul A atau diskon oleh Modul B,
//      fungsi hitungTotalBelanja() akan otomatis dijalankan.
        model.addTableModelListener(e -> hitungTotalBelanja());
        
        this.kodeBarang.addActionListener(e -> tambahBarangKeTabel());
        this.jumlahBarang.addActionListener(e -> tambahBarangKeTabel());
        
//      Menghubungkan tombol "Bayar Sekarang" ke proses pembayaran (Cash / QRIS)
        this.ButtonBayarSekarang.addActionListener(e -> prosesBayarSekarang());
        this.hitungTotalBelanja();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        InputTextIdMember = new javax.swing.JTextField();
        InputTextVoucerKode = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TextResultName = new javax.swing.JLabel();
        TextResultTanggalLahir = new javax.swing.JLabel();
        TextResultNomorHp = new javax.swing.JLabel();
        TextResultExpierd = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        TextGrandTotal = new javax.swing.JLabel();
        TextDateTimeNow = new javax.swing.JLabel();
        TextDayNow = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        kodeBarang = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jumlahBarang = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        NamaBarang = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        SatuanBarang = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        hargaPersatuan = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jTextField9 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        TextResultSubTotal = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        InputTextUangBayar = new javax.swing.JTextField();
        ButtonAdd50K = new javax.swing.JButton();
        ButtonBayarUangPas = new javax.swing.JButton();
        ButtonAdd10K = new javax.swing.JButton();
        ButtonAdd100K = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        TextResultPajak = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        ButtonBayarSekarang = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        ButtonSetCash = new javax.swing.JButton();
        ButtonSetQris = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        ButtonRiwayatTransaksi = new javax.swing.JButton();
        ButtonToProduk = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        ButtonBukaLayar2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        InputTextIdMember.setToolTipText("Id member");

        InputTextVoucerKode.setToolTipText("Id member");

        jLabel2.setText("Nama ");

        jLabel3.setText("Langgal Lahir ");

        jLabel4.setText("Nomor Hp");

        jLabel5.setText("Expierd");

        jLabel6.setText(":");

        jLabel7.setText(":");

        jLabel8.setText(":");

        jLabel9.setText(":");

        TextResultName.setText("-");

        TextResultTanggalLahir.setText("- ");

        TextResultNomorHp.setText("- ");

        TextResultExpierd.setText("-");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextResultName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextResultExpierd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextResultTanggalLahir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextResultNomorHp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6)
                    .addComponent(TextResultName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(TextResultTanggalLahir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8)
                    .addComponent(TextResultNomorHp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(TextResultExpierd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel34.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel34.setText("Data Member");

        jLabel35.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel35.setText("Id:");

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel1.setText("Voucher:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InputTextIdMember))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InputTextVoucerKode, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(InputTextIdMember, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(InputTextVoucerKode, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel18.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel18.setText("Total:");

        jLabel19.setFont(new java.awt.Font("Liberation Sans", 1, 48)); // NOI18N
        jLabel19.setText("Rp.");

        TextGrandTotal.setFont(new java.awt.Font("Liberation Sans", 1, 48)); // NOI18N
        TextGrandTotal.setText("0.000.000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(TextGrandTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TextGrandTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TextDateTimeNow.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        TextDateTimeNow.setText("27-12-2026 | 19:20:9");

        TextDayNow.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        TextDayNow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TextDayNow.setText("Senin");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TextDateTimeNow, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TextDayNow, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TextDateTimeNow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TextDayNow)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Cari customer / Voucher");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        kodeBarang.setText("Nomor...");

        jLabel21.setText("Kode barang (space untuk mencari)");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kodeBarang)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(kodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jumlahBarang.setText("0");
        jumlahBarang.addActionListener(this::jumlahBarangActionPerformed);

        jLabel22.setText("Jumlah");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jumlahBarang)
            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jumlahBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        NamaBarang.setEditable(false);
        NamaBarang.setText("Nama");
        NamaBarang.addActionListener(this::NamaBarangActionPerformed);

        jLabel23.setText("Nama Barang");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(NamaBarang)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 213, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(NamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        SatuanBarang.setEditable(false);
        SatuanBarang.setText("-");
        SatuanBarang.addActionListener(this::SatuanBarangActionPerformed);

        jLabel24.setText("Satuan");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(SatuanBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(SatuanBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        hargaPersatuan.setEditable(false);
        hargaPersatuan.setText("Rp.");
        hargaPersatuan.addActionListener(this::hargaPersatuanActionPerformed);

        jLabel25.setText("Harga / satuan");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
            .addComponent(hargaPersatuan)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(hargaPersatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTextField9.setEditable(false);
        jTextField9.setText("Rp.");
        jTextField9.addActionListener(this::jTextField9ActionPerformed);

        jLabel26.setText("Total harga");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 36, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        Tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kode", "Nama", "Jumlah", "Satuan", "Harga", "Diskon", "Netto", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel.setRowHeight(40);
        jScrollPane2.setViewportView(Tabel);
        if (Tabel.getColumnModel().getColumnCount() > 0) {
            Tabel.getColumnModel().getColumn(2).setMaxWidth(190);
            Tabel.getColumnModel().getColumn(3).setMaxWidth(190);
            Tabel.getColumnModel().getColumn(5).setMaxWidth(190);
        }

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel27.setText("Sub total");

        jLabel28.setText("Pajak (11%)");

        jLabel29.setText("Grand Total");

        jLabel30.setText(":");

        jLabel32.setText(":");

        TextResultSubTotal.setText("Rp. -");

        jLabel36.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel36.setText("BAYAR");

        InputTextUangBayar.setFont(new java.awt.Font("Liberation Sans", 0, 36)); // NOI18N
        InputTextUangBayar.setText("Rp.");
        InputTextUangBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                InputTextUangBayarKeyReleased(evt);
            }
        });

        ButtonAdd50K.setText("Rp. 50.000");
        ButtonAdd50K.setToolTipText("");
        ButtonAdd50K.addActionListener(this::ButtonAdd50KActionPerformed);

        ButtonBayarUangPas.setBackground(new java.awt.Color(204, 255, 204));
        ButtonBayarUangPas.setText("Uang Pas");
        ButtonBayarUangPas.addActionListener(this::ButtonBayarUangPasActionPerformed);

        ButtonAdd10K.setText("Rp. 10.000");
        ButtonAdd10K.addActionListener(this::ButtonAdd10KActionPerformed);

        ButtonAdd100K.setText("Rp. 100.000");
        ButtonAdd100K.addActionListener(this::ButtonAdd100KActionPerformed);

        jLabel37.setText(":");

        jLabel38.setText(":");

        TextResultPajak.setText("Rp. -");

        jLabel40.setText("Rp. -");

        jLabel41.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel41.setText("Di bayar:");

        ButtonBayarSekarang.setText("Bayar Sekarang");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(ButtonBayarUangPas, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonAdd10K, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonAdd50K, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonAdd100K, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addGap(102, 102, 102)
                                .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 4, Short.MAX_VALUE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TextResultPajak, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TextResultSubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ButtonBayarSekarang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(InputTextUangBayar, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel30)
                    .addComponent(TextResultSubTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel32)
                    .addComponent(jLabel37)
                    .addComponent(TextResultPajak))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel38)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonAdd50K, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(ButtonBayarUangPas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonAdd10K, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonAdd100K, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addGap(5, 5, 5)
                .addComponent(InputTextUangBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonBayarSekarang, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        jLabel31.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Metode Pembayaran");

        ButtonSetCash.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        ButtonSetCash.setText("Cash");
        ButtonSetCash.addActionListener(this::ButtonSetCashActionPerformed);

        ButtonSetQris.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        ButtonSetQris.setText("Qris");
        ButtonSetQris.addActionListener(this::ButtonSetQrisActionPerformed);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addComponent(ButtonSetCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonSetQris, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addGap(29, 29, 29)
                .addComponent(ButtonSetCash, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(ButtonSetQris, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ButtonRiwayatTransaksi.setText("Riwayat Transaksi (F1)");
        ButtonRiwayatTransaksi.addActionListener(this::ButtonRiwayatTransaksiActionPerformed);

        ButtonToProduk.setText("Produk (F2)");
        ButtonToProduk.addActionListener(this::ButtonToProdukActionPerformed);

        jButton10.setText("Member (F3)");

        ButtonBukaLayar2.setText("Buka Layar 2 (F5)");
        ButtonBukaLayar2.addActionListener(this::ButtonBukaLayar2ActionPerformed);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ButtonRiwayatTransaksi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonToProduk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonBukaLayar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ButtonRiwayatTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonToProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonBukaLayar2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(146, 146, 146)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jumlahBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jumlahBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jumlahBarangActionPerformed

    private void NamaBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NamaBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NamaBarangActionPerformed

    private void SatuanBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SatuanBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SatuanBarangActionPerformed

    private void hargaPersatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hargaPersatuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hargaPersatuanActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void ButtonBayarUangPasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonBayarUangPasActionPerformed
//  Mengisi otomatis nominal bayar sesuai Grand Total saat tombol "Uang Pas" diklik
        this.JumlahDibayar = this.grandTotal;
        this.renderUang();
    }//GEN-LAST:event_ButtonBayarUangPasActionPerformed

    private void ButtonAdd10KActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAdd10KActionPerformed
        this.JumlahDibayar = this.JumlahDibayar.add(new BigDecimal(10000));
        this.renderUang();
    }//GEN-LAST:event_ButtonAdd10KActionPerformed

    private void ButtonAdd50KActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAdd50KActionPerformed
        this.JumlahDibayar = this.JumlahDibayar.add(new BigDecimal(50000));
        this.renderUang();
    }//GEN-LAST:event_ButtonAdd50KActionPerformed

    private void ButtonAdd100KActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAdd100KActionPerformed
        this.JumlahDibayar = this.JumlahDibayar.add(new BigDecimal(100000));
        this.renderUang();
    }//GEN-LAST:event_ButtonAdd100KActionPerformed

    private void ButtonSetCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSetCashActionPerformed
        this.SetMetodePembayaran(true);
    }//GEN-LAST:event_ButtonSetCashActionPerformed

    private void ButtonSetQrisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSetQrisActionPerformed
        this.SetMetodePembayaran(false);
    }//GEN-LAST:event_ButtonSetQrisActionPerformed

    private void InputTextUangBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InputTextUangBayarKeyReleased
        String clean = this.InputTextUangBayar.getText().replaceAll("[^0-9]", "");
        if (clean.isEmpty()) {
            this.JumlahDibayar = BigDecimal.ZERO;
        } else {
            this.JumlahDibayar = new BigDecimal(clean);
        }

        this.renderUang();
    }//GEN-LAST:event_InputTextUangBayarKeyReleased

    private void ButtonToProdukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonToProdukActionPerformed
        this.toProduk();
    }//GEN-LAST:event_ButtonToProdukActionPerformed

    private void ButtonRiwayatTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonRiwayatTransaksiActionPerformed
        new RiwayatTransaksi().setVisible(true);
    }//GEN-LAST:event_ButtonRiwayatTransaksiActionPerformed

    private void ButtonBukaLayar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonBukaLayar2ActionPerformed
        this.bukaLayar2();
    }//GEN-LAST:event_ButtonBukaLayar2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Transaksi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonAdd100K;
    private javax.swing.JButton ButtonAdd10K;
    private javax.swing.JButton ButtonAdd50K;
    private javax.swing.JButton ButtonBayarSekarang;
    private javax.swing.JButton ButtonBayarUangPas;
    private javax.swing.JButton ButtonBukaLayar2;
    private javax.swing.JButton ButtonRiwayatTransaksi;
    private javax.swing.JButton ButtonSetCash;
    private javax.swing.JButton ButtonSetQris;
    private javax.swing.JButton ButtonToProduk;
    private javax.swing.JTextField InputTextIdMember;
    private javax.swing.JTextField InputTextUangBayar;
    private javax.swing.JTextField InputTextVoucerKode;
    private javax.swing.JTextField NamaBarang;
    private javax.swing.JTextField SatuanBarang;
    private javax.swing.JTable Tabel;
    private javax.swing.JLabel TextDateTimeNow;
    private javax.swing.JLabel TextDayNow;
    private javax.swing.JLabel TextGrandTotal;
    private javax.swing.JLabel TextResultExpierd;
    private javax.swing.JLabel TextResultName;
    private javax.swing.JLabel TextResultNomorHp;
    private javax.swing.JLabel TextResultPajak;
    private javax.swing.JLabel TextResultSubTotal;
    private javax.swing.JLabel TextResultTanggalLahir;
    private javax.swing.JTextField hargaPersatuan;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jumlahBarang;
    private javax.swing.JTextField kodeBarang;
    // End of variables declaration//GEN-END:variables
}
