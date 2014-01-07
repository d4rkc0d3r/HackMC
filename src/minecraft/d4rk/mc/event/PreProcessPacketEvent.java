package d4rk.mc.event;

import net.minecraft.src.Packet;

public class PreProcessPacketEvent extends DisableEvent {
	private final Packet packet;
	private final boolean isAsync;
	
	public PreProcessPacketEvent(Packet packet, boolean isAsync) {
		this.packet = packet;
		this.isAsync = isAsync;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public boolean isAsync() {
		return isAsync;
	}
}
