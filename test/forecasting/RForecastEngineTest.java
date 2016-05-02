package forecasting;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import forecasting.forecastEngine.ForecastResult;
import forecasting.forecastEngine.RForecastEngine;
import forecasting.forecastEngine.factory.ForecastEngineFactory;
import forecasting.forecastEngine.factory.ForecastEngineFactoryImpl;
import rconfig.RConfig;
import timeseries.RTimeSeries;
import timeseries.Seasonality;
import timeseries.TimeSeries;
import timeseries.TimeSeriesFactory;
import timeseries.TimeSeriesFactoryImpl;

public class RForecastEngineTest {
	
	@Test
	public void testCreateRmatrixFromTimeSeriesArr() throws REXPMismatchException, REngineException {
		double[] ts1Data = {1,2,3,4};
		double[] ts2Data = {5,6,7,8};
		ZonedDateTime startZDT = ZonedDateTime.of(2015, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
		ZonedDateTime[] index = new ZonedDateTime[ts1Data.length];
		for(int i=0;i<ts1Data.length;i++)
			index[i] = startZDT.plusMonths(i);
		TimeSeriesFactory tsFac = new TimeSeriesFactoryImpl();
		TimeSeries ts1 = tsFac.createTimeSeries(TimeSeries.R_COMPATIBLE, "ts1"
				, Seasonality.MONTH_OF_YEAR, ts1Data, index);
		TimeSeries ts2 = tsFac.createTimeSeries(TimeSeries.R_COMPATIBLE, "ts2"
				, Seasonality.MONTH_OF_YEAR, ts2Data, index);
		TimeSeries[] tsArr = new TimeSeries[2];
		tsArr[0] = ts1;
		tsArr[1] = ts2;
		
		//get the matrix
		ForecastEngineFactory rfac = new ForecastEngineFactoryImpl();
		RForecastEngine reng = (RForecastEngine) rfac.createEngine(ForecastEngineFactory.REngine
				,new RConfig());
		String matName = "mat";
		reng.createRmatrix(tsArr, matName);
		
		REXP r = reng.evalExpr("as.vector("+matName+")");
		REXP r1 = reng.evalExpr("class("+matName+")");
		if(r == null || !r.isNumeric() || r.length()!=8)
			Assert.assertTrue("returned matrix is either null , "
					+ "not numeric or not of proper length", false);
		else if(!r1.asString().equals("matrix"))
			Assert.assertTrue("return is not matrix", false);
		else
		{
			double[] rdata = r.asDoubles();
			double[] expecteds = {1,2,3,4,5,6,7,8};
			Assert.assertArrayEquals("Asserting matrix values", expecteds, rdata, 0.001);
		}
	}
	/**
	 * Test using usconsumption data in r library forecasting
	 * train has two fields "consumption" "income"
	 * consumption	: is the main ts data , to forecast
	 * income : is the regressor
	 * @throws Exception 
	 */
	@Test
	public void testRegArimaErrFppUScomsuption() throws Exception {
		/**
		 * R command to get training data
		 * train = usconsumption[1:160,] //first 160 records
		 * test = usconsumption[161:164,] //last 4 records
		 */
		double[] trainConsumptionData = {0.61227692,0.454929794,0.874673021,-0.272514385,1.892186993,0.913378185,0.792857898,1.649995662
				,1.32724825,1.889905062,1.532724163,2.317057775,1.813855694,-0.050557716,0.359667223,-0.293315461,-0.878770942,0.346720029
				,0.411953557,-1.47820468,0.837359874,1.653973693,1.414318836,1.053109931,1.97774749,0.915072184,1.050746068,1.295196193,1.135458885
				,0.551532397,0.950159598,1.496161496,0.582299782,2.11467168,0.418698865,0.802764302,0.50412878,-0.058551127,0.977555967,0.26591209
				,-0.173684254,-2.296562996,1.066919835,1.32441742,0.54583283,0,0.404821844,-0.758748833,0.643998142,0.356859498,0.76412375,1.807886611
				,0.975937338,1.96559809,1.751349705,1.573740048,0.853227275,1.420025736,0.769502001,1.307478026,1.681281553,0.907910806,1.880850439
				,0.219864034,0.831533592,1.059663705,1.732441717,0.600062434,-0.152288,1.329357293,1.110416851,0.240125469,1.656928516,0.723060308
				,0.786814118,1.170680141,0.365226244,0.446943251,1.031342865,0.487945312,0.787867942,0.329588878,0.379094008,-0.782282375,-0.283580869
				,0.758193783,0.382567416,-0.044932041,1.70442848,0.591033456,1.099312177,1.212615834,0.405115351,0.955401523,1.079080891,0.886099341
				,1.107815847,0.73801073,0.79832641,0.982355812,0.116703636,0.816439436,0.890123271,0.700256683,0.909995107,1.125174154,0.597491051
				,0.811049811,1.002314791,0.40370845,1.685618761,1.137796247,0.989350157,1.706687585,1.316901051,1.522383594,0.981498554,1.56147049
				,1.194790348,1.400264206,1.505040637,0.935882738,0.974321842,0.88064976,0.398685386,0.376512295,0.439188594,1.553691,0.343826889
				,0.506654039,0.675711941,0.354724654,0.503872727,0.985555729,1.337666705,0.542546733,0.880747948,0.443979489,0.870378697,1.073951523
				,0.793938878,0.984778893,0.756278018,0.248197873,1.01902713,0.600482189,0.59799998,0.925841126,0.554241245,0.38257957,0.43929546,0.294658721
				,-0.252665213,-0.03553182,-0.971774467,-1.313503998,-0.387483995,-0.470083019,0.574000955,0.109328853
			};
		double[] trainIncomeData = {0.496540045,1.736459591,1.344880981,-0.328145953,1.965432327,1.490757133,0.442927733,1.050230993,0.629713564,0.934207242
				,2.024456496,3.901784825,1.129195652,0.981487307,0.503626895,1.211474571,-1.546943855,-0.860297695,0.310671244,-0.452458059,-0.393048299
				,4.564235521,-1.359432036,1.006575406,1.462092078,0.819999864,0.896376224,0.715996597,0.082385052,1.131054305,1.490114704,1.995499552
				,0.614904129,1.141343133,0.808026906,0.782540078,1.087249832,-0.700631545,0.568900915,0.797134552,0.279831351,-1.409415369,1.02444505
				,2.043130338,-0.229300048,0.01377284,2.193050433,0.201893379,0.116463988,0.69817265,0.436938581,0.351269015,0.784231599,0.72600744
				,1.489909944,2.042140999,2.163513516,1.707008683,1.545359737,0.946974902,-0.246259445,1.966841071,-0.620830801,1.030018787,1.18335512
				,1.128116143,0.523753063,0.059897054,0.611885129,-1.09018891,1.772266694,1.410476141,1.248862406,0.934536046,0.766858588,0.896061766
				,1.130999186,-0.411533933,0.645197097,0.762948357,0.750360325,0.655150176,0.072718052,-0.676795704,0.307585359,0.755588074,0.21195715
				,0.649885878,1.497981581,0.763512573,0.506330196,1.412562187,-1.477541558,1.543168336,0.212590891,1.46302707,-0.400479849,1.69184918
				,0.73204843,1.317421015,0.646210704,0.009071803,0.745603285,0.591013847,1.059530012,1.068869375,0.847813936,0.547321447,0.890373141
				,0.748012589,1.138248868,1.379965421,2.248544314,1.417721496,1.04531261,0.730112366,0.675449171,0.228461748,0.64491755,1.55565084
				,2.079917327,1.019653998,1.055609188,0.149548646,0.7480245,-0.274110069,2.514669298,-1.173044414,2.659477606,0.549165469,-0.343744371
				,0.23623426,0.367138124,1.501775306,1.39003078,0.573803177,0.443164179,0.983686803,0.666082163,1.388903698,-1.225522766,0.701673549
				,0.594778066,0.545328307,1.85567861,0.882341774,0.479144681,1.302357376,0.451389832,0.149334878,0.392083863,0.548559897,1.430770981
				,1.973964634,-2.308604067,-0.057706851,-0.969060881,0.063290188,-1.392556217,-0.144713401
		};
		
		double[] futureIncomeData = {1.1871651,1.3543547,0.5611698,0.3710579};
		int horizon = 4;
		ZonedDateTime trainStartZonedDateTime = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
		ZonedDateTime[] trainIndex = new ZonedDateTime[trainConsumptionData.length];
		ZonedDateTime[] futureIndex = new ZonedDateTime[futureIncomeData.length];
		/**
		 * increment month by 3 , to create quarter based timeDate index
		 */
		for(int monthOffset = 0 ; (monthOffset/3) <trainConsumptionData.length ; monthOffset+=3) {
			int quarterIdx = monthOffset/3;
			trainIndex[quarterIdx] = trainStartZonedDateTime.plusMonths(monthOffset);
		}
		ZonedDateTime futureStartZDT = trainIndex[trainIndex.length-1].plusMonths(3);
		
		for(int monthOffset=0 ; (monthOffset/3) <futureIndex.length;monthOffset+=3){
			int quarterIdx = monthOffset/3;
			futureIndex[quarterIdx] = futureStartZDT.plusMonths(monthOffset);
		}
			
		
		String trainConsumptionTsName = "consumption";
		String trainIncomeTsName = "income";
		String futureIncomeTsName = "future.income";
		TimeSeriesFactory tsFactory = new TimeSeriesFactoryImpl();
		RTimeSeries trainConsumptionTS = (RTimeSeries) tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE
				,trainConsumptionTsName,Seasonality.QUARTER_OF_YEAR, trainConsumptionData, trainIndex);
		RTimeSeries trainIncomeTS = (RTimeSeries) tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE
				,trainIncomeTsName,Seasonality.QUARTER_OF_YEAR, trainIncomeData, trainIndex);
		RTimeSeries futureIncomeTS = (RTimeSeries) tsFactory.createTimeSeries(TimeSeries.R_COMPATIBLE
				,futureIncomeTsName, Seasonality.QUARTER_OF_YEAR, futureIncomeData, futureIndex);
		TimeSeries[] trainRegressors = new TimeSeries[1];
		trainRegressors[0] = trainIncomeTS;
		TimeSeries[] futureRegressors = new TimeSeries[1];
		futureRegressors[0] = futureIncomeTS;
		ForecastEngineFactory fengFac = new ForecastEngineFactoryImpl();
		RConfig rconf = new RConfig();
		rconf.setrScriptsPath("/home/baddar/git/forecast4j/R/scripts");
		RForecastEngine reng = (RForecastEngine) fengFac.createEngine(ForecastEngineFactory.REngine
				,rconf);
		ForecastResult fres = reng.regAutoArimaErr(trainConsumptionTS, trainRegressors,futureRegressors, null,"quarter",horizon);
		
		double[] expectedMeanForecast = {0.8413029,0.8664852,0.6849065,0.6469838};
		Assert.assertArrayEquals("Test forecasting mean",expectedMeanForecast
				,fres.getMean().getData(), 0.001);
		double[] expectedLowerForecast = {0.08611257,0.10721230,-0.10569307,-0.15610079};
		Assert.assertArrayEquals("Test forecasting lower",expectedLowerForecast, fres.getLower(),0.001);
		
		double[] expectedUpperForecast = {1.596493,1.625758,1.475506,1.450068};	
		Assert.assertArrayEquals("Test forecasting upper",expectedUpperForecast, fres.getUpper(),0.001);
		
		//Asserting time indices
		ZonedDateTime[] expectedForecastIndex = new ZonedDateTime[4];
		expectedForecastIndex[0] = ZonedDateTime.of(2010, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
		for(int i=1;i<expectedForecastIndex.length;i++)
			expectedForecastIndex[i]=expectedForecastIndex[i-1].plusMonths(3);
		
		for(int i=0;i<expectedForecastIndex.length;i++)
			Assert.assertEquals("Test forecast indices",expectedForecastIndex[i], fres.getMean().getIndex()[i]);
	}
	@Test
	public void testConvertZDTtoRPOSIXltStr() {
		ZonedDateTime zdt = ZonedDateTime.of(2016,2,12,11,15,30,0,ZoneId.of("GMT"));
		String format = "%Y-%m-%d %H:%M:%S";
		String s = RForecastEngine.convertZDTtoRPOSIXltStr(zdt, format);
		String expected = "2016-2-12 11:15:30";
		Assert.assertEquals(expected, s);
	}
	@Test
	public void testCreateTrainingDataFrame() throws REngineException, REXPMismatchException {
		double[] consumptionData = {0.61227692,0.454929794,0.874673021,-0.272514385,1.892186993,0.913378185,
				0.792857898,1.649995662,1.32724825,1.889905062,1.532724163,2.317057775,1.813855694,
				-0.050557716,0.359667223,-0.293315461,-0.878770942,0.346720029,0.411953557,-1.47820468,
				0.837359874,1.653973693,1.414318836,1.053109931,1.97774749,0.915072184,1.050746068,1.295196193,
				1.135458885,0.551532397,0.950159598,1.496161496,0.582299782,2.11467168,0.418698865,0.802764302,
				0.50412878,-0.058551127,0.977555967,0.26591209,-0.173684254,-2.296562996,1.066919835,
				1.32441742,0.54583283,0,0.404821844,-0.758748833,0.643998142,0.356859498,0.76412375,
				1.807886611,0.975937338,1.96559809,1.751349705,1.573740048,0.853227275,1.420025736,
				0.769502001,1.307478026,1.681281553,0.907910806,1.880850439,0.219864034,0.831533592,
				1.059663705,1.732441717,0.600062434,-0.152288,1.329357293,1.110416851,0.240125469,
				1.656928516,0.723060308,0.786814118,1.170680141,0.365226244,0.446943251,1.031342865,
				0.487945312,0.787867942,0.329588878,0.379094008,-0.782282375,-0.283580869,0.758193783,
				0.382567416,-0.044932041,1.70442848,0.591033456,1.099312177,1.212615834,0.405115351,
				0.955401523,1.079080891,0.886099341,1.107815847,0.73801073,0.79832641,0.982355812,
				0.116703636,0.816439436,0.890123271,0.700256683,0.909995107,1.125174154,0.597491051,
				0.811049811,1.002314791,0.40370845,1.685618761,1.137796247,0.989350157,1.706687585,1.316901051,
				1.522383594,0.981498554,1.56147049,1.194790348,1.400264206,1.505040637,0.935882738,0.974321842,
				0.88064976,0.398685386,0.376512295,0.439188594,1.553691,0.343826889,0.506654039,0.675711941,
				0.354724654,0.503872727,0.985555729,1.337666705,0.542546733,0.880747948,0.443979489,
				0.870378697,1.073951523,0.793938878,0.984778893,0.756278018,0.248197873,1.01902713,
				0.600482189,0.59799998,0.925841126,0.554241245,0.38257957,0.43929546,0.294658721,
				-0.252665213,-0.03553182,-0.971774467,-1.313503998,-0.387483995,-0.470083019,0.574000955,
				0.109328853,0.671017951,0.717718191,0.653143257,0.875352148};
		
		double[] incomeData = {0.496540045,1.736459591,1.344880981,-0.328145953,1.965432327,1.490757133,
				0.442927733,1.050230993,0.629713564,0.934207242,2.024456496,3.901784825,1.129195652,
				0.981487307,0.503626895,1.211474571,-1.546943855,-0.860297695,0.310671244,-0.452458059,
				-0.393048299,4.564235521,-1.359432036,1.006575406,1.462092078,0.819999864,0.896376224,
				0.715996597,0.082385052,1.131054305,1.490114704,1.995499552,0.614904129,1.141343133,
				0.808026906,0.782540078,1.087249832,-0.700631545,0.568900915,0.797134552,0.279831351,
				-1.409415369,1.02444505,2.043130338,-0.229300048,0.01377284,2.193050433,0.201893379,
				0.116463988,0.69817265,0.436938581,0.351269015,0.784231599,0.72600744,1.489909944,2.042140999,
				2.163513516,1.707008683,1.545359737,0.946974902,-0.246259445,1.966841071,-0.620830801,1.030018787,1.18335512,
				1.128116143,0.523753063,0.059897054,0.611885129,-1.09018891,1.772266694,1.410476141,1.248862406,0.934536046,
				0.766858588,0.896061766,1.130999186,-0.411533933,0.645197097,0.762948357,0.750360325,0.655150176,0.072718052,
				-0.676795704,0.307585359,0.755588074,0.21195715,0.649885878,1.497981581,0.763512573,0.506330196,1.412562187,
				-1.477541558,1.543168336,0.212590891,1.46302707,-0.400479849,1.69184918,0.73204843,1.317421015,0.646210704,
				0.009071803,0.745603285,0.591013847,1.059530012,1.068869375,0.847813936,0.547321447,0.890373141,0.748012589,
				1.138248868,1.379965421,2.248544314,1.417721496,1.04531261,0.730112366,0.675449171,0.228461748,0.64491755,
				1.55565084,2.079917327,1.019653998,1.055609188,0.149548646,0.7480245,-0.274110069,2.514669298,-1.173044414,
				2.659477606,0.549165469,-0.343744371,0.23623426,0.367138124,1.501775306,1.39003078,0.573803177,0.443164179,
				0.983686803,0.666082163,1.388903698,-1.225522766,0.701673549,0.594778066,0.545328307,1.85567861,0.882341774,
				0.479144681,1.302357376,0.451389832,0.149334878,0.392083863,0.548559897,1.430770981,1.973964634,-2.308604067,
				-0.057706851,-0.969060881,0.063290188,-1.392556217,-0.144713401,1.187165135,1.354354721,
				0.561169813,0.37105794};
		ZonedDateTime[] zdtIndex = new ZonedDateTime[consumptionData.length];
		zdtIndex[0] = ZonedDateTime.of(1970,1,1,0,0,0,0,ZoneId.of("GMT"));
		for(int i=1;i<zdtIndex.length;i++) {
			zdtIndex[i] = zdtIndex[i-1].plusMonths(3);
		}
		TimeSeriesFactory tsFac = new TimeSeriesFactoryImpl();
		TimeSeries consumptionTS = tsFac.createTimeSeries(TimeSeries.R_COMPATIBLE,"consumption",Seasonality.QUARTER_OF_YEAR,consumptionData,zdtIndex);
		TimeSeries incomeTS = tsFac.createTimeSeries(TimeSeries.R_COMPATIBLE,"income",Seasonality.QUARTER_OF_YEAR,incomeData,zdtIndex);
		TimeSeries[] regressors = new TimeSeries[1];
		regressors[0] = incomeTS;
		
		String dataFrameName = "con.inc.df";
		RConnection rconn = new RConnection();
		String dateTimeColNameR = "date.time";
		RForecastEngine.createTrainingDataFrame(rconn, consumptionTS, regressors,dateTimeColNameR, dataFrameName);
		
		REXP ret = rconn.eval("capture.output("+dataFrameName+")");
		String[] out = ret.asStrings();
		for(int i=0;i<out.length;i++){
			String s = out[i];
			System.out.println(s);
		}
		
		String expr = dataFrameName+"[,\""+consumptionTS.getName()+"\"]";
		expr = "as.vector("+expr+")";
		expr = "try(expr = {"+expr+"})";
		ret = rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(rconn, "Error in evaluating "+expr+" => "+ret.asString());
		double[] retConsumptionData = ret.asDoubles();
		Assert.assertArrayEquals("validating consumption data", consumptionData, retConsumptionData, 0.01);
		
		expr = dataFrameName+"[,\""+incomeTS.getName()+"\"]";
		expr = "as.vector("+expr+")";
		expr = "try(expr = {"+expr+"})";
		ret = rconn.eval(expr);
		if(ret.isString())
			throw new RserveException(rconn, "Error in evaluating "+expr+" => "+ret.asString());
		double[] retIncomeData = ret.asDoubles();
		
		Assert.assertArrayEquals("validating income data", incomeData, retIncomeData, 0.01);
		
		expr = dataFrameName+"[,1]";
		expr = "as.character("+expr+")";
		expr = "try(expr = {"+expr+"})";
		ret = rconn.eval(expr);
//		if(ret.isString())
//			throw new RserveException(rconn, "Error in evaluating "+expr+" => "+ret.asString());
		String[] tmp = ret.asStrings();
		Assert.assertEquals("Comparing first date time index", "1970-01-01", tmp[0]);
		Assert.assertEquals("Comparing last date time index", "2010-10-01", tmp[tmp.length-1]);
		rconn.close();
		
	}
}
