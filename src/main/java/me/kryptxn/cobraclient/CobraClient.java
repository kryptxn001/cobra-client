package me.kryptxn.cobraclient;

import me.kryptxn.cobraclient.event.EventManager;
import me.kryptxn.cobraclient.event.EventTarget;
import me.kryptxn.cobraclient.event.events.EventKeyboard;
import me.kryptxn.cobraclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class CobraClient {
    public static final String NAME = "Cobra Client", VERSION = "1.0", AUTHOR = "Kryptxn001";
    public static CobraClient Instance = new CobraClient();
    public EventManager eventManager;
    public ModuleManager moduleManager;
    public void init() {
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        
        moduleManager.loadModules();
    }
}
