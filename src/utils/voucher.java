/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.time.LocalDate;
import java.time.Month;
import java.util.InputMismatchException;

/**
 *
 * @author vallen
 */
public class voucher {

    public Integer extensionDays = 3;
    public Integer Lenght = 4;

    public boolean checkIsValidDate(LocalDate TimeNow, LocalDate timeBirthDay) {
        LocalDate batasAwal = LocalDate.of(
                TimeNow.getYear(),
                timeBirthDay.getMonth(),
                timeBirthDay.getDayOfMonth()
        );

        LocalDate batasAkhir = batasAwal.plusDays(this.extensionDays);

        //disini karena isBefore "<" bukan "<=" dan begitupun isAfter maka harus ada sedikit perubahan. untuk menyesuaikan
        //misal tanggal 10-10-2006 ultah berarti valid sampai tanggal 13-10 
        //maka kalau tanpa plus dan minus berarti tanggal validnya hanya dari tanggal 11 - 12 
        //karena bentuk logikanya gini 10-10 > tanggal sekarang && tanggal sekarang < 13-10
        //jadi tanggal validnya hanya 11 sampai 12
        return TimeNow.isBefore(batasAkhir.plusDays(1))
                && TimeNow.isAfter(batasAwal.minusDays(1));
    }

    public String getVoucer(LocalDate TimeNow, LocalDate timeBirthDay) {
        if (this.checkIsValidDate(TimeNow, timeBirthDay)) {
            return new GenerateRandomKode().generate(this.Lenght);
        }
        throw new InputMismatchException("Tidak bisa mendapatkan Voucher");
    }
}
