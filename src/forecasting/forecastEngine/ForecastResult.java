package forecasting.forecastEngine;

import timeseries.RTimeSeries;
import timeseries.TimeSeries;

/**
 * Result for {@code RForecastEngine} different forecasting methods
 * The structure is similar to forecast data structure in R
 * link:
 * http://www.inside-r.org/packages/cran/forecast/docs/forecast
 * @author baddar
 *
 */
public class ForecastResult {
	
	private TimeSeries mean;
	private double[] lower;
	private double[] upper;
	private double[] levels;
	
	private String forecastMethod;
	public ForecastResult(String forecastMethod, TimeSeries mean,
			double[] lower, double[] upper, double[] levels) {
		super();
		this.forecastMethod = forecastMethod;
		this.mean = mean;
		this.lower = lower;
		this.upper = upper;
		this.levels = levels;
	}

	public String getForecastMethod() {
		return forecastMethod;
	}

	public TimeSeries getMean() {
		return mean;
	}

	public double[] getLower() {
		return lower;
	}

	public double[] getUpper() {
		return upper;
	}

	public double[] getLevels() {
		return levels;
	}

	
	
}
