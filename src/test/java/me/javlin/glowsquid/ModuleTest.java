package me.javlin.glowsquid;

import me.javlin.glowsquid.dummy.DummyModule;
import me.javlin.glowsquid.dummy.DummySession;
import me.javlin.glowsquid.network.proxy.module.ModuleManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ModuleTest {
    @Test
    public void createModule() {
        ModuleManager manager = new ModuleManager();
        manager.register(DummyModule.class);
        Assertions.assertEquals(1, manager.getCoreModules().size());
        manager.unregister();
    }
}
