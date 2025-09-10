package me.kryptxn.cobraclient.event.events;

import me.kryptxn.cobraclient.event.Event;
import net.minecraft.network.packet.Packet;

public class EventReceivePacket extends Event {
    private Packet packet;

    public EventReceivePacket(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
