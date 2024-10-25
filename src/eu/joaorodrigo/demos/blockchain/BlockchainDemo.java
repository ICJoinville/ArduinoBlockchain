package eu.joaorodrigo.demos.blockchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.j256.ormlite.dao.ForeignCollection;

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
	public static int lastValue;
	public static List<Transaction> pendingTransactions = new ArrayList<>();
	private static final boolean debug = false;
	private static int debugMaxValue = Integer.MIN_VALUE;
	private static int debugMinValue = Integer.MAX_VALUE;
	private static int debugTimer = 30 * 60;

	private static short baud = 9600;
	public static SerialPort comPort;



	public static void main(String[] args) throws IOException, SerialPortException, SQLException {
		Report.loadLogFile();

		AlwaysOnTopDisplay.setup(debug);
		
		Report.log("Aguardando conexão serial.");
		while(SerialPortList.getPortNames().length == 0);
		comPort = new SerialPort(SerialPortList.getPortNames()[0]);
		comPort.openPort();
		comPort.setParams(BAUDRATE_9600,  DATABITS_8, STOPBITS_1, PARITY_NONE);
		
		int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
		comPort.setEventsMask(mask);
		comPort.addEventListener(new SerialReadEventHandler());
		
		lastBlockId = DatabaseInitializer.blockDao.countOf();
		System.out.println(lastBlockId + " transações encontradas.");
		
		if(lastBlockId == 0) lastBlock = new Block(0, true);
		else lastBlock = DatabaseInitializer.blockDao.queryForId((int) lastBlockId);

		if(debug && debugTimer < 0) {
			new Thread(() -> {
				while(debugTimer > 0) {
					debugTimer--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				try {
					System.out.println("Min: " + debugMinValue + " Max: " + debugMaxValue + " Qtd. Transações: " + DatabaseInitializer.transactionDao.countOf() + " Qtd. Blocos: " + DatabaseInitializer.blockDao.countOf());
				} catch (SQLException e) {
					System.out.println("Min: " + debugMinValue + " Max: " + debugMaxValue);
					throw new RuntimeException(e);
				}
				System.exit(0);
			}).start();
		}
		
		Thread thread = new Thread(() -> {
			while(true) {
				Block block = new Block((int) lastBlockId + 1, lastBlock);
				Report.log(LocalDateTime.now() + " : Applying new block {" + block.getId() + "} with " + pendingTransactions.size() + " transactions." + (debug && debugTimer != 0 ? " transactions. (t = " + debugTimer + "s remaining)" : ""));
				
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
	
	public static void sendNewValue(int b) {
		if(lastValue == b) return;
		if(debug) {
			if(b > debugMaxValue) debugMaxValue = b;
			else if(b < debugMinValue) debugMinValue = b;

			AlwaysOnTopDisplay.updateDebug(debugMinValue, debugMaxValue);
		}
		AlwaysOnTopDisplay.updateLastValue(b);
		pendingTransactions.add(Transaction.createTransaction(local, b));
	}

}
