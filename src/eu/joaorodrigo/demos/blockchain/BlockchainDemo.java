package eu.joaorodrigo.demos.blockchain;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eu.joaorodrigo.demos.blockchain.account.Account;
import eu.joaorodrigo.demos.blockchain.account.AccountManager;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;
import eu.joaorodrigo.demos.blockchain.displays.AlwaysOnTopDisplay;
import eu.joaorodrigo.demos.blockchain.integrations.MQTTSender;
import jssc.SerialPort;

public class BlockchainDemo {
	
	static {
		DatabaseInitializer.setup();
		local = AccountManager.createNewUser("node-1");
		new MQTTSender();
	}
	
	private static Block lastBlock;
	private static long lastBlockId;
	private static Account local;
	public static String lastValue;
	public static List<Transaction> pendingTransactions = new ArrayList<>();

	private static short baud = (short) 115200;
	public static SerialPort comPort;

	public static AlwaysOnTopDisplay display;

	public static void main(String[] args) throws IOException, SQLException {
		Report.loadLogFile();

		if(!GraphicsEnvironment.isHeadless()) {
			display = new AlwaysOnTopDisplay();
			display.setup();
		}


		lastBlockId = DatabaseInitializer.blockDao.countOf();
		System.out.println(lastBlockId + " transações encontradas.");
		
		if(lastBlockId == 0) lastBlock = new Block(0, true);
		else lastBlock = DatabaseInitializer.blockDao.queryForId((int) lastBlockId);
		
		Thread thread = new Thread(() -> {
			while(true) {
				Block block = new Block((int) lastBlockId + 1, lastBlock);
				Report.log(LocalDateTime.now() + " : Applying new block {" + block.getId() + "} with " + pendingTransactions.size() + " transactions.");
				
				try {
					pendingTransactions.forEach((t) -> t.setBlock(block));
					DatabaseInitializer.transactionDao.create(pendingTransactions);
					DatabaseInitializer.blockDao.create(block);
					if(!GraphicsEnvironment.isHeadless())
						display.updateTransactionsAmount();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				pendingTransactions.clear();
				
				lastBlock = block;
				lastBlockId = block.getId();
				if(display != null)
					display.updateLastBlockId(lastBlockId);
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
		
		Scanner in = new Scanner(System.in);
		Thread report = new Thread(() -> {
			while(true) {
				if(in.hasNextLine()) {
					Report.generate();
					try {
						Report.logFileWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);
				}
			}
		});
		report.start();
	}
	
	public static void sendNewValue(String b) {
		if(lastValue != null && lastValue.equals(b)) return;
		if(display != null) display.updateLastValue(b);
		pendingTransactions.add(Transaction.createTransaction(local, b));
	}

}
