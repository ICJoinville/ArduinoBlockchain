package eu.joaorodrigo.demos.blockchain.account;

import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import eu.joaorodrigo.demos.blockchain.Report;

@DatabaseTable(tableName = "accounts")
public class Account {
	
	public static void printAccounts() {
		for(Account a : AccountManager.ACCOUNTS) {
			Report.log(a.getName() + " ("+a+"): " + a.getLastValue());
		}
	}
	
	@DatabaseField(id = true)
	private String name;
	
	@DatabaseField(columnName = "last_value")
	private double lastValue;
	
	protected Account(String name) {
		this.name = name;
		AccountManager.ACCOUNTS.add(this);
	}
	
	public String getName() {
		return name;
	}
	
	protected double getLastValue() {
		return lastValue;
	}
	
	protected void setValue(double newValue) {
		this.lastValue = newValue;
	}
	
	public Account() {}
}
