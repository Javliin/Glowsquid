package me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.FilterType;

public class BooleanFilter extends FilterType<Boolean> {
    public BooleanFilter(String value) {
        super(value);
    }

    @Override
    public boolean compare(Filter.Condition condition, Object value) {
        Boolean bool = parse(value);

        if (bool == null) {
            Console.error("FILTER_CMP_FAIL", getClass().getSimpleName());
            return true;
        }

        if (condition != Filter.Condition.EQUAL) {
            Console.warn("FILTER_BOOL_BAD_CON");
            return true;
        }

        return value == this.value;
    }

    @Override
    public Boolean modify(Filter.Modify modify, Object value) {
        Boolean bool = parse(value);

        if (bool == null) {
            Console.error("FILTER_MOD_FAIL", getClass().getSimpleName());
            return null;
        }

        if (modify != Filter.Modify.SET) {
            Console.warn("FILTER_BOOL_BAD_MOD");
            return bool;
        }

        return this.value;
    }

    @Override
    protected Boolean parse(Object value) throws IllegalArgumentException {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException(String.format(Console.get("FILTER_BOOL_BAD_VAL"), value));
        }

        String str = (String) value;

        if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException(String.format(Console.get("FILTER_BOOL_BAD_VAL"), value));
        }

        return Boolean.parseBoolean(str);
    }
}
