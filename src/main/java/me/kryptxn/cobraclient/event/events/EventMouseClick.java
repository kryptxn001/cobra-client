package me.kryptxn.cobraclient.event.events;

import me.kryptxn.cobraclient.event.Event;

public class EventMouseClick extends Event {
    private clickType clickType;

    public EventMouseClick(clickType clickType) {
        this.clickType = clickType;
    }

    public enum clickType {
        LEFT,RIGHT
    }

    public EventMouseClick.clickType getClickType() {
        return clickType;
    }


}
