package me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.FilterType;

import java.util.Objects;

public class StringFilter extends FilterType<String> {
    public StringFilter(String value) {
        super(value);
    }

    @Override
    public boolean compare(Filter.Condition condition, Object value) {
        String str = parse(value);

        if (str == null) {
            Console.error("FILTER_CMP_FAIL", getClass().getSimpleName());
            return true;
        }

        if (condition != Filter.Condition.EQUAL) {
            Console.warn("FILTER_STR_BAD_CON");
            return true;
        }

        return Objects.equals(value, this.value);
    }

    @Override
    public String modify(Filter.Modify modify, Object value) {
        String str = parse(value);

        if (str == null) {
            Console.error("FILTER_MOD_FAIL", getClass().getSimpleName());
            return null;
        }

        if (modify != Filter.Modify.SET) {
            Console.warn("FILTER_STR_BAD_MOD");
            return str;
        }

        return this.value;
    }

    @Override
    protected String parse(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(Console.get("FILTER_STR_BAD_VAL"));
        }

        return (String) value;
    }
}
