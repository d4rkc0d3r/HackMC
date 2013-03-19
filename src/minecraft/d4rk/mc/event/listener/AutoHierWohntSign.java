package d4rk.mc.event.listener;

import net.minecraft.src.ItemSign;
import d4rk.mc.ChatColor;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.GlobalMessageEvent;

public class AutoHierWohntSign implements EventListener
{
    public void onGM(ChatEvent event)
    {
        String str = ChatColor.remove(event.getSrc());

        if (!str.startsWith("Owners: "))
        {
            return;
        }

        String[] owners = str.substring(8).replace(" ", "").split(",");
        String[] lines = new String[] { "", "", "", "" };
        lines[0] = "Hier wohn" + ((owners.length > 1) ? "en" : "t");
        lines[1] = owners[0];
        lines[2] = (owners.length > 1) ? "und" : "";
        lines[3] = (owners.length > 1) ? owners[1] : "";

        for (int i = 0; i < 4; i++)
            if (lines[i].length() > 15)
            {
                lines[i] = lines[i].substring(0, 15);
            }

        ItemSign.lastText = lines.clone();
    }
}
