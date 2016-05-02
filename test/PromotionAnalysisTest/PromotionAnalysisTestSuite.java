package PromotionAnalysisTest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

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
		
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		
		for (CSVRecord record : records) {
		    Transaction t = new Transaction(tschema);
		    
		}	
		rconn.close();
		
	}
}
