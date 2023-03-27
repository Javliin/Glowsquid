package me.javlin.glowsquid.gui;

import me.javlin.glowsquid.Glowsquid;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GUIAccounts extends JFrame {
    public GUIAccounts(Image icon) {
        super("Accounts");

        Map<String, String> accessTokens = Glowsquid.ACCESS_TOKENS;

        setLayout(new BorderLayout());

        JTable accounts = new JTable();
        String[][] tableData = new String[accessTokens.size()][2];

        int rowIndex = 0;

        for (Map.Entry<String, String> entry : accessTokens.entrySet()) {
            tableData[rowIndex][0] = entry.getKey();
            tableData[rowIndex][1] = entry.getValue();
            rowIndex++;
        }

        DefaultTableModel model = new DefaultTableModel(tableData, new String[]{"Username", "Access Token"});

        accounts.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        accounts.setModel(model);
        accounts.getColumnModel().getColumn(0).setPreferredWidth(150);
        accounts.getColumnModel().getColumn(1).setPreferredWidth(400);
        accounts.getTableHeader().setReorderingAllowed(false);
        accounts.getTableHeader().setResizingAllowed(false);

        JPanel buttonPanel = new JPanel();
        JButton addAccount = new JButton("Add Account");
        JButton removeAccount = new JButton("Delete Account");

        addAccount.addActionListener(click -> new GUIAccountsAdd(model, icon));
        removeAccount.addActionListener(click -> {
            accessTokens.remove((String) accounts.getValueAt(accounts.getSelectedRow(), 0));
            model.removeRow(accounts.getSelectedRow());
        });

        buttonPanel.add(addAccount);
        buttonPanel.add(removeAccount);

        add(new JScrollPane(accounts), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);

        setResizable(false);
        setIconImage(icon);
        setSize(600, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
