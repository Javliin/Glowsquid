package me.javlin.glowsquid.mc.chat;

public enum Style {
    BOLD ((byte) 0b0001),
    ITALIC ((byte) 0b0010),
    STRIKETHROUGH ((byte) 0b0100),
    UNDERLINED ((byte) 0b1000);

    private final byte mask;

    Style(byte mask) {
        this.mask = mask;
    }

    byte getMask() {
        return mask;
    }
}
