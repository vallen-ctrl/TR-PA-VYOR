package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.Timer;

public class TimeNow {

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss", ID);
    private static final DateTimeFormatter DAY_FORMAT =
            DateTimeFormatter.ofPattern("EEEE", ID);

    private Timer timerJam;

    public String getTimeNow(String pattern) {
        return getTimeNow(pattern, Locale.getDefault());
    }

    public String getTimeNow(String pattern, Locale locale) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern, locale));
    }

    public String getDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMAT);
    }

    public String getDay() {
        return LocalDateTime.now().format(DAY_FORMAT);
    }

    public void startTime(JLabel dateTime, JLabel day) {
        Objects.requireNonNull(dateTime, "dateTime label tidak boleh null");
        Objects.requireNonNull(day, "day label tidak boleh null");

        stopTime(); // cegah timer ganda

        timerJam = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now(); // satu sumber waktu
            day.setText(now.format(DAY_FORMAT));
            dateTime.setText(now.format(DATE_TIME_FORMAT));
        });
        timerJam.setInitialDelay(0);
        timerJam.start();
    }

    public void stopTime() {
        if (timerJam != null) {
            timerJam.stop();
            timerJam = null;
        }
    }
}