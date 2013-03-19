package d4rk.mc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import d4rk.mc.util.Vec2D;
import d4rk.mc.util.Vec3D;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityFireball;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityGolem;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.EntitySquid;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.EntityXPOrb;

/**
 * old AI base class. most methods are now in the PlayerWrapper.
 * @deprecated use the BaseAI class instead and convert the existing AI's to the new AI system.
 */
public class PlayerAI
{
    public static PlayerAI AI = null;

    protected EntityPlayer player = null;
    protected boolean isStopped = true;
    protected MoveHelper moveHelper = null;

    public PlayerAI(EntityPlayer player)
    {
        moveHelper = new MoveHelper(player);
        this.player = player;
    }

    public void update()
    {
    }

    public void toggle()
    {
        if (isStopped)
        {
            resume();
        }
        else
        {
            stop();
        }
    }

    public void stop()
    {
        isStopped = true;
        Hack.addChatMessage("PlayerAI stopped");
    }

    public void start()
    {
        resume();
    }

    public void resume()
    {
        isStopped = false;
        Hack.addChatMessage("PlayerAI started");
    }

    public void onTick()
    {
    }

    public void lookAtPosition(Vec3D pos, float maxRotate)   // magic, do not touch
    {
        if (pos == null)
        {
            return;
        }

        Vec2D ePos = new Vec2D(pos.x, pos.z);
        Vec2D pPos = new Vec2D(player.posX, player.posZ);
        Vec2D dist = ePos.clone().sub(pPos);
        player.rotationYaw = player.rotationYaw % 360;
        float nRotationYaw = player.rotationYaw;
        float nRotationPitch = player.rotationPitch;

        if (dist.getLen() > 0)
        {
            if ((dist.clone().angle(new Vec2D(-1, 0)) * 57.2957795) > 90)
            {
                nRotationYaw = (float)(dist.clone().angle(new Vec2D(0, 1)) * -57.2957795);
            }
            else
            {
                nRotationYaw = (float)(dist.clone().angle(new Vec2D(0, 1)) * 57.2957795);
            }
        }

        Vec2D vert = new Vec2D(dist.getLen(), pos.y - (player.posY));

        if (vert.y < 0)
        {
            nRotationPitch = (float)(vert.angle(new Vec2D(1, 0)) * 57.2957795);
        }
        else
        {
            nRotationPitch = (float)(vert.angle(new Vec2D(1, 0)) * -57.2957795);
        }

        float diffYaw = nRotationYaw - player.rotationYaw;
        float diffYaw2 = nRotationYaw - player.rotationYaw - 360;
        float diffPitch = nRotationPitch - player.rotationPitch;

        if (Math.abs(diffPitch) > maxRotate)
        {
            diffPitch = (diffPitch / Math.abs(diffPitch)) * maxRotate;
        }

        if (Math.abs(diffYaw2) < Math.abs(diffYaw))
        {
            diffYaw = diffYaw2;
        }

        if (Math.abs(diffYaw) > maxRotate)
        {
            diffYaw = (diffYaw / Math.abs(diffYaw)) * maxRotate;
        }

        player.rotationYaw += diffYaw;
        player.rotationPitch += diffPitch;
    }

    public void focusEntity(Entity e, float maxRotate)
    {
        if (e == null)
        {
            return;
        }

        this.lookAtPosition(new Vec3D(e.posX, e.posY + e.height / 2, e.posZ), maxRotate);
    }

    public Entity getNearestEntity(double rad)
    {
        return getNearestEntity(rad, ALL);
    }

    public Entity getNearestMob(double rad)
    {
        return getNearestEntity(rad, MOB);
    }

    public ArrayList<Entity> getEntities(double rad, int flag)
    {
        if (player == null)
        {
            Hack.logWarning("player == null");
            Hack.log("player = Hack.mc.thePlayer");
            player = Hack.mc.thePlayer;
        }

        ArrayList<Entity> list = new ArrayList<Entity>();
        Entity nearest = null;
        double dist = rad * rad;
        List Entities = player.worldObj.loadedEntityList;

        for (int k = 0; k < Entities.size(); k++)
            if (Entities.get(k) != null)
            {
                Entity e = (Entity) Entities.get(k);

                if (e == player)
                {
                    continue;
                }

                if (!isDesiredEntity(e, flag))
                {
                    continue;
                }

                double squareDist = e.getDistanceSqToEntity(player);

                if (squareDist < dist)
                {
                    list.add(e);
                }
            }

        return list;
    }

    public Entity getNearestEntity(double rad, int flag)
    {
        if (player == null)
        {
            Hack.logWarning("player == null");
            Hack.log("player = Hack.mc.thePlayer");
            player = Hack.mc.thePlayer;
        }

        List Entities = player.worldObj.loadedEntityList;
        Entity nearest = null;
        double nearestSquareDist = rad * rad;

        for (int k = 0; k < Entities.size(); k++)
            if (Entities.get(k) != null)
            {
                Entity e = (Entity) Entities.get(k);

                if (e == player)
                {
                    continue;
                }

                if (!isDesiredEntity(e, flag))
                {
                    continue;
                }

                double squareDist = e.getDistanceSqToEntity(player);

                if (squareDist < nearestSquareDist)
                {
                    nearest = e;
                    nearestSquareDist = squareDist;
                }
            }

        return nearest;
    }

    public boolean isMob(Entity e)
    {
        if ((e instanceof EntityMob) || (e instanceof EntitySlime) || (e instanceof EntityGhast))
        {
            return (!e.isDead && ((EntityLiving)e).getHealth() > 0);
        }

        return false;
    }

    public boolean isAnimal(Entity e)
    {
        if ((e instanceof EntitySheep) || (e instanceof EntityChicken) || (e instanceof EntityPig)
                || (e instanceof EntityCow) || (e instanceof EntitySquid))
        {
            return (!e.isDead && ((EntityLiving)e).getHealth() > 0);
        }

        return false;
    }

    public boolean isVillager(Entity e)
    {
        if (e instanceof EntityVillager)
        {
            return !e.isDead;
        }

        return false;
    }

    public boolean isItemdrop(Entity e)
    {
        if (e instanceof EntityItem)
        {
            return !e.isDead;
        }

        return false;
    }

    public boolean isExperience(Entity e)
    {
        if (e instanceof EntityXPOrb)
        {
            return !e.isDead;
        }

        return false;
    }

    public boolean isProjectile(Entity e)
    {
        if ((e instanceof EntityArrow) || (e instanceof EntityFireball))
        {
            return !e.isDead;
        }

        return false;
    }

    public boolean isGolem(Entity e)
    {
        if (e instanceof EntityGolem)
        {
            return !e.isDead;
        }

        return false;
    }

    public boolean isDesiredEntity(Entity e, int flag)
    {
        if (e == null)
        {
            return false;
        }

        if (flag == ALL)
        {
            return true;
        }

        if ((flag & MOB) == MOB) if (isMob(e))
            {
                return true;
            }

        if ((flag & ANIMAL) == ANIMAL) if (isAnimal(e))
            {
                return true;
            }

        if ((flag & VILLAGER) == VILLAGER) if (isVillager(e))
            {
                return true;
            }

        if ((flag & ITEMDROP) == ITEMDROP) if (isItemdrop(e))
            {
                return true;
            }

        if ((flag & XPORB) == XPORB) if (isExperience(e))
            {
                return true;
            }

        if ((flag & PROJECTILE) == PROJECTILE) if (isProjectile(e))
            {
                return true;
            }

        if ((flag & GOLEM) == GOLEM) if (isGolem(e))
            {
                return true;
            }

        if ((flag & CREEPER) == CREEPER) if (e instanceof EntityCreeper)
            {
                return true;
            }

        return false;
    }

    public static final int ALL 		= 0xFFFF;
    public static final int MOB 		= 0x0001;
    public static final int ANIMAL		= 0x0002;
    public static final int ITEMDROP 	= 0x0004;
    public static final int PROJECTILE 	= 0x0008;
    public static final int VILLAGER 	= 0x0010;
    public static final int GOLEM 		= 0x0020;
    public static final int XPORB       = 0x0040;
    public static final int CREEPER     = 0x0080;
}
