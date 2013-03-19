package d4rk.mc.event.listener;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Vec3;

import d4rk.mc.BlockWrapper;
import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.ParticelMark;
import d4rk.mc.Permission;
import d4rk.mc.PlayerString;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.TickEvent;
import d4rk.mc.util.Vec3D;

public class ParticelMarker implements EventListener
{
    private LinkedList<ParticelMark> list = new LinkedList();
    private Minecraft mc;

    public ParticelMarker()
    {
        instance = this;
        mc = Minecraft.getMinecraft();
    }

    public void onTick(TickEvent event)
    {
        Vec3D from = Hack.getPlayerWrapper().getPosition();

        for (ParticelMark pm : list)
        {
            pm.from = from;
            pm.onTick();
        }
    }

    public void onCommandEvent(CommandEvent event)
    {
        if (event.getCommand().equalsIgnoreCase("mark"))
        {
            event.setDisabled(true);

            if (!event.getSender().hasPermission(Permission.LOCALE))
            {
                event.getSender().sendSilent(Permission.NO_PERMISSION);
                return;
            }

            PlayerString sender = event.getSender();
            String cmd = event.getArg(4).toLowerCase();

            if (cmd.isEmpty())
            {
                cmd = "lineto";
            }

            Vec3D from = Hack.getPlayerWrapper().getPosition();
            Vec3D to = new Vec3D(new BlockWrapper(Hack.getPlayerWrapper().getPosition(
                    event.getArg(1), event.getArg(2), event.getArg(3))));

            if (event.getArg(1).equals("clear"))
            {
                list.clear();
                return;
            }

            list.add(new ParticelMark(from, to, "lineto"));
        }
    }

    public static ParticelMarker getInstance()
    {
        return instance;
    }

    private static ParticelMarker instance = null;
}
