
package timeseries;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;


public class RTimeSeriesTest {
	@Test
	public void testCreateRxtsInWorkSpace1() throws Exception {
		
		double[] data ={112.0,118,132,129,121,135,148,148,136,119,104,118,115,
				126,141,135,125,149,170,170,158,133,114,140};
		ZonedDateTime startZonedDateTime = ZonedDateTime.of(1949, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

		ZonedDateTime[] index = new ZonedDateTime[data.length];
		
		for(int monthOffset = 0 ; monthOffset <data.length ; monthOffset++) {
			index[monthOffset] = startZonedDateTime.plusMonths(monthOffset);
		}

		String name = "AirPassengers";
		
		TimeSeriesFactory tsFactory = new TimeSeriesFactoryImpl();
		RTimeSeries rts = (RTimeSeries) tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE
				,name,Seasonality.MONTH_OF_YEAR, data, index);
		RConnection conn = new RConnection();
		REXP r = rts.createRxtsInWorkSpace(conn, "demand.xts",true);
		conn.close();
		Assert.assertFalse("Check if retrun is not string error meassage", r.isString());
	}
}
