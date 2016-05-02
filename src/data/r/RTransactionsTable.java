package data.r;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import data.Transaction;
import data.TransactionSchema;
import data.TransactionsTable;

public class RTransactionsTable extends TransactionsTable{
	RConnection rconn = null;
	public RConnection getRconn() {
		return rconn;
	}

	ArrayList<Transaction> transactions;
	
	/**
	 * 
	 * @param transSchema Schema of the transaction
	 * @param name name of the transactions table
	 * @param rconn
	 */
	public RTransactionsTable(TransactionSchema transSchema,String name,RConnection rconn) {
		super(transSchema,name);
		this.rconn = rconn;
		this.transactions = new ArrayList<Transaction>();
		if(this.rconn == null)
			throw new NullPointerException("Given R connection is null");
		if(!this.rconn.isConnected())
			throw new IllegalArgumentException("Given R connections is not connected or closed");
	}
	
	/**
	 * @param transactions list of transactions to add
	 */
	@Override
	public boolean addTransactions(List<Transaction> transactions) {
		
		return false;
	}
	
	/**
	 * @param transaction transaction to add to the transactions
	 */
	@Override
	public boolean addTransaction(Transaction transaction) {
		//Check if R dataframe of this table exists in workspace
		String expr = "exists(x = \""+name+"\")";
		REXP ret = null;
		expr = "try(expr = {"+expr+"})";
		try {
			 ret = rconn.eval(expr);
			 if(ret.isString())
				 throw new RserveException(rconn, "Error in executing : "+expr+" =>"+ret.asString());
			 if(!ret.isLogical())
				 throw new RserveException(rconn, "Retun type of "+expr+" is not logical");
			 boolean exists = ((REXPLogical)ret).isTRUE()[0];
			 if(exists) {
				 String tmpTransactionName = "tmpTransactionList";
				 createListFromTransaction(transaction, tmpTransactionName, this.rconn);
				 expr = this.name + " = rbind("+this.name+","+tmpTransactionName+")";
				 expr = "try(expr = {"+expr+"})";
				 ret = rconn.eval(expr);
				 if(ret.isString())
					 throw new RserveException(rconn, "Error in executing : "+expr+" =>"+ret.asString());
			 }
			 else {
				 createTransactionRDataFrame(name, transaction, this.rconn);
			 }
			 
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean createListFromTransaction(Transaction trans,String listName , RConnection rconn) throws Exception {
		RList l = new RList();
		for(String fName : trans.getFieldsNames()) {
			Object fieldValue = trans.getField(fName);
			if(fieldValue.getClass() == Integer.class) {
				l.put(fName, new REXPInteger((Integer)fieldValue));
			}
			else if(fieldValue.getClass() == Double.class) {
				l.put(fName, new REXPDouble((Double)fieldValue));
			}
			else if(fieldValue.getClass() == String.class) {
				l.put(fName, new REXPString((String)fieldValue));
			}
			
			else {
				throw new IllegalArgumentException("Unsupported field type :"+fieldValue.getClass());
			}
		}
		rconn.assign(listName, new REXPList(l));
		//convert pair list to list
		String expr = listName + " = as.list("+listName+")";
		expr = "try(expr = {"+expr+"})";
		REXP r = rconn.eval(expr);
		if(r.isString())
			throw new IllegalArgumentException("Error in evaluating expression : "+expr+" => "+r.asString());
		return true;
		
	}
	private boolean createTransactionRDataFrame(String dataFrameName , Transaction firstTransaction,RConnection rconn) 
			throws Exception {
		String firstTransactionListName = "firstTransactionListName";
		boolean r = createListFromTransaction(firstTransaction, firstTransactionListName, this.rconn);
		String expr = "";
		if(r) {
			expr = dataFrameName + " = as.data.frame(x = "+firstTransactionListName+")";
			expr = "try(expr = {"+expr+"})";
			REXP rxp = rconn.eval(expr);
			if(rxp.isString()) {
				throw new RserveException(rconn, "Error in executing : "+expr+" =>"+rxp.asString());
			}
		}
		else {
			throw new Exception("Failed to create a list for the first transaction.");
		}
		return true;
	}
	
	@Override
	public void display() {
		String expr = "capture.output(print("+this.name+"))"; 
		expr = "try(expr = {"+expr+"})";
		try {
			REXP rxp = rconn.eval(expr);
			String[] output = rxp.asStrings();
			for(String s : output) {
				System.out.println(s);
			}
			
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
