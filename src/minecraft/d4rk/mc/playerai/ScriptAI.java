package d4rk.mc.playerai;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.playerai.script.ScriptParser;

public class ScriptAI extends BaseAI
{
    public ScriptAI(PlayerWrapper player, String[] script)
    {
        super(player);
        this.scriptParser = new ScriptParser(script, player);
    }

    public ScriptAI(PlayerWrapper player, String scriptName)
    {
        super(player);
        this.scriptParser = new ScriptParser(scriptName, player);
    }

    public void onTick()
    {
        if (isStopped)
        {
            return;
        }

        if (!this.runScriptParser())
        {
            stop();
        }
    }
}
