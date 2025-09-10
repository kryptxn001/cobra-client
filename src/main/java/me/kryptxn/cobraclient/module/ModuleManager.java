package me.kryptxn.cobraclient.module;

import me.kryptxn.cobraclient.CobraClient;
import me.kryptxn.cobraclient.event.EventTarget;
import me.kryptxn.cobraclient.event.events.EventKeyboard;
import me.kryptxn.cobraclient.module.modules.ChestESP;
import me.kryptxn.cobraclient.module.modules.EntityESP;
import me.kryptxn.cobraclient.module.modules.KillAura;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;


import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    private CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList();

    public ModuleManager() {
        CobraClient.Instance.eventManager.register(this);
    }

    public void loadModules() {
        addModule(new EntityESP());
        addModule(new KillAura());
        addModule(new ChestESP());
    }

    @EventTarget
    public void onKey(EventKeyboard e) {
        if(e.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        if(MinecraftClient.getInstance().currentScreen != null) {
            return;
        }

        /**
        if(e.getKeyCode() == GLFW.GLFW_KEY_LEFT_ALT) {
            ValueManager.getValue("downmode","scaffold").setValue(!(boolean) (ValueManager.getValue("downmode","scaffold").getValue()));
            return;
        }

        Orfeus.Instance.tabGuiManager.ProccesKeys(e.getKeyCode());
        **/

        for (Module m : modules) {
            if (m.getKeyCode() == e.getKeyCode()) {
                m.toggle();
            }
        }
    }

    public CopyOnWriteArrayList<Module> getEnabledModules() {
        CopyOnWriteArrayList<Module> catmodules = new CopyOnWriteArrayList();

        for(Module m : modules) {
            if (m.isToggle()) {
                catmodules.add(m);
            }
        }
        return catmodules;
    }

    public void addModule(Module m) {
        modules.add(m);
    }

    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }

    public Module getModule(String name) {
        for (Module m : modules) {
            if(m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public Module getModule(Class<? extends Module> moduleclass) {
        for (Module m : modules) {
            if(m.getClass().equals(moduleclass)) {
                return m;
            }
        }
        return null;
    }


    public CopyOnWriteArrayList<Module> getModulesInCategory(Category category) {
        CopyOnWriteArrayList<Module> catmodules = new CopyOnWriteArrayList();

        for(Module m : modules) {
            if (m.getCategory().equals(category)) {
                catmodules.add(m);
            }
        }
        return catmodules;
    }
}
