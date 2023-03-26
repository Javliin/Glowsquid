package me.javlin.glowsquid.network.packet.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;

@Getter
public class PacketSendEvent<T extends Packet> {
    private final PacketInfo.PacketState state;
    private final PacketInfo.PacketDirection direction;
    private final T packet;

    @Setter
    private boolean cancelled = false;

    public PacketSendEvent(PacketInfo.PacketState state, PacketInfo.PacketDirection direction, T packet) {
        this.state = state;
        this.direction = direction;
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }
}
