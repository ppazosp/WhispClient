package whisp.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void info(String message)
    {
        LocalDate actualDate = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String actualTime =  now.format(formatter);
        System.out.println("[INFO] " + actualDate + actualTime + ": " + message);
    }

    public static void error(String message)
    {
        LocalDate actualDate = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String actualTime =  now.format(formatter);
        System.err.println("[ERROR] " + actualDate + actualTime + ": " + message);

    }
}
