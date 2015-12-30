package timeseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.Assert;
public class TimeSeriesTest {

	@Test
	public void testTimeSeriesCreate() throws ParseException{
		double[] data ={112.0,118,132,129,121,135,148,148,136,119,104,118,115,
				126,141,135,125,149,170,170,158,133,114,140};
		double freq = 12;
		String name = "AirPassengers";
		SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
		Date startDate = formatter.parse("01/1949");
		TimeSeriesFactory tsFactory = new TimeSeriesFactoryImpl();
		TimeSeries ts = tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE, name, startDate
				,Seasonality.MONTH_OF_YEAR, data);
		
		
		double delta = 0.0001;
		//Assert.assertEquals("Class Name must be TimeSeriesR","TimeSeriesR",ts.getClass().getName());
		Assert.assertEquals("Frequency must be 12", freq, ts.getFrequency(), delta);
		Assert.assertArrayEquals("Matching data",data, ts.getData(),delta);
		Assert.assertEquals("Start Date must be Jan 1949",startDate.toString(),ts.getStartDate().toString());
		
	}
}
