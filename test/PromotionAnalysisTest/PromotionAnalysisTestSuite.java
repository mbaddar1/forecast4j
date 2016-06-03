package PromotionAnalysisTest;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import promotions.PromotionAnalysis;
import promotions.analyzer.PromotionAnalyzer;
import promotions.analyzer.r.RPromotionAnalyzer;
import rconfig.RConfig;
import data.Transaction;
import data.TransactionSchema;
import data.TransactionsTable;
import data.r.RTransactionsTable;

public class PromotionAnalysisTestSuite {
	@Test
	public void testRPromotionAnalyzeDataSet1() throws RserveException, IOException
	{
		/**
		 * We use the data-set in the paper
		 * Price- and Cross-Price Elasticity Estimation using SAS
		 * http://support.sas.com/resources/papers/proceedings13/425-2013.pdf
		 */
		//Week	Product_A_Quantity	Product_A_Price	Product_B_Price	Promotion_1	Promotion_2
		TransactionSchema tschema = new TransactionSchema();
		tschema.addField("Week", Integer.class);
		tschema.addField("Product_A_Quantity", Double.class);
		tschema.addField("Product_A_Price", Double.class);
		tschema.addField("Product_B_Price", Double.class);
		tschema.addField("Promotion_1", Integer.class);
		tschema.addField("Promotion_2", Integer.class);
		//create R connection
		RConnection rconn = new RConnection();
		//create transaction table
		TransactionsTable ttable = new RTransactionsTable(tschema, "ttable", rconn);
		//load the dataset
		Reader in = new FileReader("./datasets/PromoAnalysisDataSet1.csv");
		
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		for (CSVRecord record : records) {
		    Transaction t = new Transaction(tschema);
		    t.addField("Week", Integer.valueOf(record.get("Week")));
		    t.addField("Product_A_Quantity",Double.valueOf(record.get("Product_A_Quantity")));
		    t.addField("Product_A_Price", Double.valueOf(record.get("Product_A_Price")));
		    t.addField("Product_B_Price",Double.valueOf(record.get("Product_B_Price")));
		    t.addField("Promotion_1", Integer.valueOf(record.get("Promotion_1")));
		    t.addField("Promotion_2", Integer.valueOf(record.get("Promotion_2")));
		    ttable.addTransaction(t);
		}
		ttable.display();
		
		//create promotional analyzer
		try 
		{
			RConfig rconf = new RConfig();
			String rScriptsPath = "/home/baddar/git/forecast4j/R/scripts";
			String promotionAnalysisRScriptName = "PromotionAnalysis.R";
			rconf.setrScriptsPath(rScriptsPath);
			PromotionAnalyzer panalyzer = new RPromotionAnalyzer(rconn, rconf,promotionAnalysisRScriptName);
			PromotionAnalysis pAnalysis = panalyzer.analyze(ttable, "Product_A_Quantity", 
					new String[]{"Product_A_Price","Product_B_Price"}, 
					new String[]{"Promotion_1","Promotion_2"});
			/*
			 * Results obtained by script "PromotionAnalysis.R"
			 Coefficients:
                     Estimate Std. Error t value Pr(>|t|)   
				(Intercept)          7.648597   2.735837   2.796  0.01083 * 
				log_Product_A_Price -3.382426   0.987283  -3.426  0.00254 **
				log_Product_B_Price  1.724122   0.565976   3.046  0.00614 **
				Promotion_1         -0.034913   0.092532  -0.377  0.70973   
				Promotion_2          0.009324   0.037920   0.246  0.80816  
			 * */
			double delta = 0.01;
			Assert.assertEquals("Asserting log_product_A_price", -3.382426
					,pAnalysis.getPromotionEffect("log_Product_A_Price"), delta*Math.abs(-3.382426));
			Assert.assertEquals("Asserting log_product_B_price",1.724122
					,pAnalysis.getPromotionEffect("log_Product_B_Price"),delta*Math.abs(1.724122));
			/**
			 * For promotion1 and 2 coeff , p value is high (>0.1) so their coeff are set 
			 * to 0 , based on the logic in inside the script
			 * TODO make the thr of p-value a parameter
			 */
			Assert.assertEquals("Asserting Promotion_1",0
					,pAnalysis.getPromotionEffect("Promotion_1"),1e-6);
			Assert.assertEquals("Asserting Promotion_2",0
					,pAnalysis.getPromotionEffect("Promotion_2"),1e-6);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rconn.close();
		
	}
}
