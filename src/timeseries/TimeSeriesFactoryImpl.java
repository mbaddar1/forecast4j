package timeseries;

import java.time.ZonedDateTime;
import java.util.Date;

public class TimeSeriesFactoryImpl implements TimeSeriesFactory {

	@Override
	public TimeSeries createTimeSeries(int timeSeriesType, String name,Seasonality seasonality
			, double[] data,ZonedDateTime[] index) {
		switch (timeSeriesType) {
		case TimeSeries.R_COMPATIBLE: {
			return new RTimeSeries(name,seasonality, data,index);
		}

		default: {
			throw new IllegalArgumentException("Unknown time series type "
					+ timeSeriesType);
		}

		}
	}

}
