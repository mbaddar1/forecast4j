package promotions.analyzer.r;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import data.TransactionsTable;
import data.r.RTransactionsTable;
import promotions.Promotion;
import promotions.PromotionAnalysis;
import promotions.analyzer.PromotionAnalyzer;
import r.RHelper;
import rconfig.RConfig;
import r.RHelper.*;
//TODO Modify RTransactionTable , to avoid using its own Rconnection to avoid incosistency
public class RPromotionAnalyzer implements PromotionAnalyzer {
	private RConnection rconn;
	private RConfig rconf;
	private static final String logPrefix = "log_";
	private String promotionAnalysisRscriptName;
	public RPromotionAnalyzer(RConnection rconn,RConfig rconf,String promotionAnalysisRscriptName) {
		this.rconn = rconn;
		this.rconf = rconf;
		this.promotionAnalysisRscriptName = promotionAnalysisRscriptName;
	}
	@Override
	public PromotionAnalysis analyze(TransactionsTable transactionTable,String qtyColName,
			String[] pricesColNames,String[] logicalPromoColNames) throws Exception {
		RTransactionsTable rTransTable = (RTransactionsTable)transactionTable;
		RConnection tableRconn = rTransTable.getRconn();
		if(tableRconn != this.rconn)
			throw new IllegalArgumentException("Rconnection of transaction table is not the same as "
					+ "passed rconnection.");
		
		//check promotion analysis r script exists
		String promotionAnalysisRscriptPath = this.rconf.getrScriptsPath()+File.separator+
				promotionAnalysisRscriptName;
		Path path = Paths.get(promotionAnalysisRscriptPath);
		if (!Files.exists(path)) {
		  throw new FileNotFoundException("Rscript :"+promotionAnalysisRscriptPath+" cannot be found");
		}
		String expr = "source('"+promotionAnalysisRscriptPath+"')";
		REXP r = RHelper.evalWithTryCatch(this.rconn, expr);
		//Calculate promotions
		String promoAnalysisResultRName = "promo.analysis";
		String transactionTableRName = transactionTable.getName(); //name attribute is also used as r name
		String preprocessedRTransTableName = transactionTableRName+".preprocessed";
		genPreProcessedRTransactionTable(rconn,rTransTable,preprocessedRTransTableName, qtyColName,
				pricesColNames, logicalPromoColNames);
		String fmla = genRLogLogDemandFormula(qtyColName, pricesColNames, logicalPromoColNames,
				logPrefix);
		expr = promoAnalysisResultRName +" = analyze.promotions(formula = "+fmla+",transactions = "+
				preprocessedRTransTableName+")";
		RHelper.evalWithTryCatch(tableRconn, expr);
		
		//consume promotion analysis to get a dataframe of the effects
		String promoAnalysisDataFrameRName = "promo.analysis.df";
		expr = promoAnalysisDataFrameRName+" = consume.promo.analysis("+promoAnalysisResultRName+")";
		RHelper.evalWithTryCatch(tableRconn, expr);
		RHelper.printRVar(tableRconn, promoAnalysisDataFrameRName);
		String promotionDataFrameColName = "promotion"; // related to R code of consume.promo.analysis
		String effectDataFrameColName = "effect"; // related to R code of consume.promo.analysis
		//get names of promotions
		expr = promoAnalysisDataFrameRName+"[,\""+promotionDataFrameColName+"\"]";
		r = tableRconn.eval(expr);
		String[] promoNames = r.asStrings();
		//get effects of promotions
		expr = promoAnalysisDataFrameRName+"[,'"+effectDataFrameColName+"']";
		r = RHelper.evalWithTryCatch(tableRconn, expr);
		double[] effects = r.asDoubles();
		PromotionAnalysis promoAnalysis = new PromotionAnalysis();
		for(int i=0;i<promoNames.length;i++) {
			promoAnalysis.addPromotionEffect(promoNames[i], effects[i]);
		}
		return promoAnalysis;
	}
	public boolean genPreProcessedRTransactionTable (RConnection rconn,RTransactionsTable rTransTable,
			String preprocessedRTransTableName,String qtyColName,String[] pricesColNames,
			String[] logicalPromoColNames) {
		
		try {
			rconn.eval(RHelper.surroundRTryCatch(preprocessedRTransTableName +" = "+rTransTable.getName()));
			//Log Qty
			String expr = preprocessedRTransTableName+"$log_"+qtyColName+" = "+"log("+
					preprocessedRTransTableName+"$"+qtyColName+")";
			//Log prices
			RHelper.evalWithTryCatch(rconn, expr);
			for(int i=0;i<pricesColNames.length;i++) {
				expr = preprocessedRTransTableName+"$log_"+pricesColNames[i]+" = "+"log("+
						preprocessedRTransTableName+"$"+pricesColNames[i]+")";
				
				RHelper.evalWithTryCatch(rconn, expr);
			};
			
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;		
	}
	public String genRLogLogDemandFormula(String qtyColName,String[] pricesColNames,
			String[] logicalPromoColNames,String logPrefix) {
		//parameter checking
		String fmla = "";
		if(qtyColName == null || qtyColName.isEmpty()) {
			throw new NullPointerException("Quantity column name can't be null");
		}
		if(  (pricesColNames ==null || pricesColNames.length ==0)  && (logicalPromoColNames == null || logicalPromoColNames.length==0)) {
			throw new IllegalArgumentException("Prices column names and logical promotion columns names can't be both null or empty.");
		}
		if((pricesColNames !=null) && (pricesColNames.length !=0)) {
			fmla = logPrefix+qtyColName+ " ~ "+logPrefix+pricesColNames[0];
		}
		else if((logicalPromoColNames != null) && (logicalPromoColNames.length!=0)) {
			fmla = logPrefix+qtyColName+ " ~ "+logPrefix+logicalPromoColNames[0];
		}
		else {
			throw new IllegalArgumentException("Prices column names and logical promotion columns names can't be both null or empty.");
		}
		
		for(int i=1;i<pricesColNames.length ;i++) {
			fmla = fmla + " + " +logPrefix+pricesColNames[i];
		}
		for(int i=0;i<logicalPromoColNames.length;i++)
			fmla = fmla + " + " + logicalPromoColNames[i];
		return fmla;
	}
}
