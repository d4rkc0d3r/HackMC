package d4rk.mc.event;

import d4rk.mc.PlayerString;

public class CommandEvent extends DisableEvent
{
    private String[] args;
    private PlayerString sender;

    public CommandEvent(String[] args, PlayerString sender)
    {
        this.args   = args;
        this.sender = sender;
    }

    public String getArg(int i)
    {
        try
        {
            return args[i];
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String[] getArgs()
    {
        return args == null || args.length == 0 || args[0] == null ? new String[] {"null"} : args;
    }

    public String getFullString()
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String s : args)
        {
            if (!first)
            {
                sb.append(' ');
            }

            first = false;
            sb.append(s);
        }

        return sb.toString();
    }

    public String getCommand()
    {
        return args == null || args.length == 0 || args[0] == null ? "null" : args[0];
    }

    public PlayerString getSender()
    {
        return sender;
    }
}
