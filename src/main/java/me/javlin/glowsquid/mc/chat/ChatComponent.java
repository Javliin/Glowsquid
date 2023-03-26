package me.javlin.glowsquid.mc.chat;

import java.util.ArrayList;

public class ChatComponent {
    private final ArrayList<ChatMessage> components = new ArrayList<>();

    public ChatComponent(String message, Style... styles) {
        components.add(new ChatMessage(message, Color.WHITE, styles));
    }

    public ChatComponent(String message, Color color, Style... styles) {
        components.add(new ChatMessage(message, color, styles));
    }

    public ChatComponent add(String message, Color color, Style... styles) {
        components.add(new ChatMessage(message, color, styles));

        return this;
    }

    public ChatComponent add(String message, Style... styles) {
        components.add(new ChatMessage(message, Color.WHITE, styles));

        return this;
    }

    public String build() {
        StringBuilder json = new StringBuilder("{");
        int index = 0;

        for (ChatMessage component : components) {
            if (index == 1) {
                json.append(",\"extra\":[{");
            } else if (index > 1) {
                json.append("},{");
            }

            json.append("\"text\":\"");
            json.append(component.getText());
            json.append("\",\"color\":\"");
            json.append(component.getColor());
            json.append("\"");

            for (Style style : Style.values()) {
                if (!component.isStyle(style)) {
                    continue;
                }

                json.append(",\"");
                json.append(style.name().toLowerCase());
                json.append("\":\"true\"");
            }

            index++;
        }

        json.append(index > 1 ? "}]}" : "}");

        return json.toString();
    }
}
