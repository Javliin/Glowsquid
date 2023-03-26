package me.javlin.glowsquid.network.packet.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.packet.Packet;
import me.javlin.glowsquid.network.packet.PacketInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

//TODO: Make this per-ModuleManager for multi-session support
public class PacketEventHandler {
    private static final Map<Class<? extends Packet>, List<EventEntry>> eventListeners = new HashMap<>();

    public static void register(Object listener) {
        registerClassEvents(listener);
    }

    public static void unregisterAll() {
        eventListeners.clear();
    }

    public static PacketSendEvent<? extends Packet> call(Packet packet, PacketInfo.PacketState state, PacketInfo.PacketDirection direction) {
        PacketSendEvent<? extends Packet> event = new PacketSendEvent<>(state, direction, packet);

        // If the packet wasn't recognized by its ID, it doesn't have any events
        if(packet == null) {
            return event;
        }

        List<List<EventEntry>> eventEntiresSq = eventListeners.keySet()
                .stream()
                .filter(entry -> entry.isAssignableFrom(packet.getClass()))
                .map(eventListeners::get)
                .collect(Collectors.toList());

        for (List<EventEntry> eventEntries : eventEntiresSq) {
            if (eventEntries == null) {
                continue;
            }

            eventEntries.forEach(entry -> {
                try {
                    entry.getCallback().invoke(entry.getSourceInstance(), event);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    Console.error("FAIL_INVOKE_EVENT", packet.getClass().getName(), entry.getSourceInstance().getClass().getName());
                    exception.printStackTrace();
                }
            });
        }

        return event;
    }

    /**
     * Go through the available declared methods of a class, and attempt to register an event listener for
     * each method with the PacketEvent annotation.
     * @param instance Instance of object containing events
     */
    @SuppressWarnings("unchecked")
    private static void registerClassEvents(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PacketEvent.class)) {
                Class<?>[] parameters = method.getParameterTypes();
                Type[] genericParameters = method.getGenericParameterTypes();
                Type[] actualGenericTypes = null;

                if (genericParameters.length == 1 && genericParameters[0] instanceof ParameterizedType) {
                    actualGenericTypes = ((ParameterizedType) genericParameters[0]).getActualTypeArguments();
                }

                if (actualGenericTypes == null
                        || actualGenericTypes.length != 1
                        || !PacketSendEvent.class.isAssignableFrom(parameters[0])
                        || !Packet.class.isAssignableFrom((Class<?>) actualGenericTypes[0])) {
                    Console.error("FAIL_REGISTER_EVENT", instance.getClass().getSimpleName());
                    continue;
                }

                addEventListener((Class<? extends Packet>) actualGenericTypes[0], new EventEntry(method, instance));
            }
        }
    }

    private static void addEventListener(Class<? extends Packet> event, EventEntry data) {
        boolean listExists = eventListeners.containsKey(event);
        List<EventEntry> events = listExists ? eventListeners.get(event) : new ArrayList<>();

        if (!data.getCallback().isAccessible()) {
            data.getCallback().setAccessible(true);
        }

        events.add(data);

        if (!listExists) {
            eventListeners.put(event, events);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class EventEntry {
        private Method callback;
        private Object sourceInstance;
    }
}

