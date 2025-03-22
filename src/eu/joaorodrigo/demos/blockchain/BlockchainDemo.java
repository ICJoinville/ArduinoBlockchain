package eu.joaorodrigo.demos.blockchain;

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
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import static jssc.SerialPort.*;

public class BlockchainDemo {
	
	static {
		DatabaseInitializer.setup();
		local = AccountManager.createNewUser("Arduino");
	}
	
	private static Block lastBlock;
	private static long lastBlockId;
	private static Account local;
	public static String lastValue;
	public static List<Transaction> pendingTransactions = new ArrayList<>();

	private static short baud = (short) 115200;
	public static SerialPort comPort;



	public static void main(String[] args) throws IOException, SerialPortException, SQLException {
		Report.loadLogFile();

		AlwaysOnTopDisplay.setup();
		
		Report.log("Aguardando conexão serial.");
		while(SerialPortList.getPortNames().length == 0);
		comPort = new SerialPort(SerialPortList.getPortNames()[0]);
		comPort.openPort();
		comPort.setParams(BAUDRATE_115200,  DATABITS_8, STOPBITS_1, PARITY_NONE);
		
		int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
		comPort.setEventsMask(mask);
		comPort.addEventListener(new SerialReadEventHandler());
		
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
					AlwaysOnTopDisplay.updateTransactionsAmount();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				pendingTransactions.clear();
				
				lastBlock = block;
				lastBlockId = block.getId();
				AlwaysOnTopDisplay.updateLastBlockId(lastBlockId);
				try {
					Thread.sleep(5000);
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
		AlwaysOnTopDisplay.updateLastValue(b);
		pendingTransactions.add(Transaction.createTransaction(local, b));
	}

}
