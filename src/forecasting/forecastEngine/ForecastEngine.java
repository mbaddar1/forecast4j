package forecasting.forecastEngine;

import timeseries.TimeSeries;

/**
 * 
 * @author baddar Forecast future data based on historical data 2 ways to do
 *         that 1-Get the train time series model , forecast and return values
 *         2-load a saved model , forecast and return results for now we take
 *         the first implementation , relying on acceptable time for model
 *         generation
 */
public interface ForecastEngine {

	// Exponential Smoothing
	public ForecastResult SES_forecast(TimeSeries training, int horizon);

	public ForecastResult HoltWinter_forecast(TimeSeries training, int horizon,
			HoltWinterParameters params);

	public ForecastResult ETS_forecast(TimeSeries training, int horizon,
			ETS_Parameters params);

	// Forecasting with regressors
	public ForecastResult CochraneOrchutt_forecast(TimeSeries training,
			int horizon, CochraneOrchuttParameters params);
	
	public ForecastResult RegARIMAerr(TimeSeries ts,int horizon);
	
	//TODO Add and implement other forecasting methods (ARIMA and its variants,Bayesian)

}
