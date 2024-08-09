package eu.joaorodrigo.demos.blockchain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.joaorodrigo.demos.blockchain.account.Account;
import eu.joaorodrigo.demos.blockchain.account.AccountManager;
import eu.joaorodrigo.demos.blockchain.adapters.BlockAdapter;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;
import eu.joaorodrigo.demos.blockchain.displays.AlwaysOnTopDisplay;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Simulation {

    static {
        DatabaseInitializer.setup();
        local = AccountManager.createNewUser("Arduino");
    }

    private static Block lastBlock;
    private static long lastBlockId;
    private static Account local;
    public static int lastValue;
    public static List<Transaction> pendingTransactions = new ArrayList<>();


    public static void main(String[] args) throws IOException, SQLException {
        Report.loadLogFile();
        AlwaysOnTopDisplay.setup();

        Report.log("Aguardando conexão serial.");

        lastBlockId = DatabaseInitializer.blockDao.countOf();
        System.out.println(lastBlockId + " transações encontradas.");

        if(lastBlockId == 0) lastBlock = new Block(0, true);
        else lastBlock = DatabaseInitializer.blockDao.queryForId((int) lastBlockId);

        System.out.println(new GsonBuilder().registerTypeAdapter(Block.class, new BlockAdapter()).create().toJson(lastBlock));

        Thread thread = new Thread(() -> {
            while(true) {
                Block block = new Block((int) lastBlockId + 1, lastBlock);
                Report.log(LocalDateTime.now() + " : Applying new block {" + block.getId() + "} with " + pendingTransactions.size() + " transactions.");

                try {
                    pendingTransactions.forEach((t) -> t.setBlock(block));
                    DatabaseInitializer.transactionDao.create(pendingTransactions);
                    DatabaseInitializer.blockDao.create(block);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
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
        populateWithSimulatedValues();
    }

    public static void sendNewValue(int b) {
        if(lastValue == b) return;
        AlwaysOnTopDisplay.updateLastValue(b);
        pendingTransactions.add(Transaction.createTransaction(local, b));
    }

    public static void populateWithSimulatedValues() {
        // 0-255
        Thread t = new Thread(() -> {
            while(true) {
                int rand = new Random().nextInt(0,255);
                sendNewValue(rand);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
    }

    /*
    camelCase
    PascalCase
    snake_case
    kebab-case
     */

}
