package me.javlin.glowsquid.network.proxy.module.impl.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.FilterType;
import me.javlin.glowsquid.network.packet.Packet;

import java.lang.reflect.Field;

@Getter
@RequiredArgsConstructor
public class Filter {
    private final FilterType<?> conditionFilter;
    private final FilterType<?> modifyFilter;
    private final Class<? extends Packet> packetClass;

    private final Action action;
    private final Modify modify;
    private final Condition condition;

    private final Field modifyField;
    private final Field conditionField;

    public boolean process(Packet packet) {
        try {
            if (condition != Condition.NONE
                    && !conditionFilter.compare(condition, conditionField.get(packet))) {
                return false;
            }

            if (action == Action.BLOCK) {
                return false;
            }

            modifyField.set(packet, modifyFilter.modify(modify, modifyField.get(packet)));
        } catch (IllegalAccessException | IllegalArgumentException exception) {
            Console.error("FILTER_FAIL_PROCESS", packet.getClass().getSimpleName());
            exception.printStackTrace();
        }

        return true;
    }

    public enum Action {
        BLOCK, MODIFY
    }

    public enum Modify {
        SET, INCREMENT, DECREMENT
    }

    public enum Condition {
        NONE, EQUAL, LESS, MORE
    }
}
