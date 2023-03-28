package me.javlin.glowsquid.network.proxy.module.impl;

import me.javlin.glowsquid.gui.GUIGlowsquid;
import me.javlin.glowsquid.network.packet.builder.PacketBuilder;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.event.PacketEvent;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;

import java.util.List;

public class FilterModule extends Module {
    private static final GUIGlowsquid GUI = GUIGlowsquid.getInstance();

    @PacketEvent
    public void onPacket(PacketSendEvent<Packet> event) {
        Packet packet = event.getPacket();
        List<Filter> filters = GUIGlowsquid.getInstance().getFilters();
        Class<?> packetClass = event.getPacket().getClass();
        int id = PacketBuilder.getPacketIds().get(packetClass);

        for (Filter filter : filters) {
            if (packetClass.equals(filter.getPacketClass())) {
                boolean result = filter.process(packet);

                if (!result) {
                    if (filter.getAction() == Filter.Action.BLOCK) {
                        GUI.highlightPacket(id, event.getDirection(), filter.getAction());
                        event.setCancelled(true);
                        break;
                    }
                } else if (filter.getAction() == Filter.Action.MODIFY) {
                    GUI.highlightPacket(id, event.getDirection(), filter.getAction());
                }
            }
        }
     }
}
