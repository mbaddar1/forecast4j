package TransactionsTest;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import data.Transaction;
import data.TransactionSchema;
import data.TransactionsTable;
import data.r.RTransactionsTable;

public class TransactionTestSuite {
	
	@Test
	public void testTransactionAddField() {
		TransactionSchema tschema = new TransactionSchema();
		tschema.addField("Week", Integer.class);
		tschema.addField("Product_A_Quantity", Double.class);
		tschema.addField("Product_A_Price", Double.class);
		tschema.addField("Product_B_Price", Double.class);
		tschema.addField("Promotion_1", Boolean.class);
		tschema.addField("Promotion_2", Boolean.class);
		
		Transaction trans = new Transaction(tschema);
		/**
		 * Week	Product_A_Quantity	Product_A_Price	Product_B_Price	Promotion_1	Promotion_2
			1			70					7.50		7.00			0			0
		 */
		Assert.assertTrue(trans.addField("Week", 1));
		Assert.assertTrue(trans.addField("Product_A_Quantity", 70.0));
		Assert.assertTrue(trans.addField("Product_A_Price", 7.50));
		Assert.assertTrue(trans.addField("Product_B_Price", 7.00));
		Assert.assertTrue(trans.addField("Promotion_1", false));
		Assert.assertTrue(trans.addField("Promotion_2", false));
		
		Assert.assertEquals(1, trans.getField("Week"));
		Assert.assertEquals(Integer.class, trans.getField("Week").getClass());
		Assert.assertEquals(70.0, trans.getField("Product_A_Quantity"));
		Assert.assertEquals(Double.class, trans.getField("Product_A_Quantity").getClass());
		Assert.assertEquals(7.5, trans.getField("Product_A_Price"));
		Assert.assertEquals(Double.class, trans.getField("Product_A_Price").getClass());
	}
	
	@Test
	public void testRTransactionTableAddTrans() throws RserveException {
		RConnection rconn = new RConnection();
		
		TransactionSchema tschema = new TransactionSchema();
		tschema.addField("Week", Integer.class);
		tschema.addField("Product_A_Quantity", Double.class);
		tschema.addField("Product_A_Price", Double.class);
		tschema.addField("Product_B_Price", Double.class);
		tschema.addField("Promotion_1", Double.class);
		tschema.addField("Promotion_2", Double.class);
		
		Transaction trans = new Transaction(tschema);
		
		trans.addField("Week", 1);
		trans.addField("Product_A_Quantity", 70.0);
		trans.addField("Product_A_Price", 7.50);
		trans.addField("Product_B_Price", 7.00);
		trans.addField("Promotion_1", 0.0);
		trans.addField("Promotion_2", 0.0);
		
		TransactionsTable ttable = new RTransactionsTable(tschema, "RetailTransactions", rconn);
		
		ttable.addTransaction(trans);
		ttable.addTransaction(trans);
		ttable.display();
		rconn.close();
	}
}

