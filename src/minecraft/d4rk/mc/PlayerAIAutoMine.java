package d4rk.mc;

import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.util.Vec3D;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.Vec3;

public class PlayerAIAutoMine extends PlayerAI {
	private int ticksToRefocus = 0;
	private int ticksToStart = 5;
	protected Entity item = null;
	protected Vec3D to = new Vec3D();
	protected Vec3D from = new Vec3D();
	protected Vec3D pos = null;
	protected Vec3D last = new Vec3D();
	protected Vec3D next = new Vec3D();
	private boolean isMining = true;
	protected double rad = 5;
	protected int side = POSX;

	private PlayerAiBlockHelper blockHelper = null;
	private PlayerAiInventoryHelper invHelper = null;
	
	public PlayerAIAutoMine(double rad) {
		super(Hack.mc.thePlayer);
		this.rad = rad;
		blockHelper = new PlayerAiBlockHelper();
		invHelper = new PlayerAiInventoryHelper();
	}
	
	public void update() {
		player = Hack.mc.thePlayer;
		blockHelper = new PlayerAiBlockHelper();
		invHelper = new PlayerAiInventoryHelper();
		moveHelper = new MoveHelper(player);
	}

	public void stop() {
		super.stop();
		stopMining();
	}
	
	public void resume() {
		super.resume();
		pos = null;
		stopMining();
		getDir();
		next = null;
		last = null;
		blockHelper = new PlayerAiBlockHelper();
		invHelper = new PlayerAiInventoryHelper();
		ticksToStart = 5;
	}
	
	private void getDir() {
		Vec3 lookVec = Hack.mc.thePlayer.getLookVec();
		Vec3D v = new Vec3D(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
		if(Math.abs(v.x)>Math.abs(v.z) && Math.abs(v.x)>Math.abs(v.y)) {
			side = (v.x>0) ? POSX : NEGX;
		}
		else if (Math.abs(v.z)>Math.abs(v.x) && Math.abs(v.z)>Math.abs(v.y)) {
			side = (v.z>0) ? POSZ : NEGZ;
		}
		else if (Math.abs(v.y)>Math.abs(v.x) && Math.abs(v.y)>Math.abs(v.z)) {
			side = (v.y>0) ? UP : DOWN;
		}
		Hack.logInfo("New direction: " + side);
	}
	
	private boolean isBlockValid(Vec3D pos) {
		if(pos == null) return false;
		return player.worldObj.getBlockId(pos.getX(), pos.getY(), pos.getZ()) != 0;
	}
	
	private void getNextBlock() {
		if(pos == null) pos = new Vec3D(player);
		last = next;
		if(side == POSX) {
			next = blockHelper.getNearestBlock(pos, new Vec3D(1,-1,-1), new Vec3D(3,1,1));
			if(next == null) pos.x++;
		}
		else if(side == NEGX) {
			next = blockHelper.getNearestBlock(pos, new Vec3D(-1,-1,-1), new Vec3D(-3,1,1));
			if(next == null) pos.x--;
		}
		else if(side == POSZ) {
			next = blockHelper.getNearestBlock(pos, new Vec3D(-1,-1,1), new Vec3D(1,1,3));
			if(next == null) pos.z++;
		}
		else if(side == NEGZ) {
			next = blockHelper.getNearestBlock(pos, new Vec3D(-1,-1,-1), new Vec3D(1,1,-3));
			if(next == null) pos.z--;
		}
		else
			next = null;
		if(next != null) {
			Vec3D t = pos.clone();
			pos = new Vec3D(t.getX()+0.5,t.y,t.getZ()+0.5);
		}
	}
	
	private int getBlockID(Vec3D v) {
		if(v == null) return 0;
		return player.worldObj.getBlockId(v.getX(), v.getY(), v.getZ());
	}
	
	public void onBlockDestroyed() {
		getNextBlock();
		stopMining();
	}
	
	protected void stopMining() {
		if(isMining) {
			Hack.isAutoMining = false;
			isMining = false;
		}
	}
	
	protected void startMining() {
		if(!isMining) {
			Hack.mc.playerController.clickBlock(next.getX(), next.getY(), next.getZ(), side);
			Hack.isAutoMining = true;
			isMining = true;
		}
	}
	
	protected void updateMining() {
		Hack.mc.mcProfiler.startSection("mining");
		if(isBlockValid(next) && blockHelper.canReach(next, side)) {
			Block target = Block.blocksList[player.worldObj.getBlockId(next.getX(), next.getY(), next.getZ())];
//			if(!player.inventory.canHarvestBlock(target))
//				for(int i=0;i<9;i++) {
//					player.inventory.currentItem = i;
//					if(player.inventory.canHarvestBlock(target))
//						break;
//				}
			Hack.getPlayerWrapper().selectToolForBlock(new BlockWrapper(next));
			startMining();
			Hack.mc.playerController.onPlayerDamageBlock(next.getX(), next.getY(), next.getZ(), side);
			Hack.mc.effectRenderer.addBlockHitEffects(next.getX(), next.getY(), next.getZ(), side);
			Hack.mc.thePlayer.swingItem();
		}
		Hack.mc.mcProfiler.endSection();
	}
	
	private void placeCobbleOnSide(int s) {
		if(!player.onGround)
			return;
		if(!invHelper.select(new int[] {Block.cobblestone.blockID, Block.dirt.blockID, Block.netherrack.blockID}))
			return;
		Vec3D v = new Vec3D(Hack.mc.thePlayer);
		v.y-=2.5;
		BlockWrapper block = new BlockWrapper(v, Hack.mc.theWorld);
		//player.motionX=0;player.motionY=0;player.motionZ=0;
		this.lookAtPosition(block.getSideCoords(s), 180);
		Vec3D off = block.getSideOffset(s);
		player.swingItem();
		block.getRelative(s).setID(player.inventory.getCurrentItem().itemID);
		Hack.addDelayedPacket((Packet)new Packet15Place(block.x, block.y, block.z,
							  s, player.inventory.getCurrentItem(),
							  (float)off.x, (float)off.y, (float)off.z), 1);
		ticksToStart = 2;
	}
	
	protected void updateMoving() {
		Hack.mc.mcProfiler.startSection("moving");
		if(this.pos != null) {
			if(new Vec3D(Hack.mc.thePlayer).dist(this.pos) > ((item != null) ? 1.3 : 0.3)) {
				moveHelper.addVelocity(this.pos.clone().sub(new Vec3D(Hack.mc.thePlayer)));
			}
			if(item != null) {
				moveHelper.addVelocity(new Vec3D(item).sub(new Vec3D(Hack.mc.thePlayer)));
			}
			Vec3D v = new Vec3D(Hack.mc.thePlayer);
			v.y-=2.5;
			BlockWrapper block = new BlockWrapper(v, Hack.mc.theWorld);
			if(block.getRelative(1, 0, 0).isReplaceable()) {
				placeCobbleOnSide(NEGX);
			}
			else if(block.getRelative(-1, 0, 0).isReplaceable()) {
				placeCobbleOnSide(POSX);
			}
			else if(block.getRelative(0, 0, 1).isReplaceable()) {
				placeCobbleOnSide(NEGZ);
			}
			else if(block.getRelative(0, 0, -1).isReplaceable()) {
				placeCobbleOnSide(POSZ);
			}
		}
		Hack.mc.mcProfiler.endSection();
	}
	
	private void focusBlock() {
		Hack.mc.mcProfiler.startSection("look");
		if(isBlockValid(next))
			this.lookAtPosition(blockHelper.getBlockSideCoords(next, side), 180);
		Hack.mc.mcProfiler.endSection(); // end section "look"
	}
	
	public void onTick() {
		if(isStopped) return;
		if(ticksToStart-- > 0) return;
		if(item != null)
			if(item.isDead) {
				item = null;
				ticksToRefocus = 0;
			}
		
		if(pos != null) if(blockHelper.canReach(pos, NEGY)) {
			if(player.worldObj.getBlockLightValue(pos.getX(), pos.getY()-1, pos.getZ()) < 6) {
				invHelper.select(Block.torchWood.blockID);
				if(player.inventory.getCurrentItem() != null && 
				   player.inventory.getCurrentItem().itemID == Block.torchWood.blockID) {
					stopMining();
					Vec3D block = new Vec3D(pos.getX(), pos.getY()-2, pos.getZ());
					Vec3D off = blockHelper.getBlockSideCoords(block, 1).sub(block);
					this.lookAtPosition(block.clone().add(off), 180);
					Hack.addDelayedPacket((Packet)new Packet15Place(
							block.getX(), block.getY(), block.getZ(),
							1, player.inventory.getCurrentItem(),
							(float)off.x, (float)off.y, (float)off.z), 1);
					ticksToStart = 5;
					return;
				}
			}
		}
		
		Hack.mc.mcProfiler.startSection("refocus");
		if(--ticksToRefocus<0) {
			item = this.getNearestEntity(2.5, ITEMDROP|XPORB);
			if(item == null)
				ticksToRefocus = 10;
			else
				ticksToRefocus = 4;
		}
		if(!(isBlockValid(next))) {
			getNextBlock();
			stopMining();
		}
		Hack.mc.mcProfiler.endSection(); // end section "refocus"
		
		focusBlock();
		
		updateMining();
		
		updateMoving();
	}
	
	private static int[] axeIDList = new int[] {
		Item.axeGold.itemID,
		Item.axeDiamond.itemID,
		Item.axeIron.itemID,
		Item.axeStone.itemID,
		Item.axeWood.itemID
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
