package d4rk.mc.playerai.script;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.Item;
import d4rk.mc.PlayerWrapper;

public class TaskCollectItemsOnChunk extends ScriptTask
{
    int index = -1;
    ArrayList<EntityItem> targets = new ArrayList<EntityItem>();

    public TaskCollectItemsOnChunk(String[] cmd, PlayerWrapper pWrap)
    {
        super(cmd, pWrap);

        // get all the dropped items in the players chunk
        for (List l : pWrap.getChunk().entityLists)
            for (int i = 0; i < l.size(); i++)
                if (l.get(i) instanceof EntityItem)
                {
                    targets.add((EntityItem) l.get(i));
                }

        if (targets.size() == 0)
        {
            this.done(NONE);
        }

        selectNextTarget();
    }

    public TaskCollectItemsOnChunk(String name)
    {
        super(name);
    }

    private boolean selectNextTarget()
    {
        if (index != -1)
        {
            targets.remove(index);
        }

        if (targets.size() == 0)
        {
            this.done(NONE);
            return false;
        }

        index = pWrap.getNearest(targets);

        if (!pWrap.canSee(targets.get(index)) && !pWrap.canMoveTo(targets.get(index)))
        {
            return selectNextTarget();
        }

        return true;
    }

    public void onTick()
    {
        if (isDone() || isStopped)
        {
            return;
        }

        if (runScriptParser())
        {
            return;
        }

        EntityItem target = targets.get(index);

        if (target == null || target.isDead)
        {
            selectNextTarget();
            return;
        }

        if (pWrap.player.getDistanceToEntity(target) > 0.8)
        {
            pWrap.lookAt(target, 10);
            pWrap.addVelocityToEntity(target);
            return;
        }
    }
}
