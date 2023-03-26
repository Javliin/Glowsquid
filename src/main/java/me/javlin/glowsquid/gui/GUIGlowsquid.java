package me.javlin.glowsquid.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketEncoder;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class GUIGlowsquid extends JFrame {
    private static GUIGlowsquid INSTANCE;

    private final List<PacketEncoder> packetLog = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, Filter.Action> queuedPacketsInbound = new ConcurrentHashMap<>();
    private final Map<Integer, Filter.Action> queuedPacketsOutbound = new ConcurrentHashMap<>();

    private final List<Filter> filters = new CopyOnWriteArrayList<>();
    private final List<Integer> filteredPackets = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> blockedPackets = Collections.synchronizedList(new ArrayList<>());

    private final AtomicReference<PacketEncoder> selectedPacket = new AtomicReference<>();
    private final AtomicReference<Map<String, Integer>> selectedPacketFields = new AtomicReference<>();

    private final DefaultTableModel packetTableModel;
    private final JTextArea consoleOutput;
    private final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SSS");

    private GUIGlowsquid() {
        super("Glowsquid " + Glowsquid.VERSION);

        GUIFiltersAdd.registerPackets();

        setLayout(new GridBagLayout());
        setSize(1200, 600);

        Color bg = new Color(24, 21, 24);
        Color lightBg = new Color(29, 26, 29);

        JTree dataTree = new JTree(new DefaultMutableTreeNode("Click a packet to view attributes"));
        JPanel console = new JPanel();
        JPanel dataAndTree = new JPanel();
        JTextArea data = new JTextArea("Nothing to display");
        JTable packets = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        packetTableModel = new DefaultTableModel(new String[]{"No.", "Timestamp", "ID", "Type", "State", "Direction"}, 0);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        packets.setDefaultRenderer(String.class, centerRenderer);
        packets.setModel(packetTableModel);
        packets.getTableHeader().setReorderingAllowed(false);
        packets.getColumnModel().getColumn(0).setPreferredWidth(50);
        packets.getColumnModel().getColumn(1).setPreferredWidth(75);
        packets.getColumnModel().getColumn(2).setPreferredWidth(50);
        packets.getColumnModel().getColumn(3).setPreferredWidth(150);
        packets.getColumnModel().getColumn(4).setPreferredWidth(100);
        packets.getColumnModel().getColumn(5).setPreferredWidth(75);
        packets.setCellSelectionEnabled(false);
        packets.setColumnSelectionAllowed(false);
        packets.setRowSelectionAllowed(true);
        packets.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packets.getSelectionModel().addListSelectionListener(click -> {
            ListSelectionModel lsm = (ListSelectionModel) click.getSource();

            int index = lsm.isSelectedIndex(click.getFirstIndex()) ? click.getFirstIndex() : click.getLastIndex();

            PacketEncoder packetLogData = packetLog.get(Integer.parseInt((String) packetTableModel.getValueAt(index, 0)));
            Packet packet = packetLogData.getData();
            List<Map.Entry<byte[], Integer>> header = packetLogData.getHeaders();

            if (packet != null) {
                StringBuilder sb = new StringBuilder();

                for (byte bite : packet.getReadData()) {
                    sb.append(String.format("%02X ", bite));
                }

                data.setText(sb.toString());

                Map<String, Integer> fields = new HashMap<>();
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(packet.getClass().getSimpleName());
                DefaultMutableTreeNode node = new DefaultMutableTreeNode("length");

                int fieldCount = 0;
                int length = header.get(0).getValue();

                fields.put("length", fieldCount);
                fieldCount++;

                node.add(new DefaultMutableTreeNode(length));
                root.add(node);

                if (header.size() > 2) { // Compression enabled
                    node = new DefaultMutableTreeNode("compressedLength");

                    int compressedLength = header.get(1).getValue();

                    fields.put("compressedLength", fieldCount);
                    fieldCount++;

                    node.add(new DefaultMutableTreeNode(String.valueOf(compressedLength)));
                    root.add(node);
                }

                for (Field field : packet.getClass().getDeclaredFields()) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    String name = field.getName();
                    node = new DefaultMutableTreeNode(name);

                    try {
                        String value = field.get(packet).toString();

                        fields.put(name, fieldCount);
                        fieldCount++;

                        node.add(new DefaultMutableTreeNode(value));
                    } catch (IllegalAccessException exception) {
                        Console.error("FAIL_PARSE_PACKET", packet.getClass().getSimpleName(), packetLogData.getPacketId());
                        exception.printStackTrace();
                    }

                    root.add(node);
                }

                selectedPacket.set(packetLogData);
                selectedPacketFields.set(fields);
                dataTree.setModel(new DefaultTreeModel(root));
            }
        });

        JScrollPane packetScroll = new JScrollPane(packets);
        packetScroll.getVerticalScrollBar().addAdjustmentListener(event -> {
            Adjustable adj = event.getAdjustable();
            if ((adj.getMaximum() - adj.getValue()) < (packetScroll.getHeight() + adj.getVisibleAmount()) * 3){
                adj.setValue(adj.getMaximum());
            }
        });

        packets.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int index = Integer.parseInt((String) packetTableModel.getValueAt(row, 0));

                if (filteredPackets.contains(index)) {
                    component.setBackground(new Color(146, 127, 0, 255));
                } else if (blockedPackets.contains(index)) {
                    component.setBackground(new Color(122, 0, 0, 255));
                } else {
                    component.setBackground(bg);
                }

                return component;
            }
        });

        Dimension consoleSize = new Dimension(500, 200);

        dataTree.setEditable(false);
        dataTree.setPreferredSize(new Dimension(250, 300));
        dataTree.setBackground(lightBg);
        dataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        dataTree.getSelectionModel().addTreeSelectionListener(click -> {
            data.getHighlighter().removeAllHighlights();

            TreePath path = click.getNewLeadSelectionPath();

            if (path == null) {
                return;
            }

            Map<String, Integer> fields = selectedPacketFields.get();
            List<Integer> fieldSizes = new ArrayList<>();
            PacketEncoder selectedData = selectedPacket.get();
            Integer index = fields.get(path.getLastPathComponent().toString());

            if (index == null) {
                return;
            }

            for (Map.Entry<byte[], Integer> entry : selectedData.getHeaders()) {
                fieldSizes.add(entry.getKey().length);
            }

            fieldSizes.addAll(selectedData.getData().getFieldSizes());

            int startIndex = 0;

            for(int i = 0; i < index; i++) {
                startIndex += fieldSizes.get(i);
            }

            startIndex *= 3;

            int endIndex = startIndex + (fieldSizes.get(index) * 3) - 1;

            try {
                data.getHighlighter().addHighlight(startIndex, endIndex, new DefaultHighlighter.DefaultHighlightPainter(new Color(0, 0, 0, 100)));
            } catch (BadLocationException exception) {
                Console.error("FAIL_HIGHLIGHT");
                exception.printStackTrace();
            }
        });

        data.setEditable(false);
        data.setLineWrap(true);
        data.setWrapStyleWord(true);
        data.setFont(new Font("Monospaced", Font.PLAIN, 12));

        GridBagConstraints con = new GridBagConstraints();

        con.fill = GridBagConstraints.BOTH;
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 1;
        con.weighty = 1;

        dataAndTree.setPreferredSize(new Dimension(500, 300));
        dataAndTree.setLayout(new GridBagLayout());
        dataAndTree.add(dataTree, con);

        con.gridx = 1;

        JScrollPane dataScroll = new JScrollPane(data);

        dataScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        dataScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        dataScroll.setBorder(null);

        dataAndTree.add(dataScroll, con);

        console.setLayout(new BorderLayout());
        console.setBorder(new BevelBorder(BevelBorder.LOWERED));
        console.setBackground(bg);
        console.setMaximumSize(consoleSize);
        console.setPreferredSize(consoleSize);

        consoleOutput = new JTextArea();

        consoleOutput.setHighlighter(null);
        consoleOutput.setEditable(false);
        consoleOutput.setLineWrap(true);
        consoleOutput.setBackground(bg);
        consoleOutput.setForeground(Color.WHITE);

        JScrollPane consoleScroll = new JScrollPane(consoleOutput);

        consoleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        consoleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        consoleScroll.setBorder(null);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/glowsquid.png")));
        Image image = icon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        JMenuBar menuBar = new JMenuBar();
        JMenuItem about = new JMenuItem("About");
        JMenuItem accounts = new JMenuItem("Accounts");
        JMenuItem scripts = new JMenuItem("Scripts");
        JMenuItem filters = new JMenuItem("Filters");
        JMenu tools = new JMenu("Tools");

        about.addActionListener(click -> JOptionPane.showMessageDialog(
                null,
                "A Minecraft packet capture tool written in Java\nDeveloped by Javlin",
                "About",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        ));

        accounts.addActionListener(click -> new GUIAccounts(image));
        filters.addActionListener(click -> new GUIFilters(image));

        tools.add(scripts);
        tools.add(filters);

        menuBar.add(accounts);
        menuBar.add(tools);
        menuBar.add(about);
        console.add(consoleScroll);

        con.gridx = 0;
        con.weightx = 0.4;
        con.weighty = 0.5;

        add(console, con);

        con.gridy = 1;

        add(dataAndTree, con);

        con.gridx = 1;
        con.gridy = 0;
        con.gridheight = 2;
        con.weightx = 1;
        con.weighty = 1;

        add(packetScroll, con);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Glowsquid.stop();
                super.windowClosing(e);
            }
        });

        setJMenuBar(menuBar);
        setIconImage(image);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public synchronized void displayPacket(PacketEncoder writer) {
        int packetNumber = packetLog.size();

        packetLog.add(writer);
        packetTableModel.addRow(new String[]{
                Integer.toString(packetNumber),
                dateFormat.format(new Date()),
                "0x" + String.format("%02X ", writer.getPacketId()),
                writer.getData().getClass().getSimpleName(),
                writer.getBuilder().getState().name(),
                writer.getBuilder().getDirection().name()
        });

        Filter.Action action = writer.getBuilder().getDirection() == PacketInfo.PacketDirection.INBOUND
                ? queuedPacketsInbound.remove(writer.getPacketId())
                : queuedPacketsOutbound.remove(writer.getPacketId());

        if (action == Filter.Action.BLOCK) {
            blockedPackets.add(packetNumber);
        } else if (action == Filter.Action.MODIFY) {
            filteredPackets.add(packetNumber);
        }
    }

    public synchronized void printToConsole(String text) {
        if (consoleOutput == null) { // GUI not initialized
            return;
        }

        consoleOutput.setText(consoleOutput.getText() + text + System.lineSeparator());
    }

    public void highlightPacket(int id, PacketInfo.PacketDirection direction, Filter.Action action) {
        if (direction == PacketInfo.PacketDirection.INBOUND) {
            queuedPacketsInbound.put(id, action);
        } else {
            queuedPacketsOutbound.put(id, action);
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public static GUIGlowsquid getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        FlatDarkLaf.setup();

        try {
            UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());
        } catch (UnsupportedLookAndFeelException exception) {
            Console.error("GUI_FAIL_FLATLAF");
            throw new RuntimeException(exception);
        }

        FlatLaf.setUseNativeWindowDecorations(true);
        UIManager.put("TitlePane.menuBarEmbedded", true);
        UIManager.put("TitlePane.unifiedBackground", true);
        UIManager.put("TitlePane.showIcon", true);

        INSTANCE = new GUIGlowsquid();
        return INSTANCE;
    }
}
