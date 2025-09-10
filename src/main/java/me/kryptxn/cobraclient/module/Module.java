package me.kryptxn.cobraclient.module;

import me.kryptxn.cobraclient.CobraClient;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private String name;
    private boolean toggle;
    private int keyCode;
    private Category category;
    private boolean selected;

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, int keyCode, Category category) {
        this.name = name;
        this.keyCode = keyCode;
        this.category = category;
        this.toggle = false;
    }

    public void toggle() {
        this.toggle = !toggle;
        if(toggle) {
            onEnable();
        }else {
            onDisable();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    protected void onEnable() {
        CobraClient.Instance.eventManager.register(this);
    }

    protected void onDisable() {
        CobraClient.Instance.eventManager.unregister(this);
    }

    public String getName() {
        return name;
    }

    public boolean isToggle() {
        return toggle;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public Category getCategory() {
        return category;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
