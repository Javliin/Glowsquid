package me.javlin.glowsquid.network.packet.impl.play.outbound;

import lombok.Getter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.builder.PacketDecoder;

@Getter
public class PacketEnchantItem extends Packet {
    public static final int ID = 0x11;

    private byte windowId;
    private byte enchantment;

    public PacketEnchantItem(PacketDecoder packet) {
        super(packet);
    }

    @Override
    public void read() {
        windowId = readByte();
        enchantment = readByte();
    }

    @Override
    public void write() {
        writeByte(windowId);
        writeByte(enchantment);
    }
}
