package forecasting.forecastEngine;

import forecasting.forecastEngine.forecastParameters.ArimaParameters;
import forecasting.forecastEngine.forecastParameters.AutoArimaParameters;
import forecasting.forecastEngine.forecastParameters.CochraneOrcuttParameters;
import forecasting.forecastEngine.forecastParameters.ETSparameters;
import forecasting.forecastEngine.forecastParameters.HoltWinterParameters;
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
	public ForecastResult sesForecast(TimeSeries training, int horizon);

	public ForecastResult holtWinterForecast(TimeSeries training, int horizon,
			HoltWinterParameters params);

	public ForecastResult etsForecast(TimeSeries training, int horizon,
			ETSparameters params);

	// Forecasting with regressors
	public ForecastResult cochraneOrcuttForecast(TimeSeries training,
			int horizon, CochraneOrcuttParameters params);
	/**
	 * 
	 * @param ts time series to create model for
	 * @param regressors Array of time series , each correspond to one of the regressors
	 * 			all regressor's time series should have the same length 
	 *forecasting horizon = length of any time series in regressors
	 * @return instance of {@link ForecastResult}
	 */
	public ForecastResult regArimaErr(TimeSeries ts,TimeSeries[] regressors
			,ArimaParameters params);
	
	boolean checkTsArrParams(TimeSeries[] tsArr);
	
	/**
	 * 
	 * @param ts time series to create model for
	 * @param regressors Array of time series , each correspond to one of the regressors
	 * 			all regressor's time series should have the same length 
	 *forecasting horizon = length of any time series in regressors
	 *@param timeIndexGranularity string : can be "days" , "weeks" , "months" , "quarters" , "years"
	 * @return instance of {@link ForecastResult}
	 */
	ForecastResult regAutoArimaErr(TimeSeries ts,
			TimeSeries[] trainingRegressors, TimeSeries[] futureRegressors,
			AutoArimaParameters params, String timeIndexGranularity,int horizon)
			throws Exception;
}
