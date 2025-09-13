package eu.joaorodrigo.demos.blockchain.displays;

import eu.joaorodrigo.demos.blockchain.Simulation;
import eu.joaorodrigo.demos.blockchain.database.DatabaseInitializer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AlwaysOnTopDisplay {
    private JLabel lastValueLabel = new JLabel("0", SwingConstants.CENTER);
    private JLabel lastBlockLabel = new JLabel("LastBlockId: x", SwingConstants.CENTER);
    private JLabel transactionsLabel = new JLabel("Transactions: x", SwingConstants.CENTER);
    private JFrame frame = new JFrame("Blockchain");

    public void setup() {
        frame.setAlwaysOnTop(true);
        frame.setLayout(new FlowLayout());
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.decode("#121212"));
        frame.setVisible(true);
        frame.add(lastValueLabel);
        frame.setSize(840,80);
        frame.setResizable(false);

        JLabel title = new JLabel("ChainTracer", SwingConstants.CENTER);
        title.setSize(40,20);
        title.setVerticalAlignment(SwingConstants.TOP);
        title.setForeground(Color.white);
        frame.add(title);

        lastValueLabel.setForeground(Color.white);
        lastValueLabel.setFont(new Font(lastValueLabel.getFont().getName(), Font.PLAIN, 8));

        lastBlockLabel.setForeground(Color.white);
        lastBlockLabel.setFont(new Font(lastBlockLabel.getFont().getName(), Font.PLAIN, 10));

        transactionsLabel.setForeground(Color.white);
        transactionsLabel.setFont(new Font(transactionsLabel.getFont().getName(), Font.PLAIN, 10));

        frame.add(lastBlockLabel);
        frame.add(transactionsLabel);
    }

    public void updateLastValue(String value) {
        lastValueLabel.setText(value);
    }

    public void updateLastBlockId(long id) {
        lastBlockLabel.setText("LastBlockId: " + id);
    }
    public void updateTransactionsAmount() {
        try {
            transactionsLabel.setText("Transactions: " + DatabaseInitializer.transactionDao.countOf());
        } catch (SQLException e) {
            transactionsLabel.setText("Transactions: " + "SQLException");
            e.printStackTrace();
        }
    }
}
