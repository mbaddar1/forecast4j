package timeseries;

import java.util.Date;

public interface TimeSeriesFactory {
	public TimeSeries createTimeSeries(int TimeSeriesType,String name,Date startDate
			,Seasonality seasonality,double[] data);
}
