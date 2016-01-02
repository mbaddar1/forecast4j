package datetime.converters;

import java.time.ZonedDateTime;

public abstract class DateTimeConverters {
	/**
	 * converts zoned time date instance to R timeDate string representation
	 * @param zdt zonedDateTime instance
	 * @return string representation to use in constructing timeDate object in R in the format
	 * "%Y-%m-%d %H:%M:%S"
	 */
	public static String convertZonedDateTimeToRtimeDateStr(ZonedDateTime zdt)
	{
		return (zdt.getYear()+"-"+zdt.getMonthValue()+"-"+zdt.getDayOfMonth()+" "
						+zdt.getHour()+":"+zdt.getMinute()+":"+zdt.getSecond());
	}

}