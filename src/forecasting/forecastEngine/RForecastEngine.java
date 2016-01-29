package forecasting.forecastEngine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import forecasting.forecastEngine.forecastParameters.ArimaParameters;
import forecasting.forecastEngine.forecastParameters.AutoArimaParameters;
import forecasting.forecastEngine.forecastParameters.CochraneOrcuttParameters;
import forecasting.forecastEngine.forecastParameters.ETSparameters;
import forecasting.forecastEngine.forecastParameters.HoltWinterParameters;
import rconfig.RConfig;
import timeseries.RTimeSeries;
import timeseries.TimeSeries;
import timeseries.TimeSeriesFactory;
import timeseries.TimeSeriesFactoryImpl;

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
			,AutoArimaParameters params) throws Exception {
		//create time series
		TimeSeriesFactory tsFac = new TimeSeriesFactoryImpl();
		RTimeSeries rts = (RTimeSeries) tsFac.createTimeSeries(TimeSeries.R_COMPATIBLE, ts.getName(), ts.getSeasonality()
				, ts.getData(), ts.getIndex());
		String rtsName = rts.getName()+".train.ts";
		rts.createRxtsInWorkSpace(Rconn, rtsName, true);
		return null;
	}

	@Override
	public boolean checkRegressorsParams(TimeSeries[] regressors) {
		if(regressors == null)
			throw new NullPointerException("regressors is null");
		int numReg = regressors.length;
		/**
		 * Check equal length for all regressors time series
		 */
		if(numReg >1) {
			for(int i=1;i<regressors.length;i++) {
				if(regressors[i].getLength() !=regressors[0].getLength())
					throw new IllegalArgumentException("Unequal length for regressors:"
							+ "regressor[0] length = "+regressors[0].getLength()
							+"regressor["+i+"] length = "+regressors[i].getLength());
			}
		}
		else
			return true;
		return false;
	}


}
