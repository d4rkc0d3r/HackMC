package d4rk.mc.playerai.script;

import net.minecraft.src.Packet14BlockDig;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskLeftClick extends ScriptTask
{
    private Vec3D target = null;
    private int side = 1;

    public TaskLeftClick(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);
        target = new Vec3D(pWrap.player);
        target.x += 1;

        try
        {
            side = (cmd.length > 4) ? pWrap.blockSideFromString(cmd[4]) : pWrap.getLookDir();
            target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new TooFewArgumentsException(cmd[0]);
        }
    }

    public TaskLeftClick(String name)
    {
        super(name);
    }

    public void onTick()
    {
        if (isStopped)
        {
            return;
        }

        if (isDone())
        {
            return;
        }

        pWrap.ctrl.clickBlock(target.getX(), target.getY(), target.getZ(), side);
        done(NONE);
    }
}
