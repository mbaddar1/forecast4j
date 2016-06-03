package forecasting.forecastEngine;

import java.nio.file.FileSystem;
import java.time.ZonedDateTime;
import java.util.MissingResourceException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import forecasting.forecastEngine.forecastParameters.ArimaParameters;
import forecasting.forecastEngine.forecastParameters.AutoArimaParameters;
import forecasting.forecastEngine.forecastParameters.CochraneOrcuttParameters;
import forecasting.forecastEngine.forecastParameters.ETSparameters;
import forecasting.forecastEngine.forecastParameters.HoltWinterParameters;
import r.RHelper;
import rconfig.RConfig;
import timeseries.RTimeSeries;
import timeseries.Seasonality;
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
	public ForecastResult regAutoArimaErr(TimeSeries ts,TimeSeries[] trainingRegressors
			,TimeSeries[] futureRegressors,AutoArimaParameters params,String dateTimeIndexStep ,
			int horizon,String timeZone) throws Exception {
		//create training data frame
		String trainingDataFrameRName = "train.ts.df";
		String dateTimeColName = "date.time.idx";
		createTrainingDataFrame(Rconn, ts, trainingRegressors,dateTimeColName,trainingDataFrameRName,timeZone);
		
		//debugging code
//		String dexpr = "capture.output("+trainingDataFrameRName+")";
//		REXP dr = Rconn.eval(dexpr);
//		for(int i=0;i<dr.length();i++)
//			System.out.println(dr.asStrings()[i]);
		
		String[] regressorsNames = null;
		String regressorsColsNamesR = null;
		if(trainingRegressors!=null){
			regressorsNames = new String[trainingRegressors.length];
			for(int i=0;i<regressorsNames.length;i++)
				regressorsNames[i] = trainingRegressors[i].getName();
			regressorsColsNamesR = "regressors.col.names.vec";
			Rconn.assign(regressorsColsNamesR,regressorsNames);
		}
		
		
		String rAutoArimaModelName = "auto.arima.model";
		String custAutoArimaRScriptName= "custAutoArima.R";
		//TODO add system file separator
		custAutoArimaRScriptName = rConfig.getrScriptsPath()+"/"+custAutoArimaRScriptName;
		String expr =  "source(\""+custAutoArimaRScriptName+"\")";
		
		expr = "try(expr = {"+expr+"})";
		REXP r = Rconn.eval(expr);
		
		if(r.isString())
			throw new RserveException(Rconn, "Error in evaluating expression : "+expr+" =>"+r.asString());
		String aaSeason = null;
		switch(ts.getSeasonality()) {
		case MONTH_OF_YEAR:
			aaSeason = "my";
			break;
		case QUARTER_OF_YEAR :
			aaSeason = "qy";
			break;
		default :
			throw new IllegalArgumentException("Undefined seasonlity =>"+ts.getSeasonality());
		}
		if(trainingRegressors == null) {
			expr = rAutoArimaModelName+"= auto.arima.cust(train.df = "+trainingDataFrameRName+
					",seasonality = \""+aaSeason+"\",dataTimeCol = \""+dateTimeColName+
					"\",targetCol = \""+ts.getName()+"\", regressorsCols = NULL)";
		}
		else {
			expr = rAutoArimaModelName+"= auto.arima.cust(train.df = "+trainingDataFrameRName+
					",seasonality = \""+aaSeason+"\",dataTimeCol = \""+dateTimeColName+
					"\",targetCol = \""+ts.getName()+"\", regressorsCols = "+regressorsColsNamesR+")";
		}
		expr = "try(expr = {"+expr+"})";
		r = Rconn.eval(expr);
		if(r.isString())
			throw new RserveException(Rconn, "Error in evaluating expression :"+expr+" = >"+r.asString());
		System.out.println("Successfully created arima model");
		//Forecast
		expr = "require(forecast)";
		r = Rconn.eval(expr);
		if(((REXPLogical)r).isFALSE()[0])
			throw new RserveException(Rconn, "Error in "+expr);
		
		String forecastResultRName = "auto.arima.forecast.res";
		if(trainingRegressors ==null)
		{
			expr = forecastResultRName +" = forecast.Arima(object = "+rAutoArimaModelName+",h = "+horizon+")";
			expr = "try(expr = {"+expr+"})";
			r = Rconn.eval(expr);
			if(r.isString())
				throw new RserveException(Rconn, "Error evaluating expr :"+expr+" => "+r.asString());
		}
		else {
			String futureRegressorsMatrixNameR = "future.regressors";
			createRmatrix(futureRegressors, futureRegressorsMatrixNameR);
			
			expr = forecastResultRName +" = forecast.Arima(object = "+rAutoArimaModelName+
					",xreg = "+futureRegressorsMatrixNameR+")";
			expr = "try(expr = {"+expr+"})";
			r = Rconn.eval(expr);
			if(r.isString())
				throw new RserveException(Rconn, "Error evaluating expr :"+expr+" => "+r.asString());
		}
		expr = "as.vector("+forecastResultRName+"$mean)";
		expr = "try(expr = {"+expr+"})";
		r = Rconn.eval(expr);
		if(r.isString())
			throw new RserveException(Rconn, "Error in getting mean forecast");
		double[] mean_forecast = r.asDoubles();
		
		expr = "as.vector("+forecastResultRName+"$lower[,1])";
		expr = "try(expr = {"+expr+"})";
		r = Rconn.eval(expr);
		if(r.isString())
			throw new RserveException(Rconn, "Error in getting lower forecast");
		double[] lower_forecast = r.asDoubles();
		
		expr = "as.vector("+forecastResultRName+"$upper[,1])";
		expr = "try(expr = {"+expr+"})";
		r = Rconn.eval(expr);
		if(r.isString())
			throw new RserveException(Rconn, "Error in getting upper forecast");
		double[] upper_forecast = r.asDoubles();
		
		expr = "as.vector("+forecastResultRName+"$method)";
		r = Rconn.eval(expr);
		String method = r.asString();
		
		expr = "as.vector("+forecastResultRName+"$level)";
		r = Rconn.eval(expr);
		if(r.isString())
			throw new RserveException(Rconn, "Error in getting upper forecast");
		double[] levels= r.asDoubles();
		
		//create mean time series
		//get the starting index of the forecast values
		ZonedDateTime lastTrainZDT = ts.getIndex()[ts.getLength()-1];
		ZonedDateTime[] forecastIndex = new ZonedDateTime[mean_forecast.length];
		
		if(dateTimeIndexStep.equalsIgnoreCase("day")) {
			forecastIndex[0] = lastTrainZDT.plusDays(1);
			for(int i=1;i<mean_forecast.length;i++)
				forecastIndex[i] = forecastIndex[i-1].plusDays(1);
		}
		else if(dateTimeIndexStep.equalsIgnoreCase("week")) {
			forecastIndex[0] = lastTrainZDT.plusWeeks(1);
			for(int i=1;i<mean_forecast.length;i++)
				forecastIndex[i] = forecastIndex[i-1].plusWeeks(1);
		}
		else if(dateTimeIndexStep.equalsIgnoreCase("month")) {
			forecastIndex[0] = lastTrainZDT.plusMonths(1);
			for(int i=1;i<mean_forecast.length;i++)
				forecastIndex[i] = forecastIndex[i-1].plusMonths(1);
		}
		else if(dateTimeIndexStep.equalsIgnoreCase("quarter")) {
			forecastIndex[0] = lastTrainZDT.plusMonths(3);
			for(int i=1;i<mean_forecast.length;i++)
				forecastIndex[i] = forecastIndex[i-1].plusMonths(3);
		}
		else if(dateTimeIndexStep.equalsIgnoreCase("year")) {
			forecastIndex[0] = lastTrainZDT.plusYears(1);
			for(int i=1;i<mean_forecast.length;i++)
				forecastIndex[i] = forecastIndex[i-1].plusYears(1);
		}
		else
			throw new IllegalArgumentException("Unsupported date time index step : "+dateTimeIndexStep);
		TimeSeriesFactory tsFac2 = new TimeSeriesFactoryImpl();
		String forecastName = ts.getName()+".forecast";
		TimeSeries meanForecastTS = tsFac2.createTimeSeries(TimeSeries.R_COMPATIBLE, forecastName,
				ts.getSeasonality(), mean_forecast, forecastIndex);
		ForecastResult forecastRes = new ForecastResult(method, meanForecastTS, lower_forecast, upper_forecast,
				levels);
		return forecastRes;
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
	 * the created matrix is in the workspace attached with RForecastEngineInstance
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
	
	//should be private
	//dataframe structure 
	//DateTimeIndex trainTS (Y) Regressors(X)
	public static boolean createTrainingDataFrame(RConnection Rconn,TimeSeries trainTS,
			TimeSeries[] regressors,String dataTimeIndexColName,String dataFrameName,String timeZone) 
					throws REngineException, REXPMismatchException 
	{
		//assume that no missing data exists
		//TODO handle missing data
		
		//create DateTime index vector
		int len = trainTS.getData().length;
		String format = "%Y-%m-%d";
		String[] dateTimeIndexChrVec = new String[len];
		for(int i=0;i<len;i++)
		{
			dateTimeIndexChrVec[i] = convertZDTtoRPOSIXltStr(trainTS.getIndex()[i], format);
		}
		String dateTimeIndexChrRName = dataTimeIndexColName+".chr";
		String tmpRDataVecName = "tmp.data";
		
		Rconn.assign(dateTimeIndexChrRName, dateTimeIndexChrVec);
		String expr = tmpRDataVecName+" = as.POSIXct(x = strptime(x = "+dateTimeIndexChrRName+
				" ,format = \""+format+"\",tz = \""+timeZone+"\"))";
		expr = "try(expr = {"+expr+"})";
		REXP ret = Rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(Rconn, "Error in evaluating expr : "+expr+" =>"+ret.asString() );
		expr = "try(expr = { "+dataFrameName+" = data.frame("+dataTimeIndexColName+" = "+tmpRDataVecName+")})";
		ret = Rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(Rconn, "Error in evaluating expr : "+expr+" =>"+ret.asString() );
		
		
		Rconn.assign(tmpRDataVecName,trainTS.getData());
		expr = dataFrameName +" = cbind("+dataFrameName+","+trainTS.getName()+"="+tmpRDataVecName+")";
		expr = "try(expr = {"+expr+"})";
		ret = Rconn.eval(expr);
		
		if(ret.isString())
			throw new RserveException(Rconn, "Error in evaluating expr : "+expr+" =>"+ret.asString() );
		if (regressors!=null) {
			for (int i = 0; i < regressors.length; i++) {
				Rconn.assign(tmpRDataVecName, regressors[i].getData());
				expr = dataFrameName + " = cbind(" + dataFrameName + ","
						+ regressors[i].getName() + "=" + tmpRDataVecName + ")";
				expr = "try(expr = {" + expr + "})";
				ret = Rconn.eval(expr);
				if (ret.isString())
					throw new RserveException(Rconn,
							"Error in evaluating expr : " + expr + " =>"
									+ ret.asString());
			}
		}
		return true;
	}
	public static String convertZDTtoRPOSIXltStr(ZonedDateTime zdt,String format) {
		String tmp = format;
		tmp = tmp.replace("%Y",	String.valueOf(zdt.getYear()));
		tmp = tmp.replace("%m", String.valueOf(zdt.getMonthValue()));
		tmp = tmp.replace("%d", String.valueOf(zdt.getDayOfMonth()));
		tmp = tmp.replace("%H", String.valueOf(zdt.getHour()));
		tmp = tmp.replace("%M", String.valueOf(zdt.getMinute()));
		tmp = tmp.replace("%S", String.valueOf(zdt.getSecond()));
		
		return tmp;
	}
}
