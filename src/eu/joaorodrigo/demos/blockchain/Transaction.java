package eu.joaorodrigo.demos.blockchain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.joaorodrigo.demos.blockchain.account.Account;

@DatabaseTable(tableName = "transactions")
public class Transaction {
	
	@DatabaseField(columnName = "id", generatedId = true)
	private int id;
	
	@DatabaseField(columnName = "account", foreignAutoRefresh = true, foreign = true)
	private Account who;
	
	@DatabaseField(columnName = "timestamp")
	private long millis;
	
	@DatabaseField(columnName = "value")
	private double amount;
	
	@DatabaseField(foreign = true)
	private Block block;
	
	public Transaction(Account who, double amount) {
		this.who = who;
		this.amount = amount;
		this.millis = System.currentTimeMillis();
	}
	
	public Transaction () {}
	
	public static Transaction createTransaction(Account who, double amount) {
		return new Transaction(who, amount);
	}

	public Account getOwner() {
		return who;
	}

	public double getAmount() {
		return amount;
	}
	
	public long getTimestamp() {
		return millis;
	}
	
	
}
