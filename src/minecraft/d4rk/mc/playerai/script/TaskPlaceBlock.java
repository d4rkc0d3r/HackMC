package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

/**
 * This is currently a stub!
 */
public class TaskPlaceBlock extends ScriptTask
{
    private Vec3D target = null;
    private int itemID = 0;

    public TaskPlaceBlock(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);
    }

    public TaskPlaceBlock(String name)
    {
        super(name);
    }

    public void onTick()
    {
    }
}
