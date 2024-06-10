package eu.joaorodrigo.demos.blockchain;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.joaorodrigo.demos.blockchain.account.Account;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;

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
	
	public void setBlock(Block b) {
		block = b;
	}
	
	public static List<Transaction> getTransactionByBlockId(int id) {
		try {
			return DatabaseInitializer.transactionDao.queryForEq("block_id", id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
