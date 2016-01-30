package forecasting.forecastEngine;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
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
	public RForecastEngine(RConfig rConfig) throws RserveException {
		initialize(rConfig);
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
	public boolean checkTsArrParams(TimeSeries[] tsArr) {
		if(tsArr == null)
			throw new NullPointerException("regressors is null");
		int numReg = tsArr.length;
		/**
		 * Check equal length for all tsArr time series
		 */
		if(numReg >1) {
			for(int i=1;i<tsArr.length;i++) {
				if(tsArr[i].getLength() !=tsArr[0].getLength())
					throw new IllegalArgumentException("Unequal length for regressors:"
							+ "regressor[0] length = "+tsArr[0].getLength()
							+"regressor["+i+"] length = "+tsArr[i].getLength());
			}
		}
		else
			return true; //one time series only
		return true; //no exception thrown , return true
	}
	
	//Auxiliary methods
	/**
	 * Convert kxn time series array , where k is number of array element , n is the length of any
	 * time series in the array (assuming all time series have the same length) to nxk R matrix
	 * the created matrix is in the workspace attached with Rconn
	 * for example
	 * input tsArr :
	 * tsArr[0] : 1 2 3 4
	 * tsArr[1] : 5 6 7 8
	 * matrix should be
	 * m :
	 * 1 5
	 * 2 6
	 * 3 7
	 * 4 8
 	 * @param tsArr : array of time series
	 * @param name : name of matrix to create
	 * @return boolean to indicate the status of matrix creation
	 * @throws REXPMismatchException 
	 * @throws REngineException 
	 */
	public boolean createRmatrix(TimeSeries[] tsArr,String name) throws REXPMismatchException
					, REngineException {
		if(checkTsArrParams(tsArr))
		{
			int k = tsArr.length;
			int n = tsArr[0].getLength();
			double[] matData = new double[n*k];
			/**create matrix data array in column wise manner
			 * R stores data column wise
			 */
			for(int j=0;j<k;j++)
				for(int i=0;i<n;i++)
					matData[j*n+i] = tsArr[j].getData()[i];
			
			//create matrix in R
			String dataVecName = name+".data";
			Rconn.assign(dataVecName, matData);
			String expr = "try(expr = {"+name+" = matrix( data = "+dataVecName
					+ ", nrow = "+n+",ncol = "+k+",byrow = FALSE)})";
			REXP r = Rconn.eval(expr);
			if(r.isString())
				throw new RserveException(Rconn, "Error in creating matrix :"+r.asString());
			
		}
		return true;
		
	}
	/**
	 * Evaluate expression
	 * @param name
	 * @return
	 * @throws REngineException
	 */
	public REXP evalExpr(String expr) throws REngineException {
		return Rconn.eval(expr);
	}
}
