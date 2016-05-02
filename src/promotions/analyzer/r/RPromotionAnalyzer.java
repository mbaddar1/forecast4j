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
import promotions.PromotionAnalysis;
import promotions.analyzer.PromotionAnalyzer;
import r.RHelper;
import rconfig.RConfig;
import r.RHelper.*;
//TODO Modify RTransactionTable , to avoid using its own Rconnection to avoid incosistency
public class RPromotionAnalyzer implements PromotionAnalyzer {
	RConnection rconn;
	RConfig rconf;
	String logPrefix;
	public RPromotionAnalyzer(RConnection rconn,RConfig rconf) {
		this.rconn = rconn;
		this.logPrefix = "log_";
		this.rconf = rconf;
	}
	@Override
	public PromotionAnalysis analyze(TransactionsTable transactionTable,String qtyColName,
			String[] pricesColNames,String[] logicalPromoColNames) throws Exception {
		RTransactionsTable rTransTable = (RTransactionsTable)transactionTable;
		RConnection tableRconn = rTransTable.getRconn();
		String promotionAnalysisRscriptName = "PromotionAnalysis.R";
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
		String expr = "source(+"+promotionAnalysisRscriptName+")";
		REXP r = RHelper.evalWithTryCatch(this.rconn, expr);
		//Calculate promotions
		String promoAnalysisResultRName = "promo.analysis";
		String transactionTableRName = transactionTable.getName(); //name attribute is also used as r name
		String preprocessedRTransTableName = transactionTableRName+".preprocessed";
		genPreProcessedRTransactionTable(rconn,rTransTable,preprocessedRTransTableName, qtyColName,
				pricesColNames, logicalPromoColNames);
		String fmla = genRLogLogDemandFormula(qtyColName, pricesColNames, logicalPromoColNames,
				preprocessedRTransTableName);
		expr = promoAnalysisResultRName +" = analyze.promotions(formula = "+fmla+",transactions = "+
				preprocessedRTransTableName+")";
		r = RHelper.evalWithTryCatch(tableRconn, expr);
		return null;
	}
	public String genPreProcessedRTransactionTable (RConnection rconn,RTransactionsTable rTransTable,
			String preprocessedRTransTableName,String qtyColName,String[] pricesColNames,
			String[] logicalPromoColNames) {
		
		try {
			rconn.eval(RHelper.surroundRTryCatch(preprocessedRTransTableName +" = "+rTransTable.getName()));
			//Log Qty
			String expr = preprocessedRTransTableName+"$log_"+qtyColName+" = "+"log("+
					preprocessedRTransTableName+"$"+qtyColName+")";
			//Log prices
			REXP r = RHelper.evalWithTryCatch(rconn, expr);
			for(int i=0;i<pricesColNames.length;i++) {
				expr = preprocessedRTransTableName+"$log_"+pricesColNames[i]+" = "+"log("+
						preprocessedRTransTableName+"$"+pricesColNames[i]+")";
			};
			
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return null;
		//take log for price columns
		
	}
	public String genRLogLogDemandFormula(String qtyColName,String[] pricesColNames,
			String[] logicalPromoColNames,String logPrefix) {
		String fmla = logPrefix+qtyColName+ " ~ ";
		for(int i=0;i<pricesColNames.length ;i++) {
			fmla = fmla + " + " +logPrefix+pricesColNames[i];
		}
		for(int i=0;i<logicalPromoColNames.length;i++)
			fmla = fmla + " + " + logicalPromoColNames[i];
		return fmla;
	}
}
