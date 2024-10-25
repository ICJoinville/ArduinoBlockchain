package eu.joaorodrigo.demos.blockchain.displays;

import eu.joaorodrigo.demos.blockchain.Simulation;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AlwaysOnTopDisplay {
    private static JLabel lastValueLabel = new JLabel("0", SwingConstants.CENTER);
    private static JLabel lastBlockLabel = new JLabel("LastBlockId: x", SwingConstants.CENTER);
    private static JLabel transactionsLabel = new JLabel("Transactions: x", SwingConstants.CENTER);
    private static JLabel debugLabel = new JLabel("Min: x / Max: y", SwingConstants.CENTER);
    private static JFrame frame = new JFrame("Blockchain");

    public static void setup(boolean debug) {
        frame.setAlwaysOnTop(true);
        frame.setLayout(new FlowLayout());
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.decode("#121212"));
        frame.setVisible(true);
        frame.add(lastValueLabel);
        frame.setSize(120,80);
        frame.setResizable(false);

        JLabel title = new JLabel("ChainTracer", SwingConstants.CENTER);
        title.setSize(40,20);
        title.setVerticalAlignment(SwingConstants.TOP);
        title.setForeground(Color.white);
        frame.add(title);

        lastValueLabel.setForeground(Color.white);
        lastValueLabel.setFont(new Font(lastValueLabel.getFont().getName(), Font.PLAIN, 20));

        lastBlockLabel.setForeground(Color.white);
        lastBlockLabel.setFont(new Font(lastBlockLabel.getFont().getName(), Font.PLAIN, 10));

        transactionsLabel.setForeground(Color.white);
        transactionsLabel.setFont(new Font(transactionsLabel.getFont().getName(), Font.PLAIN, 10));

        debugLabel.setForeground(Color.white);
        debugLabel.setFont(new Font(debugLabel.getFont().getName(), Font.PLAIN, 10));

        frame.add(lastBlockLabel);
        frame.add(transactionsLabel);
        if(debug) frame.add(debugLabel);
    }

    public static void updateLastValue(int value) {
        lastValueLabel.setText(value + "");
    }

    public static void updateLastBlockId(long id) {
        lastBlockLabel.setText("LastBlockId: " + id);
    }
    public static void updateTransactionsAmount() {
        try {
            transactionsLabel.setText("Transactions: " + DatabaseInitializer.transactionDao.countOf());
        } catch (SQLException e) {
            transactionsLabel.setText("Transactions: " + "SQLException");
            e.printStackTrace();
        }
    }

    public static void updateDebug(int min, int max) {
        if(frame.getHeight() == 80) frame.setSize(frame.getWidth(), frame.getHeight() + 20);
        debugLabel.setText("Min: " + (min == Integer.MAX_VALUE ? "x" : min) + " / Max: " + (max == Integer.MIN_VALUE ? "y" : max));
    }
}
