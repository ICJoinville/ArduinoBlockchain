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

import eu.joaorodrigo.demos.blockchain.account.Account;
import eu.joaorodrigo.demos.blockchain.account.AccountManager;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;
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
	
	private static short baud = 9600;
	public static SerialPort comPort;
	private static JLabel lastValueLabel = new JLabel("0");

	public static void main(String[] args) throws IOException, SerialPortException, SQLException {
		Report.loadLogFile();
		
		JFrame frame = new JFrame("Blockchain");
		frame.setVisible(true);
		frame.add(lastValueLabel);
		frame.setSize(80,60);
		
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
		
		lastBlock = new Block((int) lastBlockId, new ArrayList<Transaction>(), null);
		lastBlockId++;
		
		Thread thread = new Thread(() -> {
			while(true) {
				Block block = new Block((int) lastBlockId + 1, new ArrayList<Transaction>(pendingTransactions), lastBlock);
				Report.log(LocalDateTime.now() + " : Applying new block {" + block.getId() + "} with " + pendingTransactions.size() + " transactions.");
				
				try {
					DatabaseInitializer.blockDao.create(block);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				pendingTransactions.clear();
				
				lastBlock = block;
				lastBlockId = block.getId();
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
		lastValueLabel.setText(b + "");
		pendingTransactions.add(Transaction.createTransaction(local, b));
	}

}
