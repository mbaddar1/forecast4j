package timeseries;

import java.time.ZonedDateTime;
import java.util.Date;

public interface TimeSeriesFactory {
	public TimeSeries createTimeSeries(int TimeSeriesType,String name
			,Seasonality seasonality,double[] data,ZonedDateTime[] index);
}
