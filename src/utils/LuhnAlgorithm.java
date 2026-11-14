/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author vallen
 */
public class LuhnAlgorithm {
      private int findchekDigits(int sumAll) {
        int a = (10 - (sumAll % 10)) % 10;
        return a;
    }

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
}
