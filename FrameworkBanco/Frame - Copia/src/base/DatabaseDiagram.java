package base;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DatabaseDiagram extends JPanel {
    private JPanel palette;

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

        // Set drop target for the main panel
        new DropTarget(this, new TableDropTargetListener());
    }

    private void addTable(int x, int y) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBounds(x, y, 100, 60);

        JLabel titleLabel = new JLabel("Table", SwingConstants.CENTER);
        tablePanel.add(titleLabel, BorderLayout.CENTER);

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
