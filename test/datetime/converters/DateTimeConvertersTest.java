package datetime.converters;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class DateTimeConvertersTest {
	
	@Test
	public void testConvertZonedDateTimeToRtimeDateStr() {
		ZonedDateTime zdt = ZonedDateTime.of(2016, 1, 2, 10, 15, 33, 0, ZoneId.of("GMT"));
		String rtdStr = DateTimeConverters.convertZonedDateTimeToRtimeDateStr(zdt);
		String expected = "2016-1-2 10:15:33";
		
		org.junit.Assert.assertEquals("Matching ZDT converted string ", expected,rtdStr);
	}
	@Test
	public void testConvertZonedDateTimeToRtimeDateArrStr() {
		ZonedDateTime[] zdtArr = new ZonedDateTime[3];
		zdtArr[0] = ZonedDateTime.of(2016, 1, 2, 10, 15, 33, 0, ZoneId.of("GMT"));
		zdtArr[1] = ZonedDateTime.of(2015, 1, 2, 10, 15, 33, 0, ZoneId.of("GMT"));
		zdtArr[2] = ZonedDateTime.of(2016, 1, 2, 11, 15, 33, 0, ZoneId.of("GMT"));
		String[] zdtToRtdArr = DateTimeConverters.convertZonedDateTimeToRtimeDateStr(zdtArr);
		String[] expected = {"2016-1-2 10:15:33","2015-1-2 10:15:33","2016-1-2 11:15:33"};
		org.junit.Assert.assertArrayEquals("Test converted ZDT arrays", expected, zdtToRtdArr);
		
	}
}
