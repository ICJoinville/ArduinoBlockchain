package eu.joaorodrigo.demos.blockchain.database;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import eu.joaorodrigo.demos.blockchain.Block;
import eu.joaorodrigo.demos.blockchain.Transaction;
import eu.joaorodrigo.demos.blockchain.account.Account;

public class DatabaseInitializer {
	
	protected static String DATABASE_URL = "jdbc:mysql://localhost/joaorodrigo_chaintracer";
	
	public static Dao<Account, String> userDao;
	public static Dao<Block, Integer> blockDao;
	public static Dao<Transaction, Integer> transactionDao;


	
	public static void setup() {
		ConnectionSource connectionSource = null;
		
		try {
			connectionSource = new JdbcConnectionSource(DATABASE_URL, "root", "root");
			userDao = DaoManager.createDao(connectionSource, Account.class);
			blockDao = DaoManager.createDao(connectionSource, Block.class);
			transactionDao = DaoManager.createDao(connectionSource, Transaction.class);
			
			TableUtils.createTableIfNotExists(connectionSource, Account.class);
			TableUtils.createTableIfNotExists(connectionSource, Block.class);
			TableUtils.createTableIfNotExists(connectionSource, Transaction.class);
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
