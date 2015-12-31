package timeseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.Test;
import org.junit.Assert;
public class TimeSeriesTest {

	@Test
	public void testTimeSeriesCreate() throws ParseException{
		double[] data ={112.0,118,132,129,121,135,148,148,136,119,104,118,115,
				126,141,135,125,149,170,170,158,133,114,140};
		ZonedDateTime startZonedDateTime = ZonedDateTime.of(1949, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

		ZonedDateTime[] index = new ZonedDateTime[data.length];
		
		for(int monthOffset = 0 ; monthOffset <data.length ; monthOffset++) {
			index[monthOffset] = startZonedDateTime.plusMonths(monthOffset);
		}
		
		double freq = 12;
		String name = "AirPassengers";
		
		TimeSeriesFactory tsFactory = new TimeSeriesFactoryImpl();
		TimeSeries ts = tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE
				,name,Seasonality.MONTH_OF_YEAR, data, index);
		
		double delta = 0.0001;
		//Assert.assertEquals("Class Name must be TimeSeriesR","TimeSeriesR",ts.getClass().getName());
		Assert.assertEquals("Frequency must be 12", freq, ts.getFrequency(), delta);
		Assert.assertArrayEquals("Matching data",data, ts.getData(),delta);
		Assert.assertEquals(ts.getIndex()[0],startZonedDateTime);
		
	}
}
