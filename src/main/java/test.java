import org.springframework.http.converter.json.GsonBuilderUtils;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class test {

    public static void main(String[] args) {
        LocalDate localdate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = localdate.format(dateFormatter);
        System.out.println(date);

        LocalTime localtime = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String time = localtime.format(timeFormatter);
        System.out.println(time);

        System.out.println(date+time);
    }

}
