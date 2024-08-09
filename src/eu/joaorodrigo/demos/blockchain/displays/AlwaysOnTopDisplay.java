package eu.joaorodrigo.demos.blockchain.displays;

import javax.swing.*;
import java.awt.*;

public class AlwaysOnTopDisplay {
    private static JLabel lastValueLabel = new JLabel("0", SwingConstants.CENTER);
    private static JLabel lastBlockLabel = new JLabel("LastBlockId: x", SwingConstants.CENTER);

    public static void setup() {
        JFrame frame = new JFrame("Blockchain");
        frame.setAlwaysOnTop(true);
        frame.setLayout(new FlowLayout());
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.decode("#121212"));
        frame.setVisible(true);
        frame.add(lastValueLabel);
        frame.setSize(120,60);
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

        frame.add(lastBlockLabel);
    }

    public static void updateLastValue(int value) {
        lastValueLabel.setText(value + "");
    }

    public static void updateLastBlockId(long id) {
        lastBlockLabel.setText("LastBlockId: " + id);
    }
}
