package Model;

import config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import utils.Validation;

/**
 *
 * @author vallen
 */

public abstract class model {

    private String tabel = getClass().getSimpleName();
    private Statement stm;
    private String type = "";
    private String[] insCols;
    private String[] insVals;
    private List<String> conditions = new ArrayList<>();
    private List<String> joins = new ArrayList<>();
    private Integer limit;
    private Integer offset = 0;
    private String SQL = "";
    
    public Validation validator;

    public model() {
        this.validator = new Validation();
        try {
            this.stm = new Database().start();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Gagal koneksi database: " + e.getMessage(), e);
        }
    }

    /**
     * Fungsi buildSql adalah proses build ada bagian untuk merekonstruksi sebuah data
     */
    private void buildSql() {
        if (!SQL.isEmpty() && type.isEmpty()) {
            return; // raw query dari setQuery
        }
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "SELECT":
                sb.append("SELECT ")
                        .append(insCols != null ? String.join(",", insCols) : "*")
                        .append(" FROM ").append(tabel);
                break;
            case "INSERT":
                sb.append("INSERT INTO ").append(tabel)
                        .append(" (").append(String.join(",", insCols)).append(") VALUES (");
                for (int i = 0; i < insVals.length; i++) {
                    sb.append(i > 0 ? "," : "").append("'")
                            .append(insVals[i].replace("'", "''")).append("'");
                }
                sb.append(")");
                break;
            case "UPDATE":
                sb.append("UPDATE ").append(tabel).append(" SET ")
                        .append(String.join(", ", insCols));
                break;
            case "DELETE":
                sb.append("DELETE FROM ").append(tabel);
                break;
            default:
                return;
        }
        if (!conditions.isEmpty() && (type.equals("SELECT")
                || type.equals("DELETE")
                || type.equals("UPDATE"))) {
            if(!joins.isEmpty()) sb.append(String.join(" ", joins));
            sb.append(" WHERE ").append(String.join(" ", conditions));
            if (limit != null && limit > 0) {

                sb.append(" LIMIT %d".formatted(limit));
                // batasi maksimum
                // offset hanya dipakai kalau limit ada (MySQL tidak mengizinkan OFFSET tanpa LIMIT)
                if (offset != null && offset > 0) {
                    sb.append(" OFFSET %d".formatted(offset));
                }
            }
        }
        this.SQL = sb.toString();
    }

    private void reset() {
        type = "";
        insCols = null;
        insVals = null;
        SQL = "";
        limit = null;
        offset = null;
        conditions.clear();
        joins.clear();
    }
    
    
    /**
     * ini akan membantu membuat left join di sql dengan menambahkan bantuan leftJoin
     * @param tabel nama tabel yang akan dimasukan 
     * @param ON bentuk relasi antara kedua tabel misal namaTabel1.id = namaTabel2.namatabel1id
     * @return retrun this jadi pakai .run() untuk menjalankan
     */
    public model leftJoin(String tabel, String ON){
        StringBuilder sb = new StringBuilder();
        
        sb.append("LEFT JOIN ")
                .append(tabel)
                .append(ON);
        this.joins.add(sb.toString());
        return this;
    }
    
    /**
     * ini akan membantu membuat right join di sql dengan menambahkan bantuan leftJoin
     * @param tabel nama tabel yang akan dimasukan 
     * @param ON bentuk relasi antara kedua tabel misal namaTabel1.id = namaTabel2.namatabel1id
     * @return retrun this jadi pakai .run() untuk menjalankan
     */
    public model rightJoin(String tabel, String ON){
        StringBuilder sb = new StringBuilder();
        
        sb.append("RIGHT JOIN ")
                .append(tabel)
                .append(ON);
        this.joins.add(sb.toString());
        return this;
    }
    
    /**
     * ini akan membantu membuat inner join di sql dengan menambahkan bantuan leftJoin
     * @param tabel nama tabel yang akan dimasukan 
     * @param ON bentuk relasi antara kedua tabel misal namaTabel1.id = namaTabel2.namatabel1id
     * @return retrun this jadi pakai .run() untuk menjalankan
     */
    public model innerJoin(String tabel, String ON){
        StringBuilder sb = new StringBuilder();
        
        sb.append("INNER JOIN ")
                .append(tabel)
                .append(ON);
        this.joins.add(sb.toString());
        return this;
    }

    public model select(String... Column) {

        this.type = "SELECT";
        if (Column.length > 0) {
            this.insCols = Column;
        }
        return this;
    }

    public model insert(String[] Column, String[] Value) {
        this.type = "INSERT";
        this.insCols = Column;
        this.insVals = Value;
        return this;
    }

    /**
     * ini untuk set limit dari database yang dipanggil dan di tampilkan
     * @param Limit isi dengan jumlah limit yang akan di tambilkan
     * @return jalankan dengan {@code .run()}
     */
    public model setLimit(int Limit) {
        this.limit = Limit;
        return this;
    }
    
    
    /**
     * ini untuk set offset. Set limit terlebih dahulu sebelum set offset
     * @param Offset adalah seberapa banyak anda mau melompati row tersebut
     * @return jalankan dengan {@code .run()}
     */
    public model setOffset(int Offset){
        if(this.limit == null) throw new InputMismatchException("Tidak bisa menerima offset tanpa limit di sebelumnya");
        this.offset = Offset;
        return this;
    }

    /**
     * Pada bagian untuk update pada bagian ini
     *
     * @param Cols adalah cols yang akan anda update
     * @param Value adalah value yang akan anda berikan ke cols tersebut
     * @return akan retrun objek sendiri. jalankan dengan {@code .execute()}
     * bukan {@code .run()}
     */
    public model update(String[] Cols, String[] Value) {
        if (Cols.length != Value.length) {
            throw new InputMismatchException("Jumlah cols dan values tidak sama");
        }
        this.type = "UPDATE";

        this.insCols = new String[Cols.length];
        for (int i = 0; i < Cols.length; i++) {
            this.insCols[i] = Cols[i] + " = '" + Value[i].replace("'", "''") + "'";
        }
        return this;
    }

    public model updateOnce(String Cols, String Value) {
        this.type = "UPDATE";
        this.insCols = new String[]{Cols + " = '" + Value.replace("'", "''") + "'"};
        return this;
    }

    /**
     * ini untuk menghapus sebuah row dari sebuah data,
     *
     * @return akan meretrun model untuk menjalankannya anda bisa pakai
     * .execute(); untuk menajlankan query dan untuk mencari bisa pakai method
     * where
     */
    public model delete() {
        this.type = "DELETE";
        return this;
    }

    /**
     * Memulai query DELETE. Wajib dikombinasikan dengan
     * {@link #where(String...)}.
     *
     * @return objek model ini sendiri; jalankan dengan {@code .execute()},
     * bukan {@code .run()}
     */
    public model where(String... Conditions) {
        for (String c : Conditions) {
            this.conditions.add(c);
        }
        return this;
    }

    public model setQuery(String Query) {
        this.SQL = Query;
        return this;
    }

    public String getQuery() {
        buildSql();
        return this.SQL;
    }

    // SELECT -> ResultSet
    public ResultSet run() throws SQLException {
        buildSql();
        try {
            return this.stm.executeQuery(this.SQL);
        } catch (Exception e) {
            System.out.println("Error message: %s\n SQL_QUERY:%s".formatted(e.getMessage(), this.SQL));
            return null;
        } finally {
            reset();
        }
    }

    /**
     * INSERT/UPDATE/DELETE -> jumlah baris
     *
     * @return jika retrun kurang dari 0 maka terjadi error yang tidak
     * diinginkan.
     * @throws SQLException
     */
    public int execute() throws SQLException {
        buildSql();
        try {
            return this.stm.executeUpdate(this.SQL);
        } catch (Exception e) {
            System.out.println("Error message: %s\n SQL_QUERY:%s".formatted(e.getMessage(), this.SQL));
            return -1;
        } finally {
            reset();
        }
    }
}