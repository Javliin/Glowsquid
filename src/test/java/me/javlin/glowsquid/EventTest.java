package me.javlin.glowsquid;

import me.javlin.glowsquid.dummy.DummyListener;
import me.javlin.glowsquid.network.packet.PacketInfo;
import me.javlin.glowsquid.network.packet.event.PacketEventHandler;
import me.javlin.glowsquid.network.packet.impl.play.inbound.PacketServerChat;
import me.javlin.glowsquid.network.packet.impl.play.outbound.PacketClientChat;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class EventTest {
    @Test
    public void callEvent() {
        DummyListener listener = new DummyListener();

        PacketEventHandler.register(listener);
        PacketEventHandler.call(new PacketClientChat("Hello world!"), PacketInfo.PacketState.PLAY, PacketInfo.PacketDirection.OUTBOUND);
        PacketEventHandler.call(new PacketServerChat("Hello world!", PacketServerChat.Position.CHAT_BOX, true), PacketInfo.PacketState.PLAY, PacketInfo.PacketDirection.OUTBOUND);

        int expected = DummyListener.class.getDeclaredMethods().length - 1;

        Assertions.assertEquals(expected, listener.events);
        Assertions.assertEquals(expected, listener.called);
    }
}
