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
    private static final Map<Class<? extends Packet>, List<EventEntry>> eventMap = new HashMap<>();
    private static final Map<Object, Map<Class<? extends Packet>, List<EventEntry>>> moduleMap = new HashMap<>();

    public static void register(Object listener) {
        registerClassEvents(listener);
    }

    public static void unregister(Object listener) {
        moduleMap.remove(listener);
        updateEventMap();
    }

    public static void unregisterAll() {
        eventMap.clear();
        moduleMap.clear();
    }

    public static PacketSendEvent<? extends Packet> call(Packet packet, PacketInfo.PacketState state, PacketInfo.PacketDirection direction) {
        PacketSendEvent<? extends Packet> event = new PacketSendEvent<>(state, direction, packet);

        // If the packet wasn't recognized by its ID, it doesn't have any events
        if(packet == null) {
            return event;
        }

        synchronized (eventMap) {
            List<List<EventEntry>> eventEntiresSq = eventMap.keySet()
                    .stream()
                    .filter(entry -> entry.isAssignableFrom(packet.getClass()))
                    .map(eventMap::get)
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
        Map<Class<? extends Packet>, List<EventEntry>> map = new HashMap<>();

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

                addEventListener(map, (Class<? extends Packet>) actualGenericTypes[0], new EventEntry(method, instance));
            }
        }

        moduleMap.put(instance, map);
        updateEventMap();
    }

    private static void addEventListener(
            Map<Class<? extends Packet>, List<EventEntry>> map,
            Class<? extends Packet> event,
            EventEntry data) {
        List<EventEntry> events = map.getOrDefault(event, new ArrayList<>());

        if (!data.getCallback().isAccessible()) {
            data.getCallback().setAccessible(true);
        }

        events.add(data);

        if (events.size() == 1) {
            map.put(event, events);
        }
    }

    private static void updateEventMap() {
        synchronized (eventMap) {
            eventMap.clear();

            for (Map<Class<? extends Packet>, List<EventEntry>> map : moduleMap.values()) {
                for (Map.Entry<Class<? extends Packet>, List<EventEntry>> entry : map.entrySet()) {
                    for (EventEntry eventEntry : entry.getValue()) {
                        addEventListener(eventMap, entry.getKey(), eventEntry);
                    }
                }
            }
        }
    }

    @Getter
    @AllArgsConstructor
    private static class EventEntry {
        private Method callback;
        private Object sourceInstance;
    }
}

