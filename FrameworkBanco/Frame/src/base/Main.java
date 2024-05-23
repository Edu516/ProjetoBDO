package base;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Database Diagram Builder");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            DatabaseDiagram diagram = new DatabaseDiagram();
            frame.add(diagram);

            frame.setVisible(true);
        });
    }
}
