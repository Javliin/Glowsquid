package me.javlin.glowsquid.network.proxy.module.impl.core;

import me.javlin.glowsquid.mc.chat.ChatComponent;
import me.javlin.glowsquid.mc.chat.Color;
import me.javlin.glowsquid.mc.chat.Style;
import me.javlin.glowsquid.network.DelayedType;
import me.javlin.glowsquid.network.proxy.module.Module;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import me.javlin.glowsquid.network.packet.event.PacketEvent;
import me.javlin.glowsquid.network.packet.event.PacketSendEvent;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketClientChat;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketServerChat;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.util.HashMap;
import java.util.Map;

public class CommandModule extends Module {
    private static final Map<String, DelayedType> COMMANDS = new HashMap<String, DelayedType>(){{
        put("erange", DelayedType.ENEMY_RANGE);
        put("edelay", DelayedType.ENEMY_DELAY);
        put("range", DelayedType.PLAYER_RANGE);
        put("tps", DelayedType.TELEPORT_SMOOTHING);
    }};

    public CommandModule(ModuleManager manager) {
        super(manager);
    }

    @PacketEvent
    public void onChatPacket(PacketSendEvent<PacketClientChat> event) {
        PacketClientChat chat = event.getPacket();

        String returnMessage = "";
        String message = chat.getMessage().toLowerCase();
        String[] splitMessage = message.split(" ");

        if (splitMessage.length <= 1 || !UtilDataType.isNumeric(splitMessage[1])) {
            return;
        }

        int setting = Integer.parseInt(splitMessage[1]);

        DelayedType type = COMMANDS.get(splitMessage[0].toLowerCase());

        if (type != null) {
            type.setDelay(setting);
            returnMessage = String.format("%s set to: %d", type.getDescription(), setting);
        }

        if (!returnMessage.equals("")) {
            session.queueInbound(new PacketServerChat(new ChatComponent(returnMessage, Color.GREEN, Style.BOLD).build(), session.is18()));

            // Print settings
            // Console.log("Enemy range delay (erange): " + DelayedType.ENEMY_RANGE.getDelay());
            // Console.log("Enemy general delay (edelay): " + DelayedType.ENEMY_DELAY.getDelay());
            // Console.log("Player range delay (range): " + DelayedType.PLAYER_RANGE.getDelay());
            // Console.log("Teleport smoothing delay (tps): " + DelayedType.TELEPORT_SMOOTHING.getDelay());

            event.setCancelled(true);
        }
    }
}
