package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskWaitForMove extends ScriptTask
{
    private Vec3D start = null;
    private double dist;

    public TaskWaitForMove(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);

        try
        {
            dist = Double.valueOf(cmd[1]);
        }
        catch (Exception e)
        {
            dist = 2D;
        }

        start = pWrap.getPosition();
    }

    public TaskWaitForMove(String name)
    {
        super(name);
    }

    public void onTick()
    {
        if (isDone() || isStopped)
        {
            return;
        }

        if (start.dist(pWrap.getPosition()) > dist)
        {
            done(NONE);
        }
    }
}
