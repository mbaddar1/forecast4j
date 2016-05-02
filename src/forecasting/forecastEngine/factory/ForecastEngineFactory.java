package forecasting.forecastEngine.factory;

import org.rosuda.REngine.Rserve.RserveException;

import rconfig.RConfig;
import forecasting.forecastEngine.ForecastEngine;

public interface ForecastEngineFactory {
	public static final int REngine = 1;
	
	ForecastEngine createEngine(int type, RConfig rConfig) throws RserveException;
}
