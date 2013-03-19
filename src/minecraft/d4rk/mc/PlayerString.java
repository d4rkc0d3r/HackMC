package d4rk.mc;

import d4rk.mc.event.listener.PMIgnore;
import net.minecraft.src.EntityPlayer;

public class PlayerString
{
    private String name;
    private String rank;
    private String toString;

    public PlayerString(String rankAndName)
    {
        if (rankAndName.equals("mir"))
        {
            name = Hack.getPlayerName();
            rank = "LOCALE";
            toString = "mir";
            return;
        }

        int rankStart = rankAndName.indexOf('[');
        int rankEnd = rankAndName.indexOf(']');

        if (rankStart == -1 || rankEnd == -1)
        {
            rank = "null";
            name = ChatColor.remove(rankAndName.replace("<3", ""));
        }
        else
        {
            rank = ChatColor.remove(rankAndName.substring(rankStart + 1, rankEnd));
            name = ChatColor.remove(rankAndName.substring(rankEnd + 1));
        }

        calcToString();
    }

    public PlayerString(String name, String rank)
    {
        this.name = String.valueOf(name);
        this.rank = String.valueOf(rank);
        calcToString();
    }

    public PlayerString(PlayerString player)
    {
        name = player.name;
        rank = player.rank;
        toString = player.toString;
    }

    public String getName()
    {
        return name;
    }

    /**
     * @return <code>"null"</code> if no rank was found or specified.
     */
    public String getRank()
    {
        return rank;
    }

    public boolean hasPermission(int permLevel)
    {
        return Permission.has(this, permLevel);
    }

    public void send(String msg)
    {
        if (this.equals(ME))
        {
            ImproveChat.addToChatGui(msg);
        }
        else
        {
            Hack.sendChatMessage("/tell " + name + " " + ChatColor.remove(msg));
        }
    }

    public void sendSilent(String msg)
    {
        if (this.equals(ME))
        {
            ImproveChat.addToChatGui(msg);
        }
        else
        {
            msg = ChatColor.remove(msg);
            Hack.sendChatMessage("/tell " + name + " " + msg);
            PMIgnore.addOnce(msg);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        try
        {
            PlayerString p = (PlayerString) obj;
            return toString.equals(p.toString);
        }
        catch (Exception e) {}

        try
        {
            EntityPlayer p = (EntityPlayer) obj;
            return name.equals(p.username);
        }
        catch (Exception e) {}

        return false;
    }

    @Override
    public int hashCode()
    {
        return toString.hashCode();
    }

    @Override
    public String toString()
    {
        return toString;
    }

    private void calcToString()
    {
        toString = (rank.equals("LOCALE")) ? "mir" : (rank.equals("null")) ? name : '[' + rank + ']' + name;
    }

    public static final PlayerString ME = new PlayerString("mir");
}
