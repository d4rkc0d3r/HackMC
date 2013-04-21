package d4rk.mc.util;

import net.minecraft.src.Packet;

public class UniquePacket {
	private Packet p = null;
	private long id = 0;
	
	public UniquePacket(Packet p) {
		this.p = p;
		id = currentID++;
	}
	
	public Packet getPacket() {
		return p;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof UniquePacket) ? ((UniquePacket)o).id == id : false;
	}
	
	private static long currentID = 0;
}
