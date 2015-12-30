package timeseries;

import java.util.Date;

public class TimeSeriesFactoryImpl implements TimeSeriesFactory {

	@Override
	public TimeSeries createTimeSeries(int timeSeriesType, String name,
			Date startDate, Seasonality seasonality, double[] data) {
		switch (timeSeriesType) {
		case TimeSeries.R_COMPATIBLE: {
			return new RTimeSeries(name, startDate, seasonality, data);
		}

		default: {
			throw new IllegalArgumentException("Unknown time series type "
					+ timeSeriesType);
		}

		}
	}

}
