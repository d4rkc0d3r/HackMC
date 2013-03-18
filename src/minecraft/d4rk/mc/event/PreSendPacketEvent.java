package d4rk.mc.event;

import net.minecraft.src.Packet;

public class PreSendPacketEvent extends DisableEvent {
	private final Packet packet;
	
	public PreSendPacketEvent(Packet packet) {
		this.packet = packet;
	}
	
	public Packet getPacket() {
		return packet;
	}
}
