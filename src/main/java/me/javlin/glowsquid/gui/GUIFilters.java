package me.javlin.glowsquid.gui;

import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

public class GUIFilters extends JFrame {
    public GUIFilters(Image icon) {
        super("Filters");

        setLayout(new BorderLayout());

        List<Filter> filters = GUIGlowsquid.getInstance().getFilters();
        JTable filtersTable = new JTable();
        String[][] tableData = new String[filters.size()][3];

        int rowIndex = 0;

        for (Filter filter : filters) {
            tableData[rowIndex][0] = filter.getPacketClass().getSimpleName();
            tableData[rowIndex][1] = filter.getAction().name();
            tableData[rowIndex][2] = filter.getCondition().name();

            rowIndex++;
        }

        DefaultTableModel model = new DefaultTableModel(tableData, new String[]{"Packet", "Action", "Condition"});

        filtersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        filtersTable.setModel(model);
        filtersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        filtersTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        filtersTable.getColumnModel().getColumn(2).setPreferredWidth(75);
        filtersTable.getTableHeader().setReorderingAllowed(false);
        filtersTable.getTableHeader().setResizingAllowed(false);

        JPanel buttonPanel = new JPanel();
        JButton addFilter = new JButton("Add Filter");
        JButton removeFilter = new JButton("Delete Filter");

        addFilter.addActionListener(click -> new GUIFiltersAdd(model, icon));
        removeFilter.addActionListener(click -> {
            filters.remove(filtersTable.getSelectedRow());
            model.removeRow(filtersTable.getSelectedRow());
        });

        buttonPanel.add(addFilter);
        buttonPanel.add(removeFilter);

        add(new JScrollPane(filtersTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);

        setResizable(false);
        setIconImage(icon);
        setSize(600, 200);
        setVisible(true);
    }
}
