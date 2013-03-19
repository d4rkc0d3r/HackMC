package d4rk.mc.event.listener.secretcraft;

import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.PlayerString;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PlayerChatEvent;
import d4rk.mc.event.PrivateMessageEvent;

public class PMParser implements EventListener
{
    public void onChatEvent(ChatEvent e)
    {
        PrivateMessageEvent msg = parse(ChatColor.remove(e.getSrc()));

        if (msg == null)
        {
            return;
        }

        EventManager.fireEvent(msg);
        e.setDisabled(msg.isDisabled());
    }

    public static PrivateMessageEvent parse(String str)
    {
        // TODO: make incoming PM configurable
        String b = "[";
        String e = " -> mir] ";
        String msg = null;
        PlayerString playerTo;
        PlayerString playerFrom;

        try
        {
            int start = str.indexOf(b);
            int end = str.indexOf(e);

            if (start == -1 || end == -1)
            {
                throw new Exception();
            }

            playerFrom = new PlayerString(str.substring(start + b.length(), end));
            playerTo = PlayerString.ME;
            msg = str.substring(end + e.length());
            return new PrivateMessageEvent(msg, playerFrom, playerTo);
        }
        catch (Exception ex) {}

        // TODO: make incoming PM configurable
        b = "[mir -> ";
        e = "] ";

        try
        {
            int start = str.indexOf(b);
            int end = str.indexOf(e);

            if (start == -1 || end == -1)
            {
                throw new Exception();
            }

            playerTo = new PlayerString(str.substring(start + b.length(), end));
            playerFrom = PlayerString.ME;
            msg = str.substring(end + e.length());
            return new PrivateMessageEvent(msg, playerFrom, playerTo);
        }
        catch (Exception ex) {}

        return null;
    }
}
