/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author vallen
 */
public class Text {

    private final NumberFormat nf
            = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));

    public String formatUang(BigDecimal num) {
        this.nf.setMaximumFractionDigits(0);
        return "Rp. " + this.nf.format(num);
    }

    public String textToStringNumber(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append((int) text.charAt(i));
        }
        return sb.toString();
    }

    public String formatUang(Double num) {
        this.nf.setMaximumFractionDigits(0);
        return "Rp. " + this.nf.format(num);
    }

    public String formatUang(Long num) {
        this.nf.setMaximumFractionDigits(0);
        return "Rp. " + this.nf.format(num);
    }

    public BigDecimal parseUang(String text) {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleaned = text.replaceAll("(?i)^\\s*rp\\.?\\s*", "").trim();

        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            Number n = this.nf.parse(cleaned);
            return new BigDecimal(n.toString());
        } catch (Exception e) {
            String fallback = cleaned.replaceAll("[^0-9,\\-]", "").replace(',', '.');
            if (fallback.isEmpty() || fallback.equals("-")) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(fallback);
        }
    }

    public Double parseUangDouble(String text) {
        return parseUang(text).doubleValue();
    }

    public Long parseUangLong(String text) {
        return parseUang(text).longValue();
    }
}
