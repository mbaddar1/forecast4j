package timeseries;

import java.time.ZonedDateTime;
import java.util.Date;
/**
 * 
 * @author baddar
 *implementation of time series that is compatible with R
 *RTimeSeries is designed to mimic the design of xts in R , and to be compatible with it
 */
public class RTimeSeries extends TimeSeries {

	public RTimeSeries(String name,Seasonality seasonality,
			double[] data,ZonedDateTime[] index) {
		super(name, seasonality, data,index);
	}
	
}
