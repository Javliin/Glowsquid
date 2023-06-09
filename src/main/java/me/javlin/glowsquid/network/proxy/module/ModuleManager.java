package me.javlin.glowsquid.network.proxy.module;

import lombok.Getter;
import lombok.Setter;
import me.javlin.glowsquid.Console;
import me.javlin.glowsquid.mc.Player;
import me.javlin.glowsquid.network.proxy.ProxySession;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@Getter
public class ModuleManager {
    private static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> coreModules = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    private final ConcurrentHashMap<Integer, Player> playerList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Long> recentlyAttackedPlayers = new ConcurrentHashMap<>();

    private final Player player = new Player(0, 0, 0);

    @Setter
    protected ProxySession session;

    public ModuleManager register(Class<? extends Module> clazz) {
        return register(clazz, true);
    }

    public ModuleManager register(Class<? extends Module> clazz, boolean core) {
        try {
            Module module = clazz.getConstructor().newInstance();

            if (session != null) {
                module.setEnabled(true);
            }

            if (core) {
                coreModules.add(module);
            } else {
                modules.add(module);
            }
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException exception) {
            Console.error("MODULE_FAIL_CREATE", clazz.getSimpleName());
            exception.printStackTrace();
        }

        return this;
    }

    public ModuleManager unregister(Module module) {
        modules.remove(module);
        module.setEnabled(false);

        return this;
    }

    @SuppressWarnings("unchecked")
    public List<Module> load(File file) {
        List<Module> loadedModules = new ArrayList<>();

        try (
                JarFile jarFile = new JarFile(file);
                URLClassLoader loader = URLClassLoader.newInstance(
                        new URL[]{file.toURI().toURL()},
                        ModuleManager.class.getClassLoader()
                )
        ) {
            jarFile.stream()
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .map(jarEntry -> jarEntry.getName().replace("/", "."))
                    .map(name -> name.substring(0, name.length() - 6))
                    .forEach(name -> {
                        try {
                            Class<?> clazz = Class.forName(name, true, loader);

                            if (Module.class.isAssignableFrom(clazz)) {
                                register((Class<? extends Module>) clazz, false);
                                loadedModules.add(modules.get(modules.size() - 1));
                            }
                        } catch (ClassNotFoundException exception) {
                            Console.error("MODULE_FAIL_LOAD_CLASS", name);
                            exception.printStackTrace();
                        }
                    });
        } catch (Throwable exception) {
            Console.error("MODULE_FAIL_LOAD");
            exception.printStackTrace();
        }

        Console.info("MODULES_LOADED", loadedModules.size(), file.getName());

        return loadedModules;
    }

    public void loadModulesDirectory() {
        if (!new File("modules").exists()) {
            if (!new File("modules").mkdir()) {
                Console.warn("MODULE_FOLDER_FAIL_CREATE");
            }
        }

        try (Stream<Path> paths = Files.walk(Paths.get("modules"))) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> load(path.toFile()));
        } catch (IOException exception) {
            Console.warn("MODULE_FOLDER_FAIL_LOAD");
            exception.printStackTrace();
        }
    }

    public void unregisterAll() {
        coreModules.forEach(module -> module.setEnabled(false));
        modules.forEach(module -> module.setEnabled(false));
        modules.clear();
    }

    public static ModuleManager getInstance() {
        return INSTANCE;
    }
}
