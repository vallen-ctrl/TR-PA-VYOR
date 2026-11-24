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
    public String textToStringNumber(String text){
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
}
