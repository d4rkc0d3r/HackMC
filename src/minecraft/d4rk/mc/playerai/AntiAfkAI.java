package d4rk.mc.playerai;

import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class AntiAfkAI extends BaseAI
{
    private int ticksToMove = 12;
    private Vec3D pos = null;
    private float yaw = 0;
    private float pitch = 0;

    public AntiAfkAI(PlayerWrapper player)
    {
        super(player);
        pos = player.getPosition();
        yaw = pWrap.player.rotationYaw;
        pitch = pWrap.player.rotationPitch;
        this.start();
    }

    @Override
    public void onTick()
    {
        if (isStopped)
        {
            return;
        }

        pWrap.lookAt(pos.clone().add(new Vec3D(0, 0, 1)));

        if (--ticksToMove > 0)
        {
            pWrap.addVelocity(new Vec3D(1, 0, 0));
            return;
        }

        Vec3D velo = pos.clone().sub(pWrap.getPosition());
        velo.setLen(velo.getLen() / (velo.getLen() + 1));
        velo.mul(0.1);
        pWrap.player.addVelocity(velo.x, velo.y, velo.z);

        if (velo.getLen() < 0.005)
        {
            pWrap.player.setPosition(pos.x, pos.y, pos.z);
            pWrap.player.setVelocity(0, 0, 0);
            pWrap.player.rotationPitch = pitch;
            pWrap.player.rotationYaw = yaw;
            this.stop();
        }
    }
}
