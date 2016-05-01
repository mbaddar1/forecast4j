package timeseries;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import datetime.converters.DateTimeConverters;
/**
 * 
 * @author baddar
 *implementation of time series that is compatible with R
 *RTimeSeries is designed to mimic the design of ts in R , and to be compatible with it
 */
public class RTimeSeries extends TimeSeries {
	
	RConnection rconn;
	public RTimeSeries(String name,Seasonality seasonality,
			double[] data,ZonedDateTime[] index) {
		super(name, seasonality, data,index);
		this.rconn = rconn;
	}
	@Deprecated
	/**
	 * Create xts time series in the given workspace with the given name based on the current time series
	 * data and index
	 * @param rconn R connection to workspace in which we create the variable
	 * @param withVerfication call verification method to verify variable assigned in workspace is correct 
	 * @param xtsInstanceName
	 * @return REXP return from the {@code RConnection.eval used to create the xts time series}
	 * @throws Exception 
	 */
	public REXP createRxtsInWorkSpace(RConnection rconn,String xtsInstanceName,boolean withVerfication) throws Exception {
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
		if(withVerfication)
		{
			if(!verifyRxts(rconn, xtsInstanceName))
				ret = new REXPString("VerificationError");
			else
				System.out.println("xts created in workspace and verified successfully");
		}
		return ret;
	}
	/**
	 * 
	 * @param rconn
	 * @param xtsInstanceName
	 * @return
	 * @throws Exception 
	 */
	private boolean verifyRxts(RConnection rconn,String xtsInstanceName) throws Exception {
		if(!verifyRxtsData(rconn, xtsInstanceName))
			return false;
		if(!verifyRxtsTimeDateIndex(rconn, xtsInstanceName))
			return false;
		return true;
	}
	private boolean verifyRxtsData(RConnection rconn,String xtsInstanceName) throws RserveException, REXPMismatchException {
		boolean check = true;
		REXP r1 = rconn.eval("as.numeric("+xtsInstanceName+")");
		
		if(!r1.isNumeric())
			throw new RserveException(rconn, "Return of as.numeric("+xtsInstanceName+") is not numeric.");
		double[] d = r1.asDoubles();
		Assert.assertArrayEquals("Comparing R xts data with Java time series data"
				, this.getData(), d, 0.0001);
		if(d.length!=this.getData().length)
			check = false;
		for(int i=0;i<d.length;i++)
			if(d[i]!=this.getData()[i])
				check = false;
		return check;
	}
	private boolean verifyRxtsTimeDateIndex(RConnection rconn,String xtsInstanceName) throws Exception {
		
		//load lubridate
		REXP r = rconn.eval("require(lubridate)");
		if(!r.isLogical())
			throw new RserveException(rconn, "return of require(lubridate) is not logical");
		if(((REXPLogical)r).isFALSE()[0])
			throw new RserveException(rconn, "can't load lubridate (may be not installed)");
		r = rconn.eval("try(expr = {timeDateIndex <- index("+xtsInstanceName+")})");
		//Getting the index of time series
		if(r.isString())
			throw new RserveException(rconn, "Error in getting index of xts :"+r.asString());
		
		//compare date components
		String comps[] = {"year","month","day","hour","minute","second"};
		for(int i=0;i<comps.length;i++) {
			boolean chkComp = verifyRxtsDateComponent(rconn,"timeDateIndex",comps[i]);
			if(!chkComp){
				System.err.println("Error in matching timeDate component => "+comps[i]);
				return false;
			}
				
		}
		return true;
	}
	/**
	 * compare the given component the timeDateIndex 
	 * @param rconn Rconnection
	 * @param timeDateInstanceName name of the timeDate object in the workspace
	 * @param component time Date component , can be {"year","month","day","hour","minute","second"}
	 * @return boolean check result
	 * @throws Exception 
	 */
	private boolean verifyRxtsDateComponent(RConnection rconn,String timeDateInstanceName,String component) throws Exception {
		
		//possible R function in lubridate to get timeDate components
		String[] arr = {"year","month","day","hour","minute","second"};
		Set<String> possibleRfunctions = new HashSet<String>(Arrays.asList(arr));
		if(!possibleRfunctions.contains(component))
			throw new IllegalArgumentException(component +" is not accepted timeDate component");
		String expr = "try(expr = {"+component+"("+timeDateInstanceName+")})";
		REXP r = rconn.eval(expr);
		if(r.isString())
			throw new RserveException(rconn, "Error in evaluating expression "+expr+" =>"+r.asString());
		double[] comp = r.asDoubles();
		boolean check = true;
		if(comp.length!=this.getIndex().length)
		{
			check = false;
			throw new Exception("unmatched date length in timeDate index");
		}
		switch(component) {
		case ("year") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getYear())
					check = false;
			break;
		case("month") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getMonthValue())
					check = false;
			break;
		case("day") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getDayOfMonth())
					check = false;
			break;
		case("hour") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getHour())
					check = false;
			break;
		case("minute") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getMinute())
					check = false;
			break;
		case("second") :
			for(int i=0;i<comp.length;i++)
				if(comp[i]!=this.getIndex()[i].getSecond())
					check = false;
			break;
		default :
			check = false;
			throw new IllegalArgumentException(component +" is not accepted timeDate component");
		}
		return check;
	}
	@Override
	protected double[] imputeMissing(String imputationMethod) {
		// TODO Auto-generated method stub
		return null;
	}
}
