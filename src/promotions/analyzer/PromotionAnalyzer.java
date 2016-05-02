package promotions.analyzer;

import java.io.FileNotFoundException;

import data.TransactionsTable;
import promotions.PromotionAnalysis;

public interface PromotionAnalyzer {
	public PromotionAnalysis analyze(TransactionsTable transactionTable,String qtyColName,
			String[] pricesColNames,String[] logicalPromoColNames) throws Exception;
}
