package forecasting.forecastEngine;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import forecasting.forecastEngine.forecastParameters.ArimaParameters;
import forecasting.forecastEngine.forecastParameters.AutoArimaParameters;
import forecasting.forecastEngine.forecastParameters.CochraneOrcuttParameters;
import forecasting.forecastEngine.forecastParameters.ETSparameters;
import forecasting.forecastEngine.forecastParameters.HoltWinterParameters;
import rconfig.RConfig;
import timeseries.TimeSeries;

public class RForecastEngine implements ForecastEngine {

	private RConnection Rconn;
	private RConfig rConfig;
	public RForecastEngine() {
		// TODO Auto-generated constructor stub
	}

	private void initialize(RConfig rConfig) throws RserveException {
		Rconn = new RConnection();
		this.rConfig= rConfig;
		
	}
	private void destroy() {
		Rconn.close();
	}
	@Override
	public ForecastResult sesForecast(TimeSeries training, int horizon) {
			
		return null;
	}

	@Override
	public ForecastResult holtWinterForecast(TimeSeries training, int horizon,
			HoltWinterParameters params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult etsForecast(TimeSeries ts, int horizon,
			ETSparameters params) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public ForecastResult cochraneOrcuttForecast(TimeSeries training,
			int horizon, CochraneOrcuttParameters params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult regArimaErr(TimeSeries ts, TimeSeries[] regressors
			,ArimaParameters params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult regAutoArimaErr(TimeSeries ts,TimeSeries[] regressors
			,AutoArimaParameters params) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public boolean checkRegressorsParams(TimeSeries[] regressors) {
		
		return false;
	}


}