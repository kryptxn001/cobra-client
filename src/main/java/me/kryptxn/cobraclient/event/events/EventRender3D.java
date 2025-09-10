package me.kryptxn.cobraclient.event.events;


import me.kryptxn.cobraclient.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRender3D extends Event {

	public float tickDelta;
	public MatrixStack matrixStack;

	public EventRender3D(MatrixStack matrixStack, float tickDelta) {
		this.tickDelta = tickDelta;
		this.matrixStack = matrixStack;
	}

}
