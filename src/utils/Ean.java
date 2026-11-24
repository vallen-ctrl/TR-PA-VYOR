package utils;

/**
 *
 * @author jein
 */
public class Ean {
    
    // ean13 = true: ganjil x1, genap x3 | ean13 = false (EAN-8): ganjil x3, genap x1
    private int hitungCheck(String payload, boolean ean13) {
        int total = 0;
        for (int i = 0; i < payload.length(); i++) {
            int digit = payload.charAt(i) - '0';
            boolean ganjil = (i % 2 == 0);
            int bobot = ean13 ? (ganjil ? 1 : 3) : (ganjil ? 3 : 1);
            total += digit * bobot;
        }
        return (10 - (total % 10)) % 10;
    }

    // true jika kode adalah EAN-13 atau EAN-8 yang valid, selain itu false
    public boolean validateEan(String kode) {
        if (kode == null || !kode.matches("\\d+")) {
            return false;
        }
        boolean ean13 = kode.length() == 13;
        boolean ean8 = kode.length() == 8;
        if (!ean13 && !ean8) {
            return false;
        }
        String payload = kode.substring(0, kode.length() - 1);
        int check = kode.charAt(kode.length() - 1) - '0';
        return hitungCheck(payload, ean13) == check;
    }
}