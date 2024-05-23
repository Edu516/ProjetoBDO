package base;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DatabaseDiagram extends JPanel {
    private JPanel palette;
    private JTextField inputField;
    private JButton addButton;
    private JPanel selectedTable;

    public DatabaseDiagram() {
        setLayout(null); // Using absolute layout for manual positioning
        setBackground(Color.LIGHT_GRAY);

        // Create palette panel
        palette = new JPanel();
        palette.setLayout(new BoxLayout(palette, BoxLayout.Y_AXIS));
        palette.setBorder(BorderFactory.createTitledBorder("Palette"));
        palette.setBounds(10, 10, 100, 200);

        // Create draggable table label
        JLabel tableLabel = new JLabel("Table");
        tableLabel.setTransferHandler(new ValueExportTransferHandler("table"));
        tableLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.COPY);
            }
        });

        palette.add(tableLabel);
        add(palette);

        // Create input field for adding rows
        inputField = new JTextField();
        inputField.setBounds(10, 220, 200, 30);
        add(inputField);

        // Create add button for adding rows
        addButton = new JButton("Add Row");
        addButton.setBounds(220, 220, 100, 30);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRowToSelectedTable();
            }
        });
        add(addButton);

        // Set drop target for the main panel
        new DropTarget(this, new TableDropTargetListener());
    }

    private void addTable(int x, int y) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBounds(x, y, 150, 100);

        JLabel titleLabel = new JLabel("Table", SwingConstants.CENTER);
        tablePanel.add(titleLabel, BorderLayout.NORTH);

        // Panel to hold rows
        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        tablePanel.add(new JScrollPane(rowsPanel), BorderLayout.CENTER);

        // Enable selecting the table
        tablePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setSelectedTable(tablePanel);
            }
        });

        // Enable dragging within the main panel
        tablePanel.addMouseMotionListener(new MouseMotionAdapter() {
            Point offset = new Point();


            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = tablePanel.getLocation();
                location.translate(e.getX() - offset.x, e.getY() - offset.y);
                tablePanel.setLocation(location);
                repaint();
            }
        });

        add(tablePanel);
        repaint();
        revalidate();
    }

    private void addRowToSelectedTable() {
        if (selectedTable != null) {
            String rowData = inputField.getText();
            if (!rowData.isEmpty()) {
                JPanel rowsPanel = (JPanel) ((JScrollPane) selectedTable.getComponent(1)).getViewport().getView();
                JLabel rowLabel = new JLabel(rowData);
                rowsPanel.add(rowLabel);
                rowsPanel.revalidate();
                inputField.setText("");
            }
        }
    }

    private void setSelectedTable(JPanel table) {
        if (selectedTable != null) {
            selectedTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        selectedTable = table;
        selectedTable.setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    private class ValueExportTransferHandler extends TransferHandler {
        private final String value;

        public ValueExportTransferHandler(String value) {
            this.value = value;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(value);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    private class TableDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                Transferable transferable = dtde.getTransferable();
                if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);

                    if ("table".equals(data)) {
                        Point dropPoint = dtde.getLocation();
                        addTable(dropPoint.x, dropPoint.y);
                    }

                    dtde.dropComplete(true);
                } else {
                    dtde.rejectDrop();
                }
            } catch (Exception e) {
                dtde.rejectDrop();
                e.printStackTrace();
            }
        }
    }
}
