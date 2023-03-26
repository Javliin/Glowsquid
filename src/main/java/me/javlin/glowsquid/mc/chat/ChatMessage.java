package me.javlin.glowsquid.mc.chat;

public class ChatMessage {
    private String message;
    private String color;
    private byte style;

    public ChatMessage(String message) {
        this.message = message;
    }

    public ChatMessage(String message, Color color, Style... styles) {
        this.message = message;
        this.color = color.name().toLowerCase();

        for (Style style : styles) {
            this.style |= style.getMask();
        }
    }

    String getText() {
        return message;
    }

    String getColor() {
        return color;
    }

    boolean isStyle(Style style) {
        return style.getMask() == (this.style & style.getMask());
    }
}
