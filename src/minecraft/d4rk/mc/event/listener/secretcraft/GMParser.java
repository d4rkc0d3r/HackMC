package d4rk.mc.event.listener.secretcraft;

import d4rk.mc.PlayerString;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.GlobalMessageEvent;

public class GMParser implements EventListener
{
    public void onChatEvent(ChatEvent e)
    {
        GlobalMessageEvent gm = parse(e.message);

        if (gm == null)
        {
            return;
        }

        EventManager.fireEvent(gm);
        e.setDisabled(gm.isDisabled());
    }

    public static GlobalMessageEvent parse(String str)
    {
        try
        {
            int index = str.indexOf(": ");
            PlayerString player = new PlayerString(str.substring(0, index));

            if (player.getRank().equals("null"))
            {
                return null;
            }

            return new GlobalMessageEvent(player, str.substring(index + 2, str.length()));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
