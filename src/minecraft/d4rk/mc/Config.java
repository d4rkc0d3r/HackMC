package d4rk.mc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Config
{
    protected String name = "";

    protected HashMap<String, String> cache = new HashMap<String, String>();

    public Config() {}

    public Config(String fileName)
    {
        load(fileName);
    }

    public void load()
    {
        load(name);
    }

    public void load(String fileName)
    {
        name = fileName;
        File oFile = new File(fileName);
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

                cache.put(str[0].trim(), str[1].trim());
            }

            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }

    public int getInteger(String key)
    {
        try
        {
            return Integer.valueOf(cache.get(key));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public float getFloat(String key)
    {
        try
        {
            return Float.valueOf(cache.get(key));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public double getDouble(String key)
    {
        try
        {
            return Double.valueOf(cache.get(key));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public boolean getBoolean(String key)
    {
        try
        {
            return Boolean.valueOf(cache.get(key));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String getString(String key)
    {
        String val = cache.get(key);
        return (val == null) ? "" : val;
    }
}
