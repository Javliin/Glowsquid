package me.javlin.glowsquid.gui;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.FilterType;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;

public class GUIFiltersAdd extends JFrame {
    private static final Map<String, PacketData> PACKET_DATA = new HashMap<>();
    private static final Class<?>[] APPLICABLE_FIELD_TYPES = new Class<?>[]{
            int.class,
            float.class,
            double.class,
            short.class,
            byte.class,
            boolean.class,
            String.class
    };

    public GUIFiltersAdd(DefaultTableModel model, Image icon) {
        super("Add Filter");

        JComboBox<String> packets = new JComboBox<>();

        JComboBox<String> action = new JComboBox<>(new DefaultComboBoxModel<>(
                Arrays.stream(Filter.Action.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        ));

        JComboBox<String> condition = new JComboBox<>(new DefaultComboBoxModel<>(
                Arrays.stream(Filter.Condition.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        ));

        JComboBox<String> modify = new JComboBox<>(new DefaultComboBoxModel<>(
                Arrays.stream(Filter.Modify.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        ));

        JTextField modifyValue = new JTextField();
        JComboBox<String> modifyField = new JComboBox<>();
        JTextField conditionValue = new JTextField();
        JComboBox<String> conditionField = new JComboBox<>();
        JPanel buttonPanel = new JPanel();
        JButton add = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        action.addActionListener(click -> {
            boolean enabled = Objects.equals(action.getSelectedItem(), "MODIFY");

            modify.setEnabled(enabled);
            modifyValue.setEnabled(enabled);
            modifyField.setEnabled(enabled);
        });

        condition.addActionListener(click -> {
            boolean enabled = !Objects.equals(condition.getSelectedItem(), "NONE");

            conditionField.setEnabled(enabled);
            conditionValue.setEnabled(enabled);
        });

        add.addActionListener(click ->  {
            if (packets.getSelectedItem() == null) {
                return;
            }

            PacketData data = PACKET_DATA.get((String) packets.getSelectedItem());

            FilterType<?> conditionType = null;
            FilterType<?> modifyType = null;
            Filter.Condition condition_ = Filter.Condition.valueOf((String) condition.getSelectedItem());
            Filter.Action action_ = Filter.Action.valueOf((String) action.getSelectedItem());
            Filter.Modify modify_ = (modify.isEnabled()
                    ? Filter.Modify.valueOf((String) modify.getSelectedItem())
                    : null);

            // Validate condition
            if (condition_ != Filter.Condition.NONE) {
                Class<?> type = data.getFields().get((String) conditionField.getSelectedItem()).getType();
                String conditionValueText = conditionValue.getText();

                conditionType = FilterType.create(conditionValueText, type);

                if (conditionType == null) {
                    Console.error("FILTER_FAIL_CREATE");
                    return;
                }
            }

            // Validate modify
            if (modify.isEnabled()) {
                Class<?> type = data.getFields().get((String) modifyField.getSelectedItem()).getType();
                String modifyValueText = modifyValue.getText();

                modifyType = FilterType.create(modifyValueText, type);

                if (modifyType == null) {
                    Console.error("FILTER_FAIL_CREATE");
                    return;
                }
            }

            GUIGlowsquid.getInstance().getFilters().add(new Filter(
                    conditionType,
                    modifyType,
                    data.getPacketClass(),
                    action_,
                    modify_,
                    condition_,
                    data.getFields().get((String) modifyField.getSelectedItem()),
                    data.getFields().get((String) conditionField.getSelectedItem())
            ));

            model.addRow(new String[]{data.getPacketClass().getSimpleName(), action_.name(), condition_.name()});
            destroy();
        });

        cancel.addActionListener(click -> destroy());

        packets.addActionListener(click -> {
            String[] viableFields = PACKET_DATA.get((String) packets.getSelectedItem()).getFields().keySet().toArray(new String[0]);

            modifyField.setModel(new DefaultComboBoxModel<>(viableFields));
            conditionField.setModel(new DefaultComboBoxModel<>(viableFields));
        });

        modifyField.addActionListener(click -> {

        });

        Dimension size = new Dimension(200, 25);

        modify.setEnabled(false);
        modifyValue.setEnabled(false);
        modifyField.setEnabled(false);
        conditionValue.setEnabled(false);
        conditionField.setEnabled(false);

        conditionValue.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Value");
        modifyValue.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Value");

        modifyField.setMaximumSize(size);
        conditionValue.setMaximumSize(size);
        conditionField.setMaximumSize(size);
        modifyValue.setMaximumSize(size);
        action.setMaximumSize(size);
        condition.setMaximumSize(size);
        modify.setMaximumSize(size);
        packets.setMaximumSize(size);

        packets.setEditable(false);
        packets.setModel(new DefaultComboBoxModel<>(PACKET_DATA.keySet().toArray(new String[0])));

        if (packets.getSelectedItem() != null) {
            String[] viableFields = PACKET_DATA.get((String) packets.getSelectedItem()).getFields().keySet().toArray(new String[0]);

            modifyField.setModel(new DefaultComboBoxModel<>(viableFields));
            conditionField.setModel(new DefaultComboBoxModel<>(viableFields));
        }

        buttonPanel.add(add);
        buttonPanel.add(cancel);

        Dimension small = new Dimension(5, 5);
        Dimension large = new Dimension(20, 20);

        add(Box.createRigidArea(small));
        add(packets);
        add(Box.createRigidArea(large));
        add(action);
        add(Box.createRigidArea(small));
        add(modify);
        add(Box.createRigidArea(small));
        add(modifyField);
        add(Box.createRigidArea(small));
        add(modifyValue);
        add(Box.createRigidArea(large));
        add(conditionField);
        add(Box.createRigidArea(small));
        add(condition);
        add(Box.createRigidArea(small));
        add(conditionValue);
        add(Box.createRigidArea(small));
        add(buttonPanel);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);
        setIconImage(icon);
        setSize(300, 360);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void destroy() {
        setVisible(false);
        dispose();
    }

    public static void registerPackets() {
        for (PacketInfo.PacketState state : PacketInfo.PacketState.values()) {
            for (PacketInfo.PacketDirection direction : PacketInfo.PacketDirection.values()) {
                Map<Integer, Class<? extends Packet>> packetClasses = (direction == PacketInfo.PacketDirection.OUTBOUND
                        ? state.getClientClasses()
                        : state.getServerClasses());

                for (Map.Entry<Integer, Class<? extends Packet>> entry : packetClasses.entrySet()) {
                    Map<String, Field> fields = new HashMap<>();
                    Class<? extends Packet> packetClass = entry.getValue();
                    StringBuilder sb = new StringBuilder();
                    int id = entry.getKey();

                    sb.append(String.format("%02X", id));
                    sb.append(" - ");
                    sb.append(packetClass.getSimpleName());

                    for (Field field : packetClass.getDeclaredFields()) {
                        StringBuilder fieldSb = new StringBuilder();
                        Class<?> type = field.getType();

                        for (Class<?> type_ : APPLICABLE_FIELD_TYPES) {
                            if (type_.isAssignableFrom(type)) {
                                field.setAccessible(true);
                                fieldSb.append(field.getName());
                                fieldSb.append(" - ");
                                fieldSb.append(type.getSimpleName());
                                fields.put(fieldSb.toString(), field);

                                break;
                            }
                        }
                    }

                    PACKET_DATA.put(sb.toString(), new PacketData(fields, packetClass));
                }
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class PacketData {
        private final Map<String, Field> fields;
        private final Class<? extends Packet> packetClass;
    }
}
