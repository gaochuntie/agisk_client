package atms.app.agiskclient.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String getDateString(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyMMddHHmm", Locale.US);

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String getCurrentDateTimeString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
        return outputFormat.format(calendar.getTime());
    }
}
