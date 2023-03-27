package me.javlin.glowsquid.gui;

import com.formdev.flatlaf.FlatClientProperties;
import me.javlin.glowsquid.Glowsquid;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GUIAccountsAdd extends JFrame {
    public GUIAccountsAdd(DefaultTableModel model, Image icon) {
        super("Add Account");

        JPanel buttonPanel = new JPanel();
        JTextField name = new JTextField();
        JTextField accessToken = new JTextField();
        JButton add = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        accessToken.addActionListener(keyPress -> add(model, name, accessToken));
        add.addActionListener(click -> add(model, name, accessToken));
        cancel.addActionListener(click -> destroy());

        name.setMaximumSize(new Dimension(200, 25));
        accessToken.setMaximumSize(new Dimension(200, 25));
        name.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        accessToken.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Access Token");

        buttonPanel.add(add);
        buttonPanel.add(cancel);
        add(name);
        add(Box.createRigidArea(new Dimension(5, 5)));
        add(accessToken);
        add(buttonPanel);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);
        setIconImage(icon);
        setSize(250, 125);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void add(DefaultTableModel model, JTextField name, JTextField accessToken) {
        Glowsquid.ACCESS_TOKENS.put(name.getText(), accessToken.getText());
        model.addRow(new String[]{name.getText(), accessToken.getText()});

        destroy();
    }

    private void destroy() {
        setVisible(false);
        dispose();
    }
}
