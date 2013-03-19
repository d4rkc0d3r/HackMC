package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskMoveTo extends ScriptTask
{
    private Vec3D target = null;
    private double minDist = 0.5;

    public TaskMoveTo(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);

        if (cmd.length < 4)
        {
            throw new TooFewArgumentsException(cmd[0]);
        }

        try
        {
            target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
            minDist = Double.valueOf(cmd[4]);
        }
        catch (ArrayIndexOutOfBoundsException e) {}

        if (pWrap.getDistance(target) < minDist)
        {
            this.done(NONE);
        }
    }

    public TaskMoveTo(String name)
    {
        super(name);
    }

    public void onTick()
    {
        if (this.isStopped || isDone())
        {
            return;
        }

        if (this.runScriptParser())
        {
            return;
        }

        Vec3D moveVec = new Vec3D(pWrap.player).sub(target).mul(-1);
        pWrap.addVelocity(moveVec);

        if (new Vec3D(pWrap.player).dist(target) < minDist)
        {
            done(NONE);
        }
    }
}
