package timeseries;

import java.time.ZonedDateTime;
import java.util.Date;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import datetime.converters.DateTimeConverters;
/**
 * 
 * @author baddar
 *implementation of time series that is compatible with R
 *RTimeSeries is designed to mimic the design of xts in R , and to be compatible with it
 */
public class RTimeSeries extends TimeSeries {

	public RTimeSeries(String name,Seasonality seasonality,
			double[] data,ZonedDateTime[] index) {
		super(name, seasonality, data,index);
	}
	/**
	 * Create xts time series in the given workspace with the given name based on the current time series
	 * data and index
	 * @param rconn R connection to workspace in which we create the variable
	 * @param withVerfication call verification method to verify variable assigned in workspace is correct 
	 * @param xtsInstanceName
	 * @return REXP return from the {@code RConnection.eval used to create the xts time series}
	 * @throws REngineException 
	 * @throws REXPMismatchException 
	 */
	public REXP createRxtsInWorkSpace(RConnection rconn,String xtsInstanceName,boolean withVerfication) throws REngineException, REXPMismatchException {
		//create data array in workspace
		rconn.assign("data", this.getData());
		rconn.assign("timeDateIndexChrVec", DateTimeConverters
				.convertZonedDateTimeToRtimeDateStr(this.getIndex()));
		//assume all ZonedDateTime instances in index have the same time zone
		rconn.assign("timeZone",this.getIndex()[0].getZone().toString());
		System.out.println(rconn.eval("timeZone").asString());
		REXP ret = rconn.eval("require(timeDate)");
		if(!ret.isLogical())
			throw new RserveException(rconn, "return of expression require(timeDate) is not logical");
		if(( (REXPLogical)ret).isFALSE()[0])
			throw new RserveException(rconn, "Can't load timeDate library (may be not installed)");
		String expr = "try(expr = {timeDateIndex <- timeDate(charvec = timeDateIndexChrVec"
				+ ",format = \"%Y-%m-%d %H:%M:%S\",zone = timeZone)})";
		
		ret = rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(rconn, "Error in evaluating experession to set "
					+ "timeDateIndex : "+ret.asString());
		else {
			System.out.println("R timeDateIndex set successfully");
		}
		//create xts instance
		//load xts library
		ret = rconn.eval("require(xts)");
		if(!ret.isLogical())
			throw new RserveException(rconn, "return of expression require(xts) is not logical");
		if( ((REXPLogical)ret).isFALSE()[0])
			throw new RserveException(rconn, "Can't load xts library (may be not installed)");
		
		expr = "try(expr = {"+xtsInstanceName+" = xts(x = data,order.by = timeDateIndex,tzone = "
				+ "timeZone,frequency = "+this.getFrequency()+")})";
		ret = rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(rconn, "Error in evaluating experession to set "
					+ xtsInstanceName+" : "+ret.asString());
		else {
			System.out.println("R xts: "+xtsInstanceName+" set successfully");
		}
		return ret;
	}
	
	
}
