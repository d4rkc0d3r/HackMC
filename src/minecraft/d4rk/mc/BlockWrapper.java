package d4rk.mc;

import java.util.ArrayList;

import d4rk.mc.util.Vec3D;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySign;
import net.minecraft.src.World;

public class BlockWrapper {
	public int x;
	public int y;
	public int z;
	public World world;

	public BlockWrapper(Vec3D pos, World world) {
		this.world = world;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public BlockWrapper(Vec3D pos) {
		this.world = Minecraft.getMinecraft().theWorld;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}
	
	public BlockWrapper(int x, int y, int z, World world) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockWrapper(int x, int y, int z) {
		this.world = Minecraft.getMinecraft().theWorld;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockWrapper(BlockWrapper from) {
		this.world = from.world;
		this.x = from.x;
		this.y = from.y;
		this.z = from.z;
	}

	public BlockWrapper setID(int id) {
		return setIDandMetadataNotify(id, getMetadata());
	}
	
	public BlockWrapper setMetadata(int meta) {
		return setIDandMetadataNotify(getID(), meta);
	}
	
	public BlockWrapper setIDandMetadata(int id, int meta) {
		return setIDandMetadataNotify(id, meta);
	}
	
	public BlockWrapper setIDandMetadataNotify(int id, int meta) {
		world.setBlock(x, y, z, id, meta, 0);
		return this;
	}
	
	public int getNearestSide(Vec3D pos) {
		int s = -1;
		double dist = 999999D;
		for(int i=0;i<6;i++) {
			double d = pos.dist(getSideCoords(i));
			if(dist < d) continue;
			dist = d;
			s = i;
		}
		return s;
	}
	
	public int getNearestFreeSide(Vec3D pos) {
		int s = -1;
		double dist = 999999D;
		for(int i=0;i<6;i++) {
			if(this.getRelative(i).getID() == 0) continue;
			double d = pos.dist(getSideCoords(i));
			if(dist < d) continue;
			dist = d;
			s = i;
		}
		return s;
	}
	
	public Vec3D getPosition() {
		return new Vec3D(x, y, z);
	}
	
	public Vec3D getSideCoords(int side) {
		return new Vec3D(x, y, z).add(this.getSideOffset(side));
	}
	
	public Vec3D getSideOffset(int side) {
		Vec3D v = new Vec3D(0, 0, 0);
		Block b = this.getBlock();
		if(b == null) {
			b = Block.stone;
		}
		
		double minX = b.getBlockBoundsMinX();
		double minY = b.getBlockBoundsMinY();
		double minZ = b.getBlockBoundsMinZ();
		
		double maxX = b.getBlockBoundsMaxX();
		double maxY = b.getBlockBoundsMaxY();
		double maxZ = b.getBlockBoundsMaxZ();

		double midX = (minX+maxX) /2;
		double midY = (minY+maxY) /2;
		double midZ = (minZ+maxZ) /2;
		
		switch(side) {
		case POSX:   v.x+=minX; v.y+=midY; v.z+=midZ; break;
		case NEGX:   v.x+=maxX; v.y+=midY; v.z+=midZ; break;
		case POSZ:   v.x+=midX; v.y+=midY; v.z+=minZ; break;
		case NEGZ:   v.x+=midX; v.y+=midY; v.z+=maxZ; break;
		case POSY:   v.x+=midX; v.y+=minY; v.z+=midZ; break;
		case NEGY:   v.x+=midX; v.y+=maxY; v.z+=midZ; break;
		case INSIDE: v.x+=midX; v.y+=midY; v.z+=midZ; break;
		default: 								      break;
		}
		
		return v;
	}
	
	public int getID() {
		return world.getBlockId(x, y, z);
	}
	
	public int getMetadata() {
		return world.getBlockMetadata(x, y, z);
	}
	
	/**
	 * Use for things like:<br>
	 * Walk to block xy
	 */
	public String getPositionString() {
		return String.format("%.2f %.2f %.2f", x+0.5, y+1.62+getBlock().getBlockBoundsMaxY(), z+0.5).replace(',', '.');
	}
	
	/**
	 * Straight integer coordinates as string with spaces in between.
	 */
	public String getString() {
		return x + " " + y + " " + z;
	}
	
	/**
	 * Same as <code>getMaterial()</code>.
	 * 
	 * @return <code>Material</code> of the block.
	 */
	public Material getType() {
		return world.getBlockMaterial(x, y, z);
	}
	
	/**
	 * @return <code>Material</code> of the block.
	 */
	public Material getMaterial() {
		return world.getBlockMaterial(x, y, z);
	}
	
	/**
	 * Returns the <code>TileEntity</code> associated with the block, or
	 * <code>null</code> if no <code>TileEntity</code> exists.
	 */
	public TileEntity getTileEntity() {
		return world.getBlockTileEntity(x, y, z);
	}
	
	public boolean isSign() {
		switch(getID()) {
		case 63:
		case 68:
			return true;
		default:
			return false;
		}
	}
	
	public TileEntitySign getSign() {
		try {
			return (TileEntitySign)world.getBlockTileEntity(x, y, z);
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * @return The actual signText array from the TileEntitySign. Its size is
	 *         even 4 if it is not a Sign. Use {@link #isSign()} to determine if
	 *         it is a sign or not.
	 */
	public String[] getSignText() {
		TileEntitySign sign = this.getSign();
		return (sign == null) ? new String[] {"", "", "", ""} : sign.signText;
	}
	
	/**
	 * @param n the line number
	 * @return The line number n or an empty string if the block isn't a sign.
	 */
	public String getSignLine(int n) {
		TileEntitySign sign = this.getSign();
		return (sign == null) ? "" : sign.signText[n];
	}
	
	public Block getBlock() {
		return Block.blocksList[this.getID()];
	}
	
	public BlockWrapper[] getAllBlocks(int off) {
		return this.getAllBlocks(new Vec3D(-off, -off, -off), new Vec3D(off, off, off));
	}
	
	public BlockWrapper[] getAllBlocks(int xzOff, int yOff) {
		return this.getAllBlocks(new Vec3D(-xzOff, -yOff, -xzOff), new Vec3D(xzOff, yOff, xzOff));
	}
	
	public BlockWrapper[] getAllBlocks(BlockWrapper block) {
		if(world != block.world) return null;
		Vec3D a = new Vec3D(block.x, block.y, block.z);
		Vec3D b = new Vec3D(x, y, z);
		return getAllBlocksAbsolute(a, b, world);
	}
	
	public BlockWrapper[] getAllBlocks(Vec3D a, Vec3D b) {
		Vec3D A = new Vec3D(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		Vec3D B = new Vec3D(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
		
		ArrayList<BlockWrapper> list = new ArrayList<BlockWrapper>((int) ((B.x-A.x)*(B.y-A.y)*(B.z-A.z)));

		for(int y=(int)B.y;y>=(int)A.y;y--) {
			for(int x=(int)A.x;x<=(int)B.x;x++) {
				for(int z=(int)A.z;z<=(int)B.z;z++) {
					list.add(new BlockWrapper(this.x+x, this.y+y, this.z+z, this.world));
				}
			}
		}
		return list.toArray(new BlockWrapper[0]);
	}
	
	public BlockWrapper getRelative(int side) {
		return this.getRelative(side, 1);
	}
	
	public BlockWrapper getRelative(int side, int n) {
		switch(side) {
		case POSZ: return this.getRelative(0, 0, -n);
		case NEGZ: return this.getRelative(0, 0, n);
		case POSY: return this.getRelative(0, -n, 0);
		case NEGY: return this.getRelative(0, n, 0);
		case POSX: return this.getRelative(-n, 0, 0);
		case NEGX: return this.getRelative(n, 0, 0);
		default:   return this.getRelative(0, 0, 0);
		}
	}
	
	public BlockWrapper getRelative(int x, int y, int z) {
		return new BlockWrapper(this.x+x, this.y+y, this.z+z, this.world);
	}
	
	public boolean blocksMovement() {
		return getMaterial().blocksMovement();
	}
	
	public boolean canPlayerStandOn() {
		if(!blocksMovement()) {
			return false;
		} else {
			double height = getCollisionBoxMaxY();
			if(height == 1.5) {
				return (getRelative(0, 1, 0).getCollisionBoxMaxY() <= 0.5 || !getRelative(0, 1, 0).blocksMovement())
						&& !getRelative(0, 2, 0).blocksMovement()
						&& (!getRelative(0, 3, 0).blocksMovement() || getRelative(0, 3, 0).getBlock().getBlockBoundsMinY() > 0.5);
			} else if(height == 1.0) {
				return (!getRelative(0, 1, 0).blocksMovement() && !getRelative(0, 2, 0).blocksMovement());
			} else {
				return (!getRelative(0, 1, 0).blocksMovement() && (!getRelative(0, 2, 0).blocksMovement()
						|| getRelative(0, 2, 0).getBlock().getBlockBoundsMinY() > (1 - height)));
			}
		}
	}
	
	public double getCollisionBoxMaxY() {
		if(isOneOf(Block.fence.blockID, Block.fenceGate.blockID, Block.netherFence.blockID)) {
			return 1.5;
		} else {
			return (getBlock() == null) ? 0 : getBlock().getBlockBoundsMaxY();
		}
	}
	
	public double getStepHeight(EntityPlayer e) {
		Vec3D pos = Vec3D.getPlayerFootPos(e);
		BlockWrapper test = this;
		if(!blocksMovement() && !getRelative(0, 1, 0).blocksMovement() && !getRelative(0, 2, 0).blocksMovement()) {
			while(test.y > 0) {
				if(test.canPlayerStandOn()) {
					break;
				}
				test = test.getRelative(0, -1, 0);
			}
		} else {
			while(test.y < 255) {
				if(test.canPlayerStandOn()) {
					break;
				}
				test = test.getRelative(0, 1, 0);
			}
		}
		return (test.y + test.getCollisionBoxMaxY()) - pos.y;
	}
	
	public boolean isInsideOf(BlockWrapper from, BlockWrapper to) {
		if(from.world != to.world) return false;
		return x <= Math.max(from.x, to.x) && x >= Math.min(from.x, to.x) &&
			   y <= Math.max(from.y, to.y) && y >= Math.min(from.y, to.y) &&
			   z <= Math.max(from.z, to.z) && z >= Math.min(from.z, to.z);
	}
	
	public boolean isOneOf(int ... IDs) {
		int ID = this.getID();
		for (int id : IDs)
			if (id == ID)
				return true;
		return false;
	}

	public boolean isAir() {
		return this.getID() == 0;
	}
	
	/**
	 * Not mine able are per default: air, bedrock, water & lava.
	 */
	public boolean isMineable() {
		return !contains(noneMinableList, this.getID());
	}
	
	public boolean isReplaceable() {
		Block b = this.getBlock();
		if(b == null) return true;
		if(b.blockMaterial.isLiquid()) return true;
		if(b.blockMaterial.isReplaceable()) return true;
		return false;
	}
	
	public boolean isLiquid() {
		return this.getMaterial().isLiquid();
	}
	
	public boolean isTouching(int id) {
		if(this.getRelative(1, 0, 0).getID() == id)  return true;
		if(this.getRelative(0, 1, 0).getID() == id)  return true;
		if(this.getRelative(0, 0, 1).getID() == id)  return true;
		if(this.getRelative(-1, 0, 0).getID() == id) return true;
		if(this.getRelative(0, -1, 0).getID() == id) return true;
		if(this.getRelative(0, 0, -1).getID() == id) return true;
		return false;
	}
	
	public boolean isTouching(int[] id) {
		if(this.getRelative(1, 0, 0).isOneOf(id))  return true;
		if(this.getRelative(0, 1, 0).isOneOf(id))  return true;
		if(this.getRelative(0, 0, 1).isOneOf(id))  return true;
		if(this.getRelative(-1, 0, 0).isOneOf(id)) return true;
		if(this.getRelative(0, -1, 0).isOneOf(id)) return true;
		if(this.getRelative(0, 0, -1).isOneOf(id)) return true;
		return false;
	}
	
	public boolean isTouching(int id, int metaData) {
		if(this.getRelative(1, 0, 0).equals(id, metaData))  return true;
		if(this.getRelative(0, 1, 0).equals(id, metaData))  return true;
		if(this.getRelative(0, 0, 1).equals(id, metaData))  return true;
		if(this.getRelative(-1, 0, 0).equals(id, metaData)) return true;
		if(this.getRelative(0, -1, 0).equals(id, metaData)) return true;
		if(this.getRelative(0, 0, -1).equals(id, metaData)) return true;
		return false;
	}
	
	public boolean isTouchingLiquid() {
		if(this.getRelative(1, 0, 0).getMaterial().isLiquid())  return true;
		if(this.getRelative(0, 1, 0).getMaterial().isLiquid())  return true;
		if(this.getRelative(0, 0, 1).getMaterial().isLiquid())  return true;
		if(this.getRelative(-1, 0, 0).getMaterial().isLiquid()) return true;
		if(this.getRelative(0, -1, 0).getMaterial().isLiquid()) return true;
		if(this.getRelative(0, 0, -1).getMaterial().isLiquid()) return true;
		return false;
	}
	
	public boolean exists() {
		return existsInWorld(world);
	}
	
	public boolean existsInWorld(World w) {
		if(w == null)
			return false;
		return w.blockExists(x, y, z);
	}
	
	public boolean equals(int id) {
		return this.getID() == id;
	}
	
	public boolean equals(int id, int metaData) {
		return this.getID() == id && this.getMetadata() == metaData;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public BlockWrapper clone() {
		return new BlockWrapper(this);
	}
	
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof BlockWrapper)) return false;
		BlockWrapper b = (BlockWrapper)obj;
		return world == b.world && x == b.x && y == b.y && z == b.z;
	}
	
	public int hashCode() {
		return x ^ (z<<12) ^ (y<<24) ^ ((world != null && !ignoreWorldInHashCode) ? world.hashCode() : 0);
	}
	
	public static BlockWrapper getNearestMinable(Vec3D pos, BlockWrapper[] array) {
		return getNearest(pos, array, noneMinableList, true);
	}
	
	public static BlockWrapper getNearest(Vec3D pos, BlockWrapper[] array, int[] blockIDs) {
		return getNearest(pos, array, blockIDs, false);
	}
	
	public static BlockWrapper getNearest(Vec3D pos, BlockWrapper[] array, int[] blockIDs, boolean isIgnoreList) {
		BlockWrapper nearest = null;
		double dist = 9999999D;
		for(BlockWrapper b : array) {
			if(isIgnoreList == contains(blockIDs, b.getID())) continue;
			double d = b.getSideCoords(INSIDE).dist(pos);
			if(dist<d) continue;
			nearest = b;
			dist = d;
		}
		return nearest;
	}
	
	public static BlockWrapper[] getAllBlocksAbsolute(Vec3D a, Vec3D b) {
		return getAllBlocksAbsolute(a, b, Minecraft.getMinecraft().theWorld);
	}
	
	public static BlockWrapper[] getAllBlocksAbsolute(Vec3D a, Vec3D b, World world) {
		Vec3D A = new Vec3D(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		Vec3D B = new Vec3D(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
		
		ArrayList<BlockWrapper> list = new ArrayList<BlockWrapper>((int) ((B.x-A.x)*(B.y-A.y)*(B.z-A.z)));

		for(int y=(int)B.y;y>=(int)A.y;y--) {
			for(int x=(int)A.x;x<=(int)B.x;x++) {
				for(int z=(int)A.z;z<=(int)B.z;z++) {
					list.add(new BlockWrapper(x, y, z, world));
				}
			}
		}
		return list.toArray(new BlockWrapper[0]);
	}
	
	public static boolean contains(int[] array, int reference) {
		for(int candidate : array) if(candidate == reference) return true; return false;
	}
	
	public static int[] noneMinableList = new int[] {
		0, // Air
		Block.waterStill.blockID,
		Block.waterMoving.blockID,
		Block.lavaStill.blockID,
		Block.lavaMoving.blockID,
		Block.bedrock.blockID
	};
	
	public static boolean ignoreWorldInHashCode = false;

	public static final int INSIDE = -1;
	public static final int POSY = 0;
	public static final int NEGY = 1;
	public static final int POSZ = 2;
	public static final int NEGZ = 3;
	public static final int POSX = 4;
	public static final int NEGX = 5;
}
