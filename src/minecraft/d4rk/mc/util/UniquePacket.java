package d4rk.mc.util;

import net.minecraft.src.Packet;

public class UniquePacket
{
    private Packet p = null;
    private int id = 0;

    public UniquePacket(Packet p)
    {
        this.p = p;
        id = currentID++;
    }

    public Packet getPacket()
    {
        return p;
    }

    public int hashCode()
    {
        return id;
    }

    private static int currentID = 0;
}
