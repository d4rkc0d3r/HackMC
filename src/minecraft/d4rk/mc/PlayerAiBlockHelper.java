package d4rk.mc;

import d4rk.mc.util.Vec3D;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class PlayerAiBlockHelper {
	private Minecraft mc = null;
	private EntityClientPlayerMP player = null;
	private World world = null;

	private boolean isMining = true;
	private int side = 0;
	private Vec3D cur = null;
	private Vec3D last = null;
	
	public PlayerAiBlockHelper() {
		update();
	}
	
	public void update() {
		mc = Minecraft.getMinecraft();
		if(mc != null) player = mc.thePlayer;
		if(player != null) world = player.worldObj;
	}
	
	public boolean isMining() {
		return isMining;
	}
	
	public void select(Vec3D pos, int side) {
		this.cur = pos;
		this.side = side;
	}
	
	public int getBlockID(Vec3D v) {
		if(v == null) return 0;
		return world.getBlockId(v.getX(), v.getY(), v.getZ());
	}
	
	public boolean tryPlaceBlock(Vec3D pos, int side) {
		Vec3D v = getBlockSideCoords(pos, side);
		v.sub(new Vec3D(player));
		v.setLen(1);
		mc.playerController.onPlayerRightClick(player, world, player.getCurrentEquippedItem(),
											   pos.getX(), pos.getY(), pos.getZ(), side, v.getVec3());
		return true;
	}
	
	public boolean canReach() {
		return canReach(cur, side);
	}
	
	public boolean canReach(Vec3D pos, int s) {
		if(pos == null) return false;
		return mc.playerController.getBlockReachDistance() > 
			   ((getBlockSideCoords(pos, s).dist(new Vec3D(player))));
	}
	
	protected void startMining() {
		if(!isMining && cur != null) {
			mc.playerController.clickBlock(cur.getX(), cur.getY(), cur.getZ(), side);
			Hack.isAutoMining = true;
			isMining = true;
		}
	}
	
	protected void stopMining() {
		if(isMining) {
			Hack.isAutoMining = false;
			isMining = false;
		}
	}
	
	protected void updateMining() {
		mc.mcProfiler.startSection("mining");
		if(getBlockID(cur) != 0) {
			startMining();
			mc.playerController.onPlayerDamageBlock(cur.getX(), cur.getY(), cur.getZ(), side);
			mc.effectRenderer.addBlockHitEffects(cur.getX(), cur.getY(), cur.getZ(), side);
			mc.thePlayer.swingItem();
		}
		mc.mcProfiler.endSection();
	}
	
	public Vec3D getBlockSideCoords(Vec3D block, int side) {
		Vec3D v = new Vec3D(block.getX(), block.getY(), block.getZ());
		switch(side) {
		case POSX: v.y+=0.5; v.z+=0.5; 			 break;
		case NEGX: v.y+=0.5; v.z+=0.5; v.x+=1.0; break;
		case POSZ: v.y+=0.5; v.x+=0.5; 			 break;
		case NEGZ: v.y+=0.5; v.x+=0.5; v.z+=1.0; break;
		case POSY: v.x+=0.5; v.z+=0.5; 			 break;
		case NEGY: v.x+=0.5; v.z+=0.5; v.y+=1.0; break;
		default: Hack.logWarning("Unknown side: " + side); break;
		}
		return v;
	}
	
	public Vec3D getNearestBlock(Vec3D a, Vec3D b) {
		return getNearestBlock(a, b, defaultIgnoreList, false);
	}
	
	public Vec3D getNearestBlock(Vec3D ppos, Vec3D a, Vec3D b) {
		return getNearestBlock(ppos, a, b, defaultIgnoreList, false);
	}
	
	public Vec3D getNearestBlock(Vec3D a, Vec3D b, int[] array) {
		return getNearestBlock(a, b, array, false);
	}
	
	public Vec3D getNearestBlock(Vec3D ppos, Vec3D a, Vec3D b, int[] array) {
		return getNearestBlock(ppos, a, b, array, false);
	}
	
	public Vec3D getNearestBlock(Vec3D a, Vec3D b, int[] array, boolean onlySpecified) {
		return getNearestBlock(new Vec3D(player), a, b, array, onlySpecified);
	}
	
	public Vec3D getNearestBlock(Vec3D ppos, Vec3D a, Vec3D b, int[] array, boolean onlySpecified) {
		Vec3D A = new Vec3D(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		Vec3D B = new Vec3D(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
		double dist = 1000;
		Vec3D pos = null;
		for(int x=(int)A.x;x<=(int)B.x;x++) {
			for(int y=(int)A.y;y<=(int)B.y;y++) {
				for(int z=(int)A.z;z<=(int)B.z;z++) {
					Vec3D tmp = new Vec3D(ppos.x+x, ppos.y+y, ppos.z+z);
					if(onlySpecified == isOneOf(getBlockID(tmp), array) && tmp.dist(ppos) < dist) {
						pos = tmp;
						dist = tmp.dist(ppos);
					}
				}
			}
		}
		return pos;
	}
	
	public boolean isOneOf(int id, int[] array) {
		for(int check : array) if(check == id) return true; return false;
	}
	
	public static int[] defaultIgnoreList = new int[] {
		0, // Air
		Block.waterMoving.blockID,
		Block.waterStill.blockID,
		Block.lavaMoving.blockID,
		Block.lavaStill.blockID,
		Block.torchWood.blockID
	};

	public static final int UP   = 0;
	public static final int DOWN = 1;
	
	public static final int POSY = 0;
	public static final int NEGY = 1;
	public static final int POSZ = 2;
	public static final int NEGZ = 3;
	public static final int POSX = 4;
	public static final int NEGX = 5;
}
