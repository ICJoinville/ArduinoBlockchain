package eu.joaorodrigo.demos.blockchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import static jssc.SerialPort.*;

public class BlockchainDemo {
	
	private static Block lastBlock;
	private static long lastBlockId;
	private static Account local = new Account("Arduino");
	public static int lastValue;
	public static List<Transaction> pendingTransactions = new ArrayList<>();
	
	private static short baud = 9600;
	public static SerialPort comPort = new SerialPort("COM3");
	private static JLabel lastValueLabel = new JLabel("0");

	public static void main(String[] args) throws IOException, SerialPortException {
		
		JFrame frame = new JFrame("Blockchain");
		frame.setVisible(true);
		frame.add(lastValueLabel);
		frame.setSize(80,60);
		
		System.out.println("Aguardando conexão serial.");
		while(SerialPortList.getPortNames().length == 0);
		
		comPort.openPort();
		comPort.setParams(BAUDRATE_9600,  DATABITS_8, STOPBITS_1, PARITY_NONE);
		
		int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
		comPort.setEventsMask(mask);
		comPort.addEventListener(new SerialReadEventHandler());
		lastBlock = new Block(1, new Transaction[] {}, null);
		lastBlockId = 1;
		
		Thread thread = new Thread(() -> {
			while(true) {
				Block block = new Block((int) lastBlockId + 1, pendingTransactions.toArray(new Transaction[] {}), lastBlock);
				System.out.println(LocalDateTime.now() + " : Applying new block {" + block.getId() + "} with " + pendingTransactions.size() + " transactions.");
				pendingTransactions.clear();
				
				lastBlock = block;
				lastBlockId = block.getId();
				try {
					Thread.sleep(30000);
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
