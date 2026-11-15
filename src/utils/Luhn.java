package utils;

public class Luhn {

    // Menghitung 1 angka pengaman dari deretan angka.
    // Contoh: hitungCheckDigit("001") menghasilkan 8
    public static int hitungCheckDigit(String angka) {
        int total = 0;
        boolean gandakan = true;
        for (int i = angka.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(angka.charAt(i));
            if (gandakan) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }
            total = total + digit;
            gandakan = !gandakan;
        }
        return (10 - (total % 10)) % 10;
    }

    // Mengecek apakah angka yang sudah ada pengaman di akhir itu benar.
    // Contoh: isValid("0018") menghasilkan true
    public static boolean isValid(String angkaDenganCheck) {
        if (angkaDenganCheck == null || angkaDenganCheck.length() < 2) {
            return false;
        }
        String angka = angkaDenganCheck.substring(0, angkaDenganCheck.length() - 1);
        int check = Character.getNumericValue(
                angkaDenganCheck.charAt(angkaDenganCheck.length() - 1));
        return hitungCheckDigit(angka) == check;
    }
}
