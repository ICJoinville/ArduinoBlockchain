package eu.joaorodrigo.demos.blockchain;

import java.util.ArrayList;

public class Account {
	private static final ArrayList<Account> ACCOUNTS = new ArrayList<Account>();
	
	public static void printAccounts() {
		for(Account a : ACCOUNTS) {
			System.out.println(a.getName() + " ("+a+"): " + a.getLastValue());
		}
	}
	
	private String name;
	private double lastValue;
	
	public Account(String name) {
		this.name = name;
		ACCOUNTS.add(this);
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
}
