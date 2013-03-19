package d4rk.mc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import net.minecraft.src.Packet3Chat;

import org.lwjgl.input.Keyboard;

public class KeyMakro
{
    private static HashMap<Integer, String> map = new HashMap<Integer, String>();

    public static void load(String fileName)
    {
        map.clear();
        File oFile = new File(Hack.getHackDir() + "/makro/" + fileName);
        BufferedReader file = null;

        try
        {
            file = new BufferedReader(new FileReader(oFile));
            String line = "";

            while ((line = file.readLine()) != null)
            {
                String[] str = line.split(":", 2);

                if (str.length <= 1)
                {
                    continue;
                }

                map.put(getKeyCodeFrom(str[0]), str[1]);
            }

            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }

    public static boolean onKeyPressed(int key)
    {
        String str = map.get(key);

        if (str == null)
        {
            return false;
        }

        Hack.addPacket(new Packet3Chat(str));
        return true;
    }

    public static int getKeyCodeFrom(String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e)
        {
            int res = Keyboard.getKeyIndex(str);
            return res == Keyboard.KEY_NONE ? Keyboard.getKeyIndex("KEY_" + str) : res;
        }
    }
}
