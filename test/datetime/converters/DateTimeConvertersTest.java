package datetime.converters;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import junit.framework.Assert;

import org.junit.Test;

public class DateTimeConvertersTest {
	
	@Test
	public void testConvertZonedDateTimeToRtimeDateStr() {
		ZonedDateTime zdt = ZonedDateTime.of(2016, 1, 2, 10, 15, 33, 0, ZoneId.of("GMT"));
		String rtdStr = DateTimeConverters.convertZonedDateTimeToRtimeDateStr(zdt);
		String expected = "2016-1-2 10:15:33";
		
		org.junit.Assert.assertEquals("Matching ZDT converted string ", expected,rtdStr);
	}

}
