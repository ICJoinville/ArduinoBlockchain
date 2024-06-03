package eu.joaorodrigo.demos.blockchain.account;

import java.sql.SQLException;
import java.util.ArrayList;

import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;

public class AccountManager {
	protected static final ArrayList<Account> ACCOUNTS = new ArrayList<Account>();
	
	public static Account createNewUser(String name) {
		Account account = new Account(name);
		
		try {
			DatabaseInitializer.userDao.createIfNotExists(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return account;
	}
	
	public static boolean doesUserExists(String name) {
		try {
			return DatabaseInitializer.userDao.idExists(name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
