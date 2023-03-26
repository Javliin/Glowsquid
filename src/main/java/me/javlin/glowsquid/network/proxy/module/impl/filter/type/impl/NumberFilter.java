package me.javlin.glowsquid.network.proxy.module.impl.filter.type.impl;

import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.network.proxy.module.impl.filter.Filter;
import me.javlin.glowsquid.network.proxy.module.impl.filter.type.FilterType;
import me.javlin.glowsquid.network.util.UtilDataType;

import java.math.BigDecimal;


public class NumberFilter extends FilterType<Number> {
    public NumberFilter(String value) {
        super(value);
    }

    @Override
    public boolean compare(Filter.Condition condition, Object value) {
        Number num = parse(value);

        if (num == null) {
            Console.error("FILTER_CMP_FAIL", getClass().getSimpleName());
            return true;
        }

        switch (condition) {
            case EQUAL:
                return num.doubleValue() == this.value.doubleValue();
            case MORE:
                return num.doubleValue() > this.value.doubleValue();
            case LESS:
                return num.doubleValue() < this.value.doubleValue();
            default:
                return true;
        }
    }

    @Override
    public Number modify(Filter.Modify modify, Object value) {
        Number num = parse(value);

        if (num == null) {
            Console.error("FILTER_MOD_FAIL", getClass().getSimpleName());
            return null;
        }

        switch (modify) {
            case SET:
                num = this.value;
                break;
            case INCREMENT:
                num = num.doubleValue() + this.value.doubleValue();
                break;
            case DECREMENT:
                num = num.doubleValue() - this.value.doubleValue();
        }

        if (value instanceof Integer) {
            return num.intValue();
        } else if (value instanceof Float) {
            return num.floatValue();
        } else if (value instanceof Double) {
            return num.doubleValue();
        } else if (value instanceof Short) {
            return num.shortValue();
        } else if (value instanceof Byte) {
            return num.byteValue();
        }

        return num;
    }

    @Override
    protected Number parse(Object value) throws IllegalArgumentException {
        if (value instanceof Number) {
            return (Number) value;
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException(String.format(Console.get("FILTER_NUM_BAD_VAL"), value));
        }

        String str = (String) value;

        if (!UtilDataType.isNumeric(str)) {
            throw new IllegalArgumentException(String.format(Console.get("FILTER_NUM_BAD_VAL"), value));
        }

        return Double.parseDouble(str);
    }
}
