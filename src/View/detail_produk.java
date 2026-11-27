/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import utils.TimeNow;
import Controller.DetailProdukController;
import javax.swing.table.DefaultTableModel;
import Model.interfaces.product_batchInterface;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import Model.interfaces.productInterface;
import java.util.List;
import utils.Text;
import Controller.ProdukController;
import Model.interfaces.categoriesInterface;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author vallen
 */
public class detail_produk extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(detail_produk.class.getName());
    private TimeNow TIME = new TimeNow();
    private DefaultTableModel model;
    private DetailProdukController detControler = new DetailProdukController();
    private ProdukController produk = new ProdukController();
    private String nomorProduk;
    private String nomorProdukTemp;
    private productInterface getproduk = null;
    private List<product_batchInterface> batch = null;
    private List<categoriesInterface> categoris = new ArrayList<>();
    private Text tx = new Text();

    private void renderTabel() {
        this.model.setRowCount(0);
        try {
            this.batch = this.detControler.getBatches(this.getproduk.id);
            for (product_batchInterface items : this.batch) {
                int lamaPerjalanan = (int) ChronoUnit.DAYS.between(items.tanggal_pemesanan, items.tanggal_masuk);
                this.model.addRow(
                        new Object[]{items.tanggal_masuk.toString(), items.tanggal_pemesanan.toString(),
                            String.valueOf(lamaPerjalanan), items.alamatSupplier,
                            items.supplierName, items.harga_beli, items.qty, items.tanggal_kadaluarsa.toString()
                        }
                );
            }
        } catch (SQLException ex) {
            System.getLogger(detail_produk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void updateBarang() {

        int pilihan = JOptionPane.showConfirmDialog(
                this, // parent component
                "Yakin ingin mengubah data ini?", // pesan
                "Konfirmasi", // judul
                JOptionPane.YES_NO_OPTION, // tombol yang tampil
                JOptionPane.QUESTION_MESSAGE // ikon
        );

        if (pilihan == JOptionPane.YES_OPTION) {
            int selectedCtegories = this.ComboBoxCategori.getSelectedIndex();
            int categoriesId = this.categoris.get(selectedCtegories).id;
            productInterface prodUpdate = new productInterface(
                    this.InputTextNomorBarang.getText(),
                    this.InputTextNama.getText(),
                    categoriesId,
                    tx.parseUangDouble(this.InputTextHargaJual.getText())
            );
            try {
                
                System.out.println("Berhasil updateed ROW -> "+ this.detControler.updateProduk(this.nomorProdukTemp, prodUpdate));
            } catch (SQLException ex) {
                System.getLogger(detail_produk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        } else {
            System.out.println("Dibatalkan");
        }

    }

    private void hapusBarang() {
        try {
            int pilihan = JOptionPane.showConfirmDialog(
                    this, // parent component
                    "Yakin ingin menghapus data ini?", // pesan
                    "Konfirmasi", // judul
                    JOptionPane.YES_NO_OPTION, // tombol yang tampil
                    JOptionPane.QUESTION_MESSAGE // ikon
            );

            if (pilihan == JOptionPane.YES_OPTION) {
                this.produk.HapusProduk(this.nomorProduk);
                System.out.println("Lanjut hapus");
            } else {
                System.out.println("Dibatalkan");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this, // parent
                    ex.getMessage(), // pesan
                    "Informasi", // judul
                    JOptionPane.INFORMATION_MESSAGE // ikon
            );
        }
    }

    private void renderCategory() {
        this.ComboBoxCategori.removeAllItems();
        try {
            this.categoris = this.produk.getCategories();
            for (categoriesInterface items : this.categoris) {
                this.ComboBoxCategori.addItem(items.name);
            }
        } catch (SQLException ex) {
            System.getLogger(produk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        this.ComboBoxCategori.addItem("Tambah baru");
    }

    private void initComponentsText() {
        this.InputTextNomorBarang.setText(this.nomorProduk);
        this.InputTextNama.setText(this.getproduk.nama);
        this.InputTextHargaJual.setText(this.tx.formatUang(this.getproduk.harga_jual));
        Long potensiPemasukan = Math.round(this.getproduk.harga_jual - this.getproduk.harga_beli);
        this.ChangeTextPotensiPemasukan.setText(this.tx.formatUang(potensiPemasukan));
        this.ChangeTextStokSekarang.setText(this.getproduk.stok.toString());
        this.renderCategory();
        this.ComboBoxCategori.setSelectedItem(this.getproduk.String_categories);
    }

    /**
     * Creates new form produk
     */
    public detail_produk(String nomorProduk) {
        initComponents();
        TIME.startTime(this.TextDateTimeNow, this.TextDayNow);
        this.nomorProduk = nomorProduk;
        this.model = (DefaultTableModel) this.TabelBatches.getModel();
        try {
            this.getproduk = this.detControler.getProdukByNumber(this.nomorProduk);
        } catch (SQLException ex) {
            System.getLogger(detail_produk.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        this.setupShortcut();
        this.renderTabel();
        this.initComponentsText();
        this.nomorProdukTemp = nomorProduk;
    }

    public detail_produk() {
        initComponents();
        TIME.startTime(this.TextDateTimeNow, this.TextDayNow);
        this.model = (DefaultTableModel) this.TabelBatches.getModel();
        this.setupShortcut();
    }

    private void toproduk() {
        produk next = new produk();
        next.setLocation(this.getLocation());
        next.setSize(this.getSize());
        next.setVisible(true);
        this.dispose();
    }

    private void setupShortcut() {
        javax.swing.KeyStroke esc = javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_ESCAPE, 0);

        this.getRootPane().registerKeyboardAction(
                e -> toproduk(),
                esc,
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TextDateTimeNow = new javax.swing.JLabel();
        TextDayNow = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        InputTextNomorBarang = new javax.swing.JTextField();
        InputTextNama = new javax.swing.JTextField();
        InputTextHargaJual = new javax.swing.JTextField();
        ChangeTextPotensiPemasukan = new javax.swing.JLabel();
        ChangeTextStokSekarang = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        ComboBoxCategori = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelBatches = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        ButtonHapusBarang = new javax.swing.JToggleButton();
        ButtonToTransaksi = new javax.swing.JToggleButton();
        jToggleButton1 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel1.setText("Manajemen Barang");

        TextDateTimeNow.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        TextDateTimeNow.setText("12 - 12- 2026 | 00:00:00");

        TextDayNow.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        TextDayNow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TextDayNow.setText("Senin");

        jLabel2.setText("Nomor (EAN18):");

        jLabel3.setText("Nama:");

        jLabel16.setText("Harga Jual");

        jLabel18.setText("Potensi pemasukan:");

        jLabel19.setText("Stok sekarang:");

        InputTextNomorBarang.setText("-");
        InputTextNomorBarang.addActionListener(this::InputTextNomorBarangActionPerformed);

        InputTextNama.setText("-");

        InputTextHargaJual.setText("-");
        InputTextHargaJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                InputTextHargaJualKeyReleased(evt);
            }
        });

        ChangeTextPotensiPemasukan.setText("Rp -");

        ChangeTextStokSekarang.setText("00");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(InputTextNomorBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(InputTextNama)
                            .addComponent(InputTextHargaJual))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(ChangeTextPotensiPemasukan, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .addComponent(ChangeTextStokSekarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(InputTextNomorBarang)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(InputTextNama))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(InputTextHargaJual))
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(ChangeTextPotensiPemasukan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(ChangeTextStokSekarang)))
        );

        jLabel23.setText("Supplier Terakhir:");

        jLabel24.setText("Tanggal Masuk:");

        jLabel25.setText("Nama:");

        jLabel26.setText("Harga: ");

        jLabel27.setText("Qty:");

        jLabel29.setText("Rp -");

        jLabel30.setText("00");

        jLabel28.setText("20 - 12 - 2026");

        jLabel31.setText("-");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel30))
                .addGap(22, 22, 22))
        );

        jLabel4.setText("Kategori");

        ComboBoxCategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(ComboBoxCategori, 0, 117, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBoxCategori, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 272, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TextDateTimeNow))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextDayNow, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TextDateTimeNow))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TextDayNow)
                        .addContainerGap())
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        TabelBatches.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Tanggal Masuk", "Tanggal Pembelian", "Lama Perjalanan", "Alamat Supplier", "Nama", "Harga Beli", "QTY", "Kadaluarsa"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TabelBatches);
        if (TabelBatches.getColumnModel().getColumnCount() > 0) {
            TabelBatches.getColumnModel().getColumn(6).setMaxWidth(50);
        }

        ButtonHapusBarang.setText("Hapus Barang");
        ButtonHapusBarang.addActionListener(this::ButtonHapusBarangActionPerformed);

        ButtonToTransaksi.setText("Kembali (esc)");
        ButtonToTransaksi.addActionListener(this::ButtonToTransaksiActionPerformed);

        jToggleButton1.setText("Simpan Barang");
        jToggleButton1.addActionListener(this::jToggleButton1ActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(ButtonToTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonHapusBarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonToTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonHapusBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addGap(36, 36, 36)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonHapusBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonHapusBarangActionPerformed
        this.hapusBarang();
    }//GEN-LAST:event_ButtonHapusBarangActionPerformed

    private void ButtonToTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonToTransaksiActionPerformed
        this.toproduk();
    }//GEN-LAST:event_ButtonToTransaksiActionPerformed

    private void InputTextNomorBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputTextNomorBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InputTextNomorBarangActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        this.updateBarang();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void InputTextHargaJualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InputTextHargaJualKeyReleased
        Double textBefore = this.tx.parseUangDouble(this.InputTextHargaJual.getText());
        this.InputTextHargaJual.setText(tx.formatUang(textBefore));
    }//GEN-LAST:event_InputTextHargaJualKeyReleased

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
        java.awt.EventQueue.invokeLater(() -> new detail_produk().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton ButtonHapusBarang;
    private javax.swing.JToggleButton ButtonToTransaksi;
    private javax.swing.JLabel ChangeTextPotensiPemasukan;
    private javax.swing.JLabel ChangeTextStokSekarang;
    private javax.swing.JComboBox<String> ComboBoxCategori;
    private javax.swing.JTextField InputTextHargaJual;
    private javax.swing.JTextField InputTextNama;
    private javax.swing.JTextField InputTextNomorBarang;
    private javax.swing.JTable TabelBatches;
    private javax.swing.JLabel TextDateTimeNow;
    private javax.swing.JLabel TextDayNow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
