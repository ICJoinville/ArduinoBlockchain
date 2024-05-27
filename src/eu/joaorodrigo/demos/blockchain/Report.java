package eu.joaorodrigo.demos.blockchain;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Report {
	public static void generate() {
		System.out.println("--------------");
		System.out.println("----REPORT----");
		System.out.println(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
		System.out.println("--------------");
		System.out.println("");
		System.out.println("--------------");
		System.out.println("--BLOCKCHAIN--");
		System.out.println("----Saldos----");
		System.out.println("--------------");
		Account.printAccounts();
		System.out.println();
		System.out.println("--------------");
		System.out.println("----Blocos----");
		System.out.println("--------------");
		Block.printAllBlocks();
		Block.validate();
	}
}
