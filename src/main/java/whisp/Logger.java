package whisp;

import java.time.LocalDate;
import java.time.LocalTime;

public class Logger {
    public static void info(String message)
    {
        LocalDate actualDate = LocalDate.now();
        LocalTime actualTime = LocalTime.now();
        System.out.println("[INFO] " + actualDate + actualTime + ": " + message);
    }

    public static void error(String message)
    {
        LocalDate actualDate = LocalDate.now();
        LocalTime actualTime = LocalTime.now();
        System.err.println("[ERROR] " + actualDate + actualTime + ": " + message);
    }
}
