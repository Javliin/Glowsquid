package me.javlin.glowsquid.network.proxy.module.impl.filter.type;

import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl.BooleanFilter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl.NumberFilter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl.StringFilter;

import java.util.HashMap;
import java.util.Map;

public abstract class FilterType<T> {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<Class<?>, Class<?>>(){{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(short.class, Short.class);
    }};

    protected T value;

    protected FilterType(String value) {
        this.value = parse(value);
    }

    public abstract boolean compare(Filter.Condition condition, Object value);
    public abstract T modify(Filter.Modify modify,Object value);

    protected abstract T parse(Object value);

    public static FilterType<?> create(String value, Class<?> type) throws IllegalArgumentException {
        type = PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);

        if (Boolean.class.isAssignableFrom(type)) {
            return new BooleanFilter(value);
        } else if (Number.class.isAssignableFrom(type)) {
            return new NumberFilter(value);
        } else if (String.class.isAssignableFrom(type)) {
            return new StringFilter(value);
        }

        return null;
    }
}
