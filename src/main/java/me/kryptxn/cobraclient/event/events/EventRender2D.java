package me.kryptxn.cobraclient.event.events;



import me.kryptxn.cobraclient.event.Event;
import net.minecraft.client.util.math.MatrixStack;
public class EventRender2D extends Event {
	private final MatrixStack matrixStack;
	public EventRender2D(MatrixStack matrixStack) {
		this.matrixStack = matrixStack;
	}

	public MatrixStack getMatrixStack() {
		return matrixStack;
	}

}
