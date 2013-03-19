package d4rk.mc.event;

import net.minecraft.src.Packet4UpdateTime;

public class TimeUpdateEvent extends BaseEvent
{
    private Packet4UpdateTime packet = null;

    public TimeUpdateEvent(Packet4UpdateTime packet)
    {
        this.packet = packet;
    }

    public long getTime()
    {
        return packet.time;
    }

    public void setTime(long time)
    {
        packet.time = time;
    }
}
