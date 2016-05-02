package TransactionsSchema;

import org.junit.Assert;
import org.junit.Test;

import data.TransactionSchema;

public class TransactionSchemaTest {
	
	@Test
	public void testAddField() {
		/*Use dataset in the paper
		 * Paper 425-2013 Price- and Cross-Price Elasticity Estimation using SAS in Table 1 page 6
		 * */
		TransactionSchema tschema = new TransactionSchema();
		tschema.addField("Week", Integer.class);
		tschema.addField("Product_A_Qty", Double.class);
		Assert.assertEquals("Assert weel col ", Integer.class, tschema.getFieldType("Week"));
		Assert.assertEquals("Assert Product A Qty col ", Double.class, tschema.getFieldType("Product_A_Qty"));
		Assert.assertTrue(tschema.hasField("Week"));
		Assert.assertTrue(tschema.hasField("Product_A_Qty"));
		Assert.assertFalse(tschema.hasField("Product_A Qty"));
	}
	
}
