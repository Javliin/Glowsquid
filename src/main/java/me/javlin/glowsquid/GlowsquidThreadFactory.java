package me.javlin.glowsquid;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class GlowsquidThreadFactory implements ThreadFactory {
    private final String name;

    private int threadCount = -1;

    public GlowsquidThreadFactory(String name) {
        this.name = name + "-%d";
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        threadCount++;
        return new Thread(runnable, String.format(name, threadCount));
    }
}
