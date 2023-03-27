package me.javlin.glowsquid.gui;

import me.javlin.glowsquid.Glowsquid;
import me.javlin.glowsquid.network.proxy.ProxySession;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;

public class GUIModules extends JFrame {
    public GUIModules(Image icon) {
        super("Modules");

        setLayout(new BorderLayout());
        ModuleManager manager = ModuleManager.getInstance();
        List<Module> modules = manager.getModules();

        Object[][] tableData = new Object[modules.size()][2];
        JTable modulesTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                if (column == 1 && manager.getSession() == null) {
                    return null;
                }

                return super.prepareRenderer(renderer, row, column);
            }

            @Override
            public void setValueAt(Object value, int row, int col) {
                super.setValueAt(value, row, col);
                if (col == 1) {
                    if ((Boolean) this.getValueAt(row, col)) {
                        ((Module) this.getValueAt(row, 0)).setEnabled(true);
                    } else {
                        ((Module) this.getValueAt(row, 0)).setEnabled(false);
                    }
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 && manager.getSession() != null;
            }
        };

        int rowIndex = 0;

        for (Module module : modules) {
            tableData[rowIndex][0] = module;
            tableData[rowIndex][1] = module.isEnabled();

            rowIndex++;
        }

        DefaultTableModel model = new DefaultTableModel(tableData, new String[]{"Module", "Enabled"}) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Boolean.class;
                }

                return super.getColumnClass(columnIndex);
            }
        };

        modulesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        modulesTable.setModel(model);
        modulesTable.getTableHeader().setReorderingAllowed(false);
        modulesTable.getTableHeader().setResizingAllowed(false);
        modulesTable.setCellSelectionEnabled(false);
        modulesTable.setColumnSelectionAllowed(false);
        modulesTable.setRowSelectionAllowed(true);

        JPanel buttonPanel = new JPanel();
        JButton loadModule = new JButton("Load Module");
        JButton deleteModule = new JButton("Delete Module");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR Files", "jar");
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File("."));
        chooser.addActionListener(click -> {
            if (click.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                List<Module> loadedModules = ModuleManager.getInstance().load(chooser.getSelectedFile());

                for (Module module : loadedModules) {
                    model.addRow(new Object[]{module, module.isEnabled()});
                }
            }
        });

        loadModule.addActionListener(click -> chooser.showOpenDialog(this));
        deleteModule.addActionListener(click -> {
            Module module = (Module) model.getValueAt(modulesTable.getSelectedRow(), 0);

            ModuleManager.getInstance().unregister(module);
            model.removeRow(modulesTable.getSelectedRow());
        });

        buttonPanel.add(loadModule);
        buttonPanel.add(deleteModule);

        add(new JScrollPane(modulesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);

        setResizable(false);
        setIconImage(icon);
        setSize(600, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
