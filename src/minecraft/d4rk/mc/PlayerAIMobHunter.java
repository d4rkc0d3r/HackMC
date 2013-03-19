package d4rk.mc;

import java.util.ArrayList;

import d4rk.mc.util.Vec3D;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Vec3;

public class PlayerAIMobHunter extends PlayerAI
{
    private int ticksToRefocus = 0;
    private int ticksToAttack = 3;
    private int ticksToFindPath = 0;
    private EntityLiving target = null;
    private Entity nearestItem = null;
    private double rad = 0;
    private Vec3D start = null;
    private boolean lookAtTarget = true;

    public PlayerAIMobHunter(EntityPlayer player, Vec3D start, double rad)
    {
        super(player);
        this.rad = rad;
        this.start = start.clone();
    }

    public void onTick()
    {
        if (isStopped)
        {
            return;
        }

        if (nearestItem == null)
        {
            moveHelper.clearPath();
        }

        if (target != null)
            if (target.getHealth() == 0)
            {
                target = null;
                ticksToRefocus = 0;
                ticksToFindPath = 0;
            }

        if (nearestItem != null)
            if (nearestItem.isDead)
            {
                nearestItem = null;
                ticksToRefocus = 0;
                ticksToFindPath = 0;
            }

        Hack.mc.mcProfiler.startSection("refocus");

        if (--ticksToRefocus < 0)
        {
            target = (EntityLiving) this.getNearestEntity(rad, MOB);
            nearestItem = this.getNearestEntity(rad, ITEMDROP | XPORB);

            if (target == null)
            {
                ticksToRefocus = 10;
            }
            else
            {
                ticksToRefocus = 4;
            }

            ticksToAttack = 0;
        }

        Hack.mc.mcProfiler.endSection(); // end section "refocus"
        Hack.mc.mcProfiler.startSection("look");

        if (lookAtTarget)
        {
            focusEntity(target, 180);
        }

        Hack.mc.mcProfiler.endSection(); // end section "look"
        Hack.mc.mcProfiler.startSection("move");
        Vec3D move = new Vec3D();
        Vec3D dangerMove = new Vec3D();
        ArrayList<Entity> list = getEntities(8F, CREEPER);

        for (int i = 0; i < list.size(); i++)
        {
            EntityCreeper c = (EntityCreeper)list.get(i);

            if (c.getCreeperState() > 0)
            {
                dangerMove.add(new Vec3D(player, c).setLen(-1.0D));
            }
        }

        if (target != null)
        {
            if (player.getDistanceToEntity(target) > 4.0F)
            {
                move.add(new Vec3D(player, target).setLen(1.0D));
            }

            if (player.getDistanceToEntity(target) < 3.5F)
            {
                dangerMove.add(new Vec3D(player, target).setLen(-1.0D));
            }

            if (--ticksToAttack < 0 && player.getDistanceToEntity(target) < 4.5F)
            {
                Hack.mc.playerController.attackEntity(player, target);
                player.swingItem();
                ticksToAttack = 10;
            }
        }

        if (nearestItem != null && dangerMove.getLen() == 0)
        {
//			if(player.getDistanceToEntity(nearestItem)>0.8F) {
//				move.add(new Vec3D(player, nearestItem).setLen(3.5D));
//			}
//			moveHelper.addVelocity(move);
            if (--ticksToFindPath < 0 || !moveHelper.hasPath())
            {
                moveHelper.findPath(nearestItem);
                ticksToFindPath = 10 * 20;
            }

            moveHelper.followPath();
        }

        if (!moveHelper.hasPath() && dangerMove.getLen() == 0)
        {
            moveHelper.addVelocity(move);
        }

        moveHelper.addVelocity(dangerMove);
        Hack.mc.mcProfiler.endSection(); // end section "move"
    }
}
