package d4rk.mc.playerai.script;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskMineBlock extends ScriptTask
{
    private BlockWrapper target = null;
    private int side = 1;

    public TaskMineBlock(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);
        Vec3D target = new Vec3D(pWrap.player);
        target.x += 1;

        if (cmd.length < 4)
        {
            throw new TooFewArgumentsException(cmd[0]);
        }

        side = (cmd.length > 4) ? pWrap.blockSideFromString(cmd[4]) : -1;
        target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
        this.target = new BlockWrapper(target, pWrap.player.worldObj);

        if (side == -1)
        {
            side = this.target.getNearestSide(pWrap.getPosition());
        }

        if (!pWrap.canReach(this.target, side))
        {
            this.done(OUT_OF_RANGE);
            return;
        }

        if (!this.target.isMineable())
        {
            this.done(BLOCK_NOT_MINEABLE);
            return;
        }

        Vec3D vec = this.target.getSideCoords(side);
        this.startScript(new String[]
                {
                    "nolog: lookat " + vec.x + " " + vec.y + " " + vec.z,
                    "nolog: wait 1"
                });
        pWrap.selectBlock(this.target, side);
    }

    public TaskMineBlock(String name)
    {
        super(name);
    }

    @Override
    public void onTick()
    {
        if (runScriptParser())
        {
            return;
        }

        if (isDone() || isStopped)
        {
            return;
        }

        pWrap.lookAt(target.getSideCoords(side));
        pWrap.selectToolForBlock(target);

        if (!target.isMineable())
        {
            pWrap.stopMining();
            done(NONE);
            return;
        }

        pWrap.updateMining();
    }
}
