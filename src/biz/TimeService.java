package biz;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class TimeService {

    public static final DateTimeFormatter shortTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateTimeFormatter12Hour = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private static final ZoneId zoneIdUtc = ZoneId.of("UTC");
    private static final ZoneId zoneIdEst = ZoneId.of("America/New_York");
    private static final ValueRange businessHourRange = ValueRange.of(8, 22);

    /**
     * Converts a dateTimeString to localtime
     * @param dateString dateTime string to convert/pull time from
     * @return LocalTime object of the parsed time.
     */
    public static LocalTime convertToLocalTime(String dateString){
        return LocalTime.parse(LocalDateTime.parse(dateString, dateTimeFormatter12Hour).format(timeFormatter), timeFormatter);
    }

    /**
     * Converts a dateString to a localDate object
     * @param dateString dateTime string to convert
     * @return LocalDate object of the parsed date
     */
    public static LocalDate convertToLocalDate(String dateString){
        return LocalDate.parse(dateString, dateTimeFormatter12Hour);
    }

    /**
     * Converts a dateTimeString to ZonedDateTime object
     * @param dateString dateTime string to convert.
     * @return ZonedDateTime object
     */
    public static ZonedDateTime convertToZonedDateTime(String dateString, ZoneId zoneId){
        LocalDateTime date = LocalDateTime.parse(dateString, dateTimeFormatter12Hour);
        return date.atZone(zoneId).withZoneSameInstant(zoneId);
    }

    /**
     * Converts a dateTimeString to ZonedDateTime object of EST zone
     * @param dateString dateTime string to convert.
     * @return EST ZonedDateTime object
     */
    public static ZonedDateTime convertToBusinessHoursZonedDateTime(String dateString, ZoneId zoneId){
        LocalDateTime date = LocalDateTime.parse(dateString, dateTimeFormatter12Hour);
        return date.atZone(zoneId).withZoneSameInstant(zoneIdEst);
    }

    /**
     * Converts LocalDate and Time String to ZonedDateTime object, in EST
     * @param localDate LocalDate object to convert (Usually from DatePicker JavaFX control)
     * @param time String of the time selected
     * @return EST ZonedDateTime object
     */
    public static ZonedDateTime convertToBusinessHoursZonedDateTime(LocalDate localDate, String time, ZoneId zoneId){
        LocalTime localStartTime = LocalTime.parse(time, shortTime);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localStartTime);
        return localDateTime.atZone(zoneId).withZoneSameInstant(zoneIdEst);
    }

    /**
     * Checks if selected Date and time are out of business hours
     * @param localDate LocalDate object to convert (Usually from DatePicker JavaFX control)
     * @param time String of the time selected
     * @return true if falls out of business hour range.
     */
    public static boolean outOfBusinessHours(LocalDate localDate, String time, ZoneId zoneId){
        LocalTime localStartTime = LocalTime.parse(time, shortTime);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localStartTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId).withZoneSameInstant(zoneIdEst);
        return ( zonedDateTime.getHour() > businessHourRange.getMaximum() || zonedDateTime.getHour() < businessHourRange.getMinimum());
    }

    /**
     * Converts String timestamp to UTC for database saving
     * @param timeString String of time to convert
     * @return Timestamp object to save to database
     */
    public static Timestamp convertToUtcTimestamp(String timeString, ZoneId zoneId){
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, dateTimeFormatter12Hour);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId).withZoneSameInstant(zoneIdUtc);
        return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
    }

    /**
     * Converts dateTimeString to local ZonedDateTime
     * @param dateString dateTime string to convert.
     * @return ZonedDateTime object
     */
    public static String convertToLocalDateTimeString(String dateString, ZoneId zoneId){
        LocalDateTime date = LocalDateTime.parse(dateString.substring(0, 19), dateTimeFormatter);
        ZonedDateTime zonedDateTime = date.atZone(zoneIdUtc).withZoneSameInstant(zoneId);
        return zonedDateTime.format(dateTimeFormatter12Hour);
    }

    /**
     * Converts DatePicker information to ZonedDateTime object
     * @param date LocalDate object to convert (Usually from DatePicker JavaFX control)
     * @param time String of the time selected
     * @return ZonedDateTime object
     */
    public static String convertToLocalDateTimeString(LocalDate date, String time, ZoneId zoneId){
        LocalTime localStartTime = LocalTime.parse(time, shortTime);
        LocalDateTime localDateTime = LocalDateTime.of(date, localStartTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.format(dateTimeFormatter12Hour);
    }

    /**
     * Returns localTime from String including time
     * @param string time string to collect time from
     * @return LocalTime object
     */
    public static LocalTime getLocalTimeFromString(String string){
        return LocalTime.parse(string, DateTimeFormatter.ofPattern("h:m a"));
    }

    /**
     * Checks to see if date is within current month.
     * @param date ZonedDateTime to check
     * @return true if ZonedDateTime is within current month
     */
    public static Boolean isCurrentMonth(ZonedDateTime date, ZoneId zoneId) {
        YearMonth currentMonth = YearMonth.now(zoneId);
        return currentMonth.equals(YearMonth.from(date));
    }

    /**
     * Checks to see if date is within current week.
     * @param date ZonedDateTime to check
     * @return true if ZonedDateTime is within current week
     */
    public static Boolean isCurrentWeek(ZonedDateTime date, ZoneId zoneId){
        DayOfWeek firstDay = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        ZonedDateTime firstDayTime = ZonedDateTime.now(zoneId).with(TemporalAdjusters.previousOrSame(firstDay));
        DayOfWeek lastDay = DayOfWeek.of(((firstDay.getValue()  + 5) % DayOfWeek.values().length) + 1);
        ZonedDateTime lastDayTime = ZonedDateTime.now(zoneId).with(TemporalAdjusters.nextOrSame(lastDay));
        return date.isAfter(firstDayTime) && date.isBefore(lastDayTime);
    }
}
