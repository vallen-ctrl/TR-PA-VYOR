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

        return !TimeNow.isBefore(batasAwal)
                && !TimeNow.isAfter(batasAkhir);
    }

    public String getVoucer(LocalDate TimeNow, LocalDate timeBirthDay) {
        if (this.checkIsValidDate(TimeNow, timeBirthDay)) {
            return new GenerateRandomKode().generate(this.Lenght);
        }
        throw new InputMismatchException("Tidak bisa mendapatkan Voucher");
    }
}
