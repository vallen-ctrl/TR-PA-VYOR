package utils;
/** @author jein */
public class Luhn {
    
    private int findchekDigits(int sumAll) {
        int a = (10 - (sumAll % 10)) % 10;
        return a;
    }

    // Menghitung 1 angka pengaman dari deretan angka.
    // Contoh: hitungCheckDigit("001") menghasilkan 8
    /**
     *
     * @param angka
     * @return int + 64 untuk mengirimkan char ASCII A
     */
    public char chekSumGenerate(String Text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Text.length(); i++) {
            sb.append((int) Text.charAt(i));
        }
        int jumlah = 0;

        for (int i = sb.toString().length() - 1; i > 0; i--) {
            int number = sb.toString().charAt(i) - '0';
            if (i % 2 == 0) {
                int temp = number * 2;
                if (temp > 9) {
                    number = (temp - 9);
                } else {
                    number = temp;
                }
            }

            jumlah += number;
        }
        return (char) (findchekDigits(jumlah) + 65);
    }
    // Mengecek apakah angka yang sudah ada pengaman di akhir itu benar.
    // Contoh: isValid("0018") menghasilkan true
    public boolean isValid(String kode) {
        String payload = kode.substring(0, kode.length() - 1);
        char chekDigits = kode.charAt(kode.length() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < payload.length(); i++) {
            sb.append((int) payload.charAt(i));
        }

        int jumlah = 0;

        for (int i = sb.toString().length() - 1; i > 0; i--) {
            int number = sb.toString().charAt(i) - '0';
            if (i % 2 == 0) {
                int temp = number * 2;
                if (temp > 9) {
                    number = (temp - 9);
                } else {
                    number = temp;
                }
            }
            jumlah += number;
        }

        int chekDigitsNums = (int) chekDigits - 65;
        return (jumlah + chekDigitsNums) % 10 == 0;
    }
}
