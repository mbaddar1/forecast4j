package forecasting.forecastEngine;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import timeseries.TimeSeries;

public class RForecastEngine implements ForecastEngine {

	private RConnection Rconn;

	public RForecastEngine() {
		// TODO Auto-generated constructor stub
	}

	private void initialize() throws RserveException {
		Rconn = new RConnection();
		
	}
	private void destory() {
		Rconn.close();
	}
	@Override
	public ForecastResult SES_forecast(TimeSeries training, int horizon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult HoltWinter_forecast(TimeSeries training, int horizon,
			HoltWinterParameters params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult ETS_forecast(TimeSeries training, int horizon,
			ETS_Parameters params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForecastResult CochraneOrchutt_forecast(TimeSeries training,
			int horizon, CochraneOrchuttParameters params) {
		// TODO Auto-generated method stub
		return null;
	}

}
