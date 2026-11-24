/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.ReturController;
import Controller.ReturController.HasilRetur;
import Controller.ReturController.ItemRetur;
import Model.interfaces.transaction_itemInterface;
import Model.interfaces.transactionsInterface;
import Model.transaction_items;
import Model.transactions;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ricor
 */

public class RiwayatTransaksi extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RiwayatTransaksi.class.getName());

    /**
     * Creates new form RiwayatTransaksi
     */
    public RiwayatTransaksi() {
        initComponents();
        
        this.setLocationRelativeTo(null);
        muatDataTransaksi(null, null, null);

        // Event klik baris pada TabelTransaksi -> muat detail barang ke TabelDetailItem
        TabelTransaksi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                muatDetailTransaksiTerpilih();
            }
        });

        ButtonCari.addActionListener(e -> jalankanFilter());
        ButtonReset.addActionListener(e -> {
            InputCari.setText("");
            InputTanggal.setText("");
            ComboStatus.setSelectedIndex(0);
            muatDataTransaksi(null, null, null);
        });
        ButtonCetakStruk.addActionListener(e -> cetakUlangStruk());
        ButtonEksporCsv.addActionListener(e -> eksporCsv());
        ButtonRetur.addActionListener(e -> bukaDialogRetur());
        ButtonKembali.addActionListener(e -> this.toTransaksi());
    }
    
    private void toTransaksi(){
        Transaksi next = new Transaksi();
        next.setSize(this.getSize());
        next.setLocation(this.getLocation());
        next.setVisible(true);
        this.dispose();
    }
    
    private List<transactionsInterface> daftarAktif = new ArrayList<>();
    private List<transaction_itemInterface> detailAktif = new ArrayList<>();

    private String formatRp(BigDecimal nilai) {
        if (nilai == null) return "Rp. 0";
        DecimalFormatSymbols sym = new DecimalFormatSymbols();
        sym.setGroupingSeparator('.');
        return "Rp. " + new DecimalFormat("#,##0", sym).format(nilai);
    }

    private void muatDataTransaksi(String status, LocalDate tanggal, Integer customerId) {
        DefaultTableModel model = (DefaultTableModel) TabelTransaksi.getModel();
        model.setRowCount(0);
        ((DefaultTableModel) TabelDetailItem.getModel()).setRowCount(0);

        transactions trxModel = new transactions();
        daftarAktif = trxModel.daftar(status, tanggal, tanggal, customerId, 200, 0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (transactionsInterface t : daftarAktif) {
            String noNota = (t.kode != null && !t.kode.isBlank()) ? t.kode : "TRX-" + t.id;
            String waktuStr = (t.waktu != null) ? t.waktu.format(fmt) : "-";
            String memberStr = (t.customer_id != null) ? "Member #" + t.customer_id : "Umum";
            String metodeStr = (t.kembalian != null && t.kembalian.compareTo(BigDecimal.ZERO) == 0
                    && t.bayar != null && t.bayar.compareTo(t.grand_total) == 0) ? "Cash / QRIS" : "Cash";

            model.addRow(new Object[]{
                noNota, waktuStr, memberStr, metodeStr, formatRp(t.grand_total), t.status
            });
        }
    }

    private void jalankanFilter() {
        String statusDipilih = ComboStatus.getSelectedItem().toString();
        String statusFilter = "Semua".equalsIgnoreCase(statusDipilih) ? null : statusDipilih.toLowerCase();

        LocalDate tglFilter = null;
        String tglText = InputTanggal.getText().trim();
        if (!tglText.isEmpty()) {
            try {
                if (tglText.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    tglFilter = LocalDate.parse(tglText, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } else {
                    tglFilter = LocalDate.parse(tglText);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal gunakan YYYY-MM-DD atau DD-MM-YYYY.");
                return;
            }
        }

        Integer custFilter = null;
        String cariText = InputCari.getText().trim().replaceAll("[^0-9]", "");
        if (!cariText.isEmpty()) {
            custFilter = Integer.parseInt(cariText);
        }

        muatDataTransaksi(statusFilter, tglFilter, custFilter);
    }

    private void muatDetailTransaksiTerpilih() {
        int row = TabelTransaksi.getSelectedRow();
        DefaultTableModel modelDetail = (DefaultTableModel) TabelDetailItem.getModel();
        modelDetail.setRowCount(0);
        detailAktif.clear();

        if (row < 0 || row >= daftarAktif.size()) return;

        transactionsInterface trx = daftarAktif.get(row);
        detailAktif = new transaction_items().byTransaksi(trx.id);

        for (transaction_itemInterface item : detailAktif) {
            modelDetail.addRow(new Object[]{
                item.kode, item.nama, item.jumlah, formatRp(item.harga), formatRp(item.total)
            });
        }
    }

    private void cetakUlangStruk() {
        int row = TabelTransaksi.getSelectedRow();
        if (row < 0 || row >= daftarAktif.size()) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu transaksi di tabel atas terlebih dahulu.");
            return;
        }
        transactionsInterface trx = daftarAktif.get(row);
        String noNota = (trx.kode != null && !trx.kode.isBlank()) ? trx.kode : "TRX-" + trx.id;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          SALINAN STRUK BELANJA         \n");
        sb.append("========================================\n");
        sb.append("No. Nota : ").append(noNota).append("\n");
        sb.append("Waktu    : ").append(trx.waktu != null
                ? trx.waktu.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) : "-").append("\n");
        sb.append("Status   : ").append(trx.status.toUpperCase()).append("\n");
        sb.append("----------------------------------------\n");
        for (transaction_itemInterface item : detailAktif) {
            sb.append(item.nama).append("\n");
            sb.append("  ").append(item.jumlah).append(" ").append(item.satuan)
              .append(" x ").append(formatRp(item.harga))
              .append(" = ").append(formatRp(item.total)).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append("Sub Total   : ").append(formatRp(trx.sub_total)).append("\n");
        sb.append("Diskon      : ").append(formatRp(trx.diskon_voucher)).append("\n");
        sb.append("Pajak (11%) : ").append(formatRp(trx.pajak)).append("\n");
        sb.append("Grand Total : ").append(formatRp(trx.grand_total)).append("\n");
        sb.append("Bayar       : ").append(formatRp(trx.bayar)).append("\n");
        sb.append("Kembalian   : ").append(formatRp(trx.kembalian)).append("\n");
        sb.append("========================================\n");

        JTextArea area = new JTextArea(sb.toString(), 20, 42);
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Cetak Ulang Struk - " + noNota, JOptionPane.INFORMATION_MESSAGE);
    }

    private void eksporCsv() {
    if (daftarAktif.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tidak ada data transaksi untuk diekspor.");
        return;
    }
    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new java.io.File("riwayat_transaksi.csv"));
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        DateTimeFormatter fmtWaktu = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (PrintWriter pw = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            pw.println("sep=,");
            pw.println("ID,No_Nota,Waktu,Customer_ID,Sub_Total,Diskon,Pajak,Grand_Total,Bayar,Kembalian,Status");
            for (transactionsInterface t : daftarAktif) {
                String waktuStr = (t.waktu != null) ? t.waktu.format(fmtWaktu) : "";
                pw.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        t.id,
                        escapeCsv(t.kode != null ? t.kode : "TRX-" + t.id),
                        escapeCsv(waktuStr),
                        escapeCsv(t.customer_id != null ? String.valueOf(t.customer_id) : "Umum"),
                        t.sub_total.toPlainString(),
                        t.diskon_voucher.toPlainString(),
                        t.pajak.toPlainString(),
                        t.grand_total.toPlainString(),
                        t.bayar.toPlainString(),
                        t.kembalian.toPlainString(),
                        escapeCsv(t.status));
            }
            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengekspor CSV: " + ex.getMessage());
        }
    }
}

private String escapeCsv(String value) {
    if (value == null) return "";
    if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
}

    private void bukaDialogRetur() {
        int row = TabelTransaksi.getSelectedRow();
        if (row < 0 || row >= daftarAktif.size()) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang ingin diretur terlebih dahulu.");
            return;
        }
        transactionsInterface trx = daftarAktif.get(row);
        if (detailAktif.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Detail item transaksi kosong.");
            return;
        }

        String[] kolom = {"Pilih", "ID Item", "Nama Barang", "Qty Beli", "Qty Retur", "Layak Jual"};
        DefaultTableModel modelRetur = new DefaultTableModel(kolom, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0 || col == 5) return Boolean.class;
                if (col == 3 || col == 4) return Integer.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int r, int col) {
                return col == 0 || col == 4 || col == 5;
            }
        };

        for (transaction_itemInterface item : detailAktif) {
            modelRetur.addRow(new Object[]{false, item.id, item.nama, item.jumlah, 1, true});
        }

        JTable tabelPilihan = new JTable(modelRetur);
        JTextField inputAlasan = new JTextField();

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(8, 8));
        panel.add(new JScrollPane(tabelPilihan), java.awt.BorderLayout.CENTER);

        javax.swing.JPanel panelBawah = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
        panelBawah.add(new javax.swing.JLabel("Alasan Retur (Wajib):"), java.awt.BorderLayout.NORTH);
        panelBawah.add(inputAlasan, java.awt.BorderLayout.CENTER);
        panel.add(panelBawah, java.awt.BorderLayout.SOUTH);
        panel.setPreferredSize(new java.awt.Dimension(560, 260));

        int konfirmasi = JOptionPane.showConfirmDialog(
                this, panel, "Proses Retur / Refund - Transaksi #" + trx.id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (konfirmasi == JOptionPane.OK_OPTION) {
            List<ItemRetur> daftarItemRetur = new ArrayList<>();
            for (int i = 0; i < modelRetur.getRowCount(); i++) {
                Boolean dipilih = (Boolean) modelRetur.getValueAt(i, 0);
                if (Boolean.TRUE.equals(dipilih)) {
                    long itemId = ((Number) modelRetur.getValueAt(i, 1)).longValue();
                    int qtyRetur = ((Number) modelRetur.getValueAt(i, 4)).intValue();
                    boolean layak = Boolean.TRUE.equals(modelRetur.getValueAt(i, 5));
                    daftarItemRetur.add(new ItemRetur(itemId, qtyRetur, layak));
                }
            }

            HasilRetur hasil = new ReturController().prosesRetur(trx.id, inputAlasan.getText(), daftarItemRetur);
            JOptionPane.showMessageDialog(this, hasil.pesan(),
                    hasil.berhasil() ? "Retur Berhasil" : "Retur Gagal",
                    hasil.berhasil() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (hasil.berhasil()) {
                muatDataTransaksi(null, null, null);
            }
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        InputCari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        InputTanggal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ComboStatus = new javax.swing.JComboBox<>();
        ButtonCari = new javax.swing.JButton();
        ButtonReset = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TabelTransaksi = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TabelDetailItem = new javax.swing.JTable();
        ButtonCetakStruk = new javax.swing.JButton();
        ButtonEksporCsv = new javax.swing.JButton();
        ButtonRetur = new javax.swing.JButton();
        ButtonKembali = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Riwayat Transaksi");

        InputCari.setColumns(15);
        InputCari.addActionListener(this::InputCariActionPerformed);

        jLabel2.setText("Cari Member:");

        jLabel3.setText("Tanggal:");

        InputTanggal.setColumns(15);
        InputTanggal.addActionListener(this::InputTanggalActionPerformed);

        jLabel4.setText("Status:");

        ComboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Selesai", "Retur" }));
        ComboStatus.addActionListener(this::ComboStatusActionPerformed);

        ButtonCari.setText("Cari");
        ButtonCari.addActionListener(this::ButtonCariActionPerformed);

        ButtonReset.setText("Reset");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InputCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InputTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButtonCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButtonReset)
                .addContainerGap(179, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ButtonCari, ButtonReset});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InputCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(InputTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(ComboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonCari)
                    .addComponent(ButtonReset))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ButtonCari, ButtonReset});

        TabelTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "No. Nota", "Tanggal", "Member", "Metode", "Grand Total", "Status"
            }
        ));
        jScrollPane2.setViewportView(TabelTransaksi);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Detail Item Transaksi");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Riwayat Transaksi");

        TabelDetailItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Kode", "Nama Barang", "Qty", "Harga", "Total"
            }
        ));
        jScrollPane3.setViewportView(TabelDetailItem);

        ButtonCetakStruk.setText("Cetak Ulang Struk");
        ButtonCetakStruk.addActionListener(this::ButtonCetakStrukActionPerformed);

        ButtonEksporCsv.setText("Ekspor CSV");
        ButtonEksporCsv.addActionListener(this::ButtonEksporCsvActionPerformed);

        ButtonRetur.setText("Retur / Refund");
        ButtonRetur.addActionListener(this::ButtonReturActionPerformed);

        ButtonKembali.setText("Kembali");
        ButtonKembali.addActionListener(this::ButtonKembaliActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ButtonCetakStruk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ButtonEksporCsv)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ButtonRetur)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ButtonKembali)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ButtonCetakStruk, ButtonEksporCsv, ButtonKembali, ButtonRetur});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonCetakStruk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonEksporCsv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonRetur, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ButtonCetakStruk, ButtonEksporCsv, ButtonKembali, ButtonRetur});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InputCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InputCariActionPerformed

    private void InputTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InputTanggalActionPerformed

    private void ComboStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ComboStatusActionPerformed

    private void ButtonCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonCariActionPerformed

    private void ButtonCetakStrukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCetakStrukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonCetakStrukActionPerformed

    private void ButtonEksporCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonEksporCsvActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonEksporCsvActionPerformed

    private void ButtonReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonReturActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonReturActionPerformed

    private void ButtonKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonKembaliActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RiwayatTransaksi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonCari;
    private javax.swing.JButton ButtonCetakStruk;
    private javax.swing.JButton ButtonEksporCsv;
    private javax.swing.JButton ButtonKembali;
    private javax.swing.JButton ButtonReset;
    private javax.swing.JButton ButtonRetur;
    private javax.swing.JComboBox<String> ComboStatus;
    private javax.swing.JTextField InputCari;
    private javax.swing.JTextField InputTanggal;
    private javax.swing.JTable TabelDetailItem;
    private javax.swing.JTable TabelTransaksi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
