package eu.joaorodrigo.demos.blockchain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import eu.joaorodrigo.demos.blockchain.account.Account;
import eu.joaorodrigo.demos.blockchain.adapters.BlockAdapter;
import eu.joaorodrigo.demos.blockchain.hashing.SHA256;

@DatabaseTable(tableName = "blocks")
public class Block {

	public static Gson blockAdaptedGson = new GsonBuilder().registerTypeAdapter(Block.class, new BlockAdapter()).create();

	private static ArrayList<Block> BLOCKS = new ArrayList<>();
	
	public static void printAllBlocks() {
		for(Block b : BLOCKS) {
			b.print();
			Report.log("");
			Report.log("--------------");

		}
	}
	
	public static boolean validate() {
		
		HashMap<Account, Double> moneyChange = new HashMap<Account, Double>();
		
		Report.log("");
		Report.log("--------------");
		Report.log("INICIANDO VALIDAÇÃO (" + BLOCKS.size() + " blocos)");
		Report.log("--------------");
		int lastIndex = BLOCKS.size();
		int currentIndex = lastIndex;
		
		boolean success = true;
		
		while(currentIndex > 0) {
			--currentIndex;
			Block b = BLOCKS.get(currentIndex);
			System.out.print("\nValidando bloco " + b.getId() + ": ");
			boolean valid = b.hash.equals(SHA256.encode(b));
			System.out.print(valid);
			
			if(!valid) {
				success = false;
				break;
			}
		
		Report.log("");
		
		if(success) {
			Report.log("------------------");
			Report.log("VALIDAÇÃO SUCESSO!");
			Report.log("------------------");
			Report.log("");
		}else {
			Report.log("------------------");
			Report.log("VALIDAÇÃO FALHOU NO BLOCO " + currentIndex);
			Report.log("------------------");
			System.exit(0);
		}
		}
		return success;
	}
	
	@DatabaseField(id = true)
	private int id;
	
	@ForeignCollectionField(columnName = "transactions", eager = false)
	private ForeignCollection<Transaction> transactions;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "previous_block")
	private Block previousBlock;
	private boolean isGenesis = false;
	
	@DatabaseField(columnName = "hash")
	private String hash;
	
	@DatabaseField(columnName = "previous_hash")
	private String previousHash;
	
	public Block(int id, Block previousBlock) {
		this.id = id;
//		this.transactions = transactions;
		this.previousBlock = previousBlock;
		if(this.previousBlock != null) this.previousHash = previousBlock.getHash();
		else this.previousHash = null;
		this.hash = SHA256.encode(this);
	
		BLOCKS.add(this);
	}
	
	public Block(int id, boolean isGenesis) {
		if(isGenesis) {
			this.id = id;
//			this.transactions = transactions;
			this.hash = SHA256.encode(this);
			
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
	
	public String getPreviousHash() {
		return previousBlock == null ? "" : previousBlock.getHash();
	}
	
	public String getHash() {
		return hash; 
	}
	
	public Collection<Transaction> getTransactions() {
		return Transaction.getTransactionByBlockId(getId());
	}
	
	public void print() {
		Report.log("Bloco " + id + ":");
		Report.log(" - Transações (total: "+ getTransactions().size() +"):");
		
		
		int i = 0;
		
		for(Transaction t : getTransactions()) {
			Report.log("");
			Report.log(" Transação " + i + ":");
			Report.log("  - Data: " + Instant.ofEpochMilli(t.getTimestamp()).atZone(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());
			Report.log("  - Dono: " + t.getOwner());
			Report.log("  - Valor Novo: " + t.getData());
			
			i++;
		}
		
	}
	
	public Block() {}
}
