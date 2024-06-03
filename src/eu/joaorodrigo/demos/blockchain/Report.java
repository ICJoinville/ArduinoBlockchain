package eu.joaorodrigo.demos.blockchain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import eu.joaorodrigo.demos.blockchain.account.Account;

public class Report {
	
	public static File logFile = new File("blockchain-arduino.log");
	public static FileWriter logFileWriter;
	
	public static void loadLogFile() {
		try {
			if(!logFile.exists()) logFile.createNewFile();
			logFileWriter = new FileWriter(logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void generate() {
		
		log("--------------");
		log("----REPORT----");
		log(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
		log("--------------");
		log("");
		log("--------------");
		log("--BLOCKCHAIN--");
		log("----Saldos----");
		log("--------------");
		Account.printAccounts();
		log("");
		log("--------------");
		log("----Blocos----");
		log("--------------");
		Block.printAllBlocks();
		Block.validate();
		
		
	}
	
	public static void log(String msg) {
		System.out.println(msg);
		try {
			logFileWriter.write(msg + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
