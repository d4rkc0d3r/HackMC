package d4rk.mc.event.listener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import d4rk.mc.Hack;
import d4rk.mc.PlayerString;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PrivateMessageEvent;
import d4rk.mc.util.FileIO;

public class PMLogger implements EventListener
{
    private boolean show = false;

    public PMLogger()
    {
        lastInstance = this;
    }

    public void onPM(PrivateMessageEvent e)
    {
        if (show)
        {
            System.out.println("[MSG] " + e);
        }

        try
        {
            String name = (e.sender.equals(PlayerString.ME)) ? e.receiver.getName() : e.sender.getName();
            Writer output = new BufferedWriter(FileIO.createWriter(Hack.getHackDir() + "/log/msg/" + name + ".log", true));
            output.append(Hack.getCurrentDateAndTime() + " " + e + "\r\n");
            output.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void showOnConsole(boolean show)
    {
        lastInstance.show = show;
    }

    private static PMLogger lastInstance = new PMLogger();
}
