package d4rk.mc.event.listener;

import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.PlayerString;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.PlayerChatEvent;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PrivateMessageEvent;

public class CommandParser implements EventListener
{
    public void onPlayerChatEvent(PlayerChatEvent e)
    {
        if (!e.message.startsWith("/"))
        {
            return;
        }

        CommandEvent cmd = new CommandEvent(e.message.substring(1).split(" "), PlayerString.ME);
        EventManager.fireEvent(cmd);
        e.setDisabled(cmd.isDisabled());
    }

    public void onPMEvent(PrivateMessageEvent e)
    {
        if (!e.msg.startsWith("/"))
        {
            return;
        }

        if (e.sender.equals(PlayerString.ME))
        {
            return;
        }

        CommandEvent cmd = new CommandEvent(ChatColor.remove(e.msg.substring(1)).split(" "), e.sender);
        EventManager.fireEvent(cmd);
        e.setDisabled(cmd.isDisabled());
    }
}
