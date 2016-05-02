package data;

import java.util.List;


public abstract class TransactionsTable {
	protected TransactionSchema transSchema;
	protected String name;
	public TransactionsTable(TransactionSchema transSchema,String name) {
		super();
		this.transSchema = transSchema;
		this.name = name;
	}
	public TransactionSchema getTransSchema() {
		return transSchema;
	}

	public void setTransSchema(TransactionSchema transSchema) {
		this.transSchema = transSchema;
	}
	public String getName() {
		return this.name;
	}
	public abstract boolean  addTransaction(Transaction transaction);
	public abstract boolean  addTransactions(List<Transaction> transactions);
	public abstract void display();
}
