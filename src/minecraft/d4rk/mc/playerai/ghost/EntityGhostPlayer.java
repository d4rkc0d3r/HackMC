package d4rk.mc.playerai.ghost;

import net.minecraft.src.EntityOtherPlayerMP;
import net.minecraft.src.WorldClient;

public class EntityGhostPlayer extends EntityOtherPlayerMP {
	private WorldClient world;
	
	public EntityGhostPlayer(WorldClient world) {
		super(world, "Server Me");
		this.world = world;
		this.noClip = false;
		this.stepHeight = 0.5F;
	}
	
	@Override
	public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8) {
		super.setPositionAndRotation(par1, par3, par5, par7, par8);
		super.setPositionAndRotation2(par1, par3, par5, par7, par8, 3);
	}

	static public final int ENTITY_ID = 123456789;
}
