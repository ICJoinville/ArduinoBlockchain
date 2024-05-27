package eu.joaorodrigo.demos.blockchain;

public class Transaction {
	private Account who;
	private long millis;
	private double amount;
	
	public Transaction(Account who, double amount) {
		this.who = who;
		this.amount = amount;
		this.millis = System.currentTimeMillis();
	}
	
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
