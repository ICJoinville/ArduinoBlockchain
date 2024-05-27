package eu.joaorodrigo.demos.blockchain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Block {
	
	private static ArrayList<Block> BLOCKS = new ArrayList<>();
	
	public static void printAllBlocks() {
		for(Block b : BLOCKS) {
			b.print();
			System.out.println();
			System.out.println("--------------");

		}
	}
	
	public static boolean validate() {
		
		HashMap<Account, Double> moneyChange = new HashMap<Account, Double>();
		
		System.out.println("");
		System.out.println("--------------");
		System.out.println("INICIANDO VALIDAÇÃO (" + BLOCKS.size() + " blocos)");
		System.out.println("--------------");
		int lastIndex = BLOCKS.size();
		int currentIndex = lastIndex;
		
		boolean success = true;
		
		while(currentIndex > 0) {
			--currentIndex;
			Block b = BLOCKS.get(currentIndex);
			System.out.print("\nValidando bloco " + b.getId() + ": ");
			boolean valid = b.hash == Arrays.hashCode(new int[] {Arrays.hashCode(b.getTransactions()), b.getPreviousHash()});
			System.out.print(valid);
			
			if(!valid) {
				success = false;
				break;
			}
		
		System.out.println("");
		
		if(success) {
			System.out.println("------------------");
			System.out.println("VALIDAÇÃO SUCESSO!");
			System.out.println("------------------");
			System.out.println("");
		}else {
			System.out.println("------------------");
			System.out.println("VALIDAÇÃO FALHOU NO BLOCO " + currentIndex);
			System.out.println("------------------");
			System.exit(0);
		}
		}
		return success;
	}
	
	private int id;
	private Transaction[] transactions;
	private Block previousBlock;
	private boolean isGenesis = false;
	
	private int hash;
	
	public Block(int id, Transaction[] transactions, Block previousBlock) {
		this.id = id;
		this.transactions = transactions;
		this.previousBlock = previousBlock;
		this.hash = Arrays.hashCode(new int[] {Arrays.hashCode(transactions), getPreviousHash()});
	
		BLOCKS.add(this);
	}
	
	public Block(int id, Transaction[] transactions, boolean isGenesis) {
		if(isGenesis) {
			this.id = id;
			this.transactions = transactions;
			this.hash = Arrays.hashCode(new int[] {Arrays.hashCode(transactions), 0});
			
			BLOCKS.add(this);
			this.isGenesis = true;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Block getPreviousBlock() {
		return previousBlock;
	}
	
	public int getPreviousHash() {
		return previousBlock == null ? 0 : previousBlock.getHash();
	}
	
	public int getHash() {
		return hash; 
	}
	
	public Transaction[] getTransactions() {
		return transactions;
	}
	
	public void print() {
		System.out.println("Bloco " + id + ":");
		System.out.println(" - Transações (total: "+ transactions.length +"):");
		
		
		int i = 0;
		
		for(Transaction t : transactions) {
			System.out.println("");
			System.out.println(" Transação " + i + ":");
			System.out.println("  - Data: " + Instant.ofEpochMilli(t.getTimestamp()).atZone(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());;
			System.out.println("  - Dono: " + t.getOwner());
			System.out.println("  - Valor Novo: " + t.getAmount());
			
			i++;
		}
		
	}
}
