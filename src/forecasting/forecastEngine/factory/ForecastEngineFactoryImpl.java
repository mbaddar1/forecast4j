package forecasting.forecastEngine.factory;

import org.rosuda.REngine.Rserve.RserveException;

import rconfig.RConfig;
import forecasting.forecastEngine.ForecastEngine;
import forecasting.forecastEngine.RForecastEngine;

public class ForecastEngineFactoryImpl implements ForecastEngineFactory{

	@Override
	public ForecastEngine createEngine(int type,RConfig rConfig) throws RserveException {
		switch(type) {
		case ForecastEngineFactory.REngine:
			return new RForecastEngine(rConfig);
		default :
			throw new IllegalArgumentException("Unknow type parameter : "+type);
		}
	}
}
