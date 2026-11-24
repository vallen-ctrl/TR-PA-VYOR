package utils;

/**
 * @author jein
 */
public class Luhn {

    private Text tx = new Text();

    private int findchekDigits(int sumAll) {
        int a = (10 - (sumAll % 10)) % 10;
        return a;
    }

    // Menghitung 1 angka pengaman dari deretan angka.
    // Contoh: hitungCheckDigit("001") menghasilkan 8
    /**
     *
     * @param angka
     * @return int + 65 untuk mengirimkan char ASCII A
     */
    public char chekSumGenerate(String Text) {
        String numText = this.tx.textToStringNumber(Text);
        int jumlah = 0;

        for (int i = numText.length() - 1; i >= 0; i--) {
            int number = numText.charAt(i) - '0';
            if ((numText.length() - 1 - i) % 2 == 0) {   
                int temp = number * 2;
                number = (temp > 9) ? temp - 9 : temp;
            }
            jumlah += number;
        }   
        System.out.println("Jumlah: "+jumlah);
        return (char) (findchekDigits(jumlah) + 65);
    }

    // Mengecek apakah angka yang sudah ada pengaman di akhir itu benar.
    // Contoh: isValid("0018") menghasilkan true
    public boolean isValid(String kode) {
        String payload = kode.substring(0, kode.length() - 1);
        char chekDigits = kode.charAt(kode.length() - 1);

        String numText = this.tx.textToStringNumber(payload);

        int jumlah = 0;

        for (int i = numText.length() - 1; i >= 0; i--) {
            int number = numText.charAt(i) - '0';
            if ((numText.length() - 1 - i) % 2 == 0) {   // dihitung dari kanan
                int temp = number * 2;
                number = (temp > 9) ? temp - 9 : temp;
            }
            jumlah += number;
        }

        int chekDigitsNums = (int) chekDigits - 65;
        return (jumlah + chekDigitsNums) % 10 == 0;
    }
}
