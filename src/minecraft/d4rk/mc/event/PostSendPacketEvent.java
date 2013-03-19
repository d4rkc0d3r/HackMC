package d4rk.mc.event;

import net.minecraft.src.Packet;

public class PostSendPacketEvent extends BaseEvent
{
    private final Packet packet;

    public PostSendPacketEvent(Packet packet)
    {
        this.packet = packet;
    }

    public Packet getPacket()
    {
        return packet;
    }
}
