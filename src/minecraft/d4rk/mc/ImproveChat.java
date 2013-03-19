package d4rk.mc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.PlayerChatEvent;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.listener.PMIgnore;

public class ImproveChat
{
//	static private String[] msgFormat = {"[", " -> mir] "};
//	static private String[] sendFormat = {"[mir -> ", "] "};
    static private String[] msgFormat = {"", " whispers "};
    static private String[] sendFormat = {"You whisper to ", " "};

    static public boolean onReceiveMsg(String str)
    {
        String msg = ChatColor.remove(str);

        if (msg.indexOf(msgFormat[1]) != -1)
        {
            int index = msg.indexOf(msgFormat[1]) + msgFormat[1].length();
            String nachricht = msg.substring(index);
            String sender = "KeinSender";

            if (msg.indexOf(']') != -1 && msg.indexOf(']') < msg.indexOf(msgFormat[1]))
            {
                sender = msg.substring(msg.indexOf(']') + 1, msg.indexOf(msgFormat[1]));
            }
            else
            {
                sender = msg.substring(msg.indexOf(msgFormat[0]) + msgFormat[0].length(), msg.indexOf(msgFormat[1]));
            }

            String rang = "";

            if (msg.indexOf('[', msgFormat[0].length()) != -1 && msg.indexOf(']') != -1)
            {
                rang = msg.substring(msg.indexOf('[', 1) + 1, msg.indexOf(']'));
            }

            if (rang.equals(""))
            {
                sender = msg.substring(msg.indexOf("<") + 1, msg.indexOf(msgFormat[1]));
            }

            Hack.log("[MSG] " + msg);
            CommandManager.parse(nachricht, sender, rang);
            return CommandManager.isInputMessageHidden(nachricht);
        }

        if (msg.indexOf(sendFormat[0]) == 0)
        {
            Hack.log("[MSG] " + msg);
            int begin = msg.indexOf(sendFormat[0]) + sendFormat[0].length();
            int end = msg.substring(begin).indexOf(sendFormat[1]) + sendFormat[1].length();
            return CommandManager.isOutputMessageHidden(msg.substring(end));
        }

        return CommandManager.parseLocal(msg);
    }

    /**
     * This method formats the string with key word coloring and puts it to the GUI.<br>
     * Currently only the player name is colored in pink.
     */
    static public void addToChatGui(String msg)
    {
        Hack.mc.ingameGUI.getChatGUI().printChatMessage(msg);
    }

    /**
     * For mc.secretcraft.de use ImproveChat.setMSGFormat("[? -> mir] ");
     */
    static public void setMSGFormat(String str)
    {
        if (str.indexOf('?') == -1)
        {
            msgFormat = new String[2];
            msgFormat[0] = "";
            msgFormat[1] = new String(str);
        }
        else
        {
            msgFormat = str.split("\\?", 2);
        }

        Hack.logInfo("msgFormat[0]: \"" + msgFormat[0] + "\"");
        Hack.logInfo("msgFormat[1]: \"" + msgFormat[1] + "\"");
    }

    /**
     * For mc.secretcraft.de use ImproveChat.setSendFormat("[mir -> ?] ");
     */
    static public void setSendFormat(String str)
    {
        if (str.indexOf('?') == -1)
        {
            sendFormat = new String[2];
            sendFormat[0] = "";
            sendFormat[1] = new String(str);
        }
        else
        {
            sendFormat = str.split("\\?", 2);
        }

        Hack.logInfo("sendFormat[0]: \"" + sendFormat[0] + "\"");
        Hack.logInfo("sendFormat[1]: \"" + sendFormat[1] + "\"");
    }

    static public void sendMessage(String[] p, String msg)
    {
        for (String player : p)
        {
            sendMessage(player, msg);
        }
    }

    /**
     * @deprecated Use the {@link d4rk.mc.PlayerString Player} class instead
     */
    static public void sendMessage(String player, String msg)
    {
        if (player == null || msg == null)
        {
            Hack.logWarning("sendMessage got null pointers");
            return;
        }

        if (player.equals(Hack.mc.thePlayer.getEntityName()))
        {
            addToChatGui("§e" + msg);
        }
        else
        {
            Hack.sendChatMessage("/tell " + player + " " + ChatColor.remove(msg));
        }
    }

    /**
     * @deprecated just use Hack.log("[MSG] "+fullMsg); instead
     */
    static public void logMessageReceive(String sender, String rank, String msg)
    {
        Hack.log("[MSG] [" + ((rank.isEmpty()) ? "" : "[" + rank + "]") + sender + " -> mir] " + msg);
    }

    /**
     * @deprecated just use Hack.log("[MSG] "+fullMsg); instead
     */
    static public void logMessageSend(String receiver, String rank, String msg)
    {
        Hack.log("[MSG] [mir -> [" + rank + "]" + receiver + "] " + msg);
    }
}
