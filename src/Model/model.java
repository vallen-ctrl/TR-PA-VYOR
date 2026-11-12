package Model;

import config.Database;
import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class model {

    private String tabel = getClass().getSimpleName();
    private Statement stm;
    private String type = "";
    private String colum = "*";
    private String[] insCols;
    private String[] insVals;
    private List<String> conditions = new ArrayList<>();
    private String SQL = "";

    public model() {
        try {
            this.stm = new Database().start();
        } catch (IOException e) {
            throw new RuntimeException("Gagal koneksi database: " + e.getMessage(), e);
        }
    }

    private void buildSql() {
        if (!SQL.isEmpty() && type.isEmpty()) {
            return; // raw query dari setQuery
        }
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "SELECT":
                sb.append("SELECT ").append(colum).append(" FROM ").append(tabel);
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
            default:
                return;
        }
        if (!conditions.isEmpty() && type.equals("SELECT")) {
            sb.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        this.SQL = sb.toString();
    }

    private void reset() {
        type = "";
        colum = "*";
        insCols = null;
        insVals = null;
        conditions.clear();
    }

    public model select(String... Column) {
        reset();
        this.type = "SELECT";
        if (Column.length > 0) {
            this.colum = String.join(",", Column); // fix: > 0, bukan > 1
        }
        return this;
    }

    public model insert(String[] Column, String[] Value) {
        reset();
        this.type = "INSERT";
        this.insCols = Column;
        this.insVals = Value;
        return this;
    }

    public model where(String... Conditions) {
        for (String c : Conditions) {
            this.conditions.add(c);
        }
        return this;
    }

    public model setQuery(String Query) {
        reset();
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
        return this.stm.executeQuery(this.SQL);
    }

    // INSERT/UPDATE/DELETE -> jumlah baris
    public int execute() throws SQLException {
        buildSql();
        return this.stm.executeUpdate(this.SQL);
    }
}
