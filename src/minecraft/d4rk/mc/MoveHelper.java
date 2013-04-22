package d4rk.mc;

import d4rk.mc.util.Vec3D;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.PathEntity;
import net.minecraft.src.PathPoint;
import net.minecraft.src.StatList;

public class MoveHelper {
	protected EntityPlayer player;
	protected PathEntity path = null;
	
	public MoveHelper(EntityPlayer player) {
		this.player = player;
	}
	
	/**
	 * @deprecated use the BlockWrapper class instead.
	 */
	protected Material getMaterialRelative(double x, double y, double z) {
		return getMaterial(player.posX+x, player.posY-2.0F+y, player.posZ+z);
	}

	/**
	 * @deprecated use the BlockWrapper class instead.
	 */
	protected Material getMaterial(double x, double y, double z) {
		return player.worldObj.getBlockMaterial((int)((x<0) ? x-1 : x), 
												(int)((y<0) ? y-1 : y),
												(int)((z<0) ? z-1 : z));
	}
	
	protected boolean capX(Vec3D vec) {
		if(vec.x>0)
			if((player.posX-Math.floor(player.posX))>0.75) {
				vec.x = 0;
				player.motionX*=0.5F;
				return true;
			}
		if(vec.x<0)
			if((player.posX-Math.floor(player.posX))<0.25) {
				vec.x = 0;
				player.motionX*=0.5F;
				return true;
			}
		return false;
	}
	
	protected boolean capZ(Vec3D vec) {
		if(vec.z>0)
			if((player.posZ-Math.floor(player.posZ))>0.75) {
				vec.z = 0;
				player.motionZ*=0.5F;
				return true;
			}
		if(vec.z<0)
			if((player.posZ-Math.floor(player.posZ))<0.25) {
				vec.z = 0;
				player.motionZ*=0.5F;
				return true;
			}
		return false;
	}
	
	protected void jumpInDirection(Vec3D vec) {
		if(vec.x>0)
			if((player.posX-Math.floor(player.posX))>0.5)
				jump();
		if(vec.x<0)
			if((player.posX-Math.floor(player.posX))<0.5)
				jump();
		if(vec.z>0)
			if((player.posZ-Math.floor(player.posZ))>0.5)
				jump();
		if(vec.z<0)
			if((player.posZ-Math.floor(player.posZ))<0.5)
				jump();
	}
	
	protected void jump() {
		if(!player.onGround) return;
		player.motionY = 0.42D;
        player.isAirBorne = true;
        player.addStat(StatList.jumpStat, 1);
		player.setJumping(true);
		if (player.isSprinting())
			player.addExhaustion(0.8F);
        else
        	player.addExhaustion(0.2F);
	}
	
	protected void checkBlocks(Vec3D vec) {
		int X = 0;
		int Z = 0;
		if(vec.x!=0) X = (vec.x<0) ? -1 : 1;
		if(vec.z!=0) Z = (vec.z<0) ? -1 : 1;
		Vec3D pos = Vec3D.getPlayerFootPos(player);
		BlockWrapper bx = new BlockWrapper(pos).getRelative(X, 0, 0);
		BlockWrapper bz = new BlockWrapper(pos).getRelative(0, 0, Z);

		if(X != 0) {
			double stepHeight = bx.getStepHeight(player);
			if(stepHeight > 0.5 && stepHeight < 1.1) {
				jumpInDirection(vec);
			} else if(stepHeight < -10) {
				capX(vec);
			}
		}
		if(Z != 0) {
			double stepHeight = bz.getStepHeight(player);
			if(stepHeight > 0.5 && stepHeight < 1.1) {
				jumpInDirection(vec);
			} else if(stepHeight < -10) {
				capZ(vec);
			}
		}
		/*
		if(bx.getRelative(0, 1, 0).getMaterial().blocksMovement()
				&& !bx.getRelative(0, 1, 0).isOneOf(new int[]{Block.fence.blockID})) {
			if(bx.getRelative(0, 2, 0).getMaterial().blocksMovement()
					|| bx.getRelative(0, 3, 0).getMaterial().blocksMovement()) {
				capX(vec);
			} else {
				jumpInDirection(vec);
			}
		} else if(!bx.getMaterial().blocksMovement()
				&& !bx.getRelative(0, 1, 0).getMaterial().blocksMovement()
				&& !bx.getRelative(0, 2, 0).getMaterial().blocksMovement()) {
			//TODO: check for falling distance and avoid it (maybe)
			//capX(vec);
		}
		if(bz.getRelative(0, 1, 0).getMaterial().blocksMovement()) {
			if(bz.getRelative(0, 2, 0).getMaterial().blocksMovement()
					|| bz.getRelative(0, 3, 0).getMaterial().blocksMovement()) {
				capX(vec);
			} else {
				jumpInDirection(vec);
			}
		} else if(!bz.getMaterial().blocksMovement() && !bz.getRelative(0, 2, 0).getMaterial().blocksMovement()) {
			//TODO: check for falling distance and avoid it (maybe)
			//capZ(vec);
		}*/
	}
	
	private boolean tryMoveTo(int x, int y, int z) {
		addVelocity(new Vec3D(player.posX-(x+0.5F),player.posY-1.62F-(y),player.posZ-(z+0.5F)).mul(-1));
		if(player.getDistance(x+0.5, player.posY, z+0.5)<0.5) {
			return true;
		}
		return false;
	}
	
	public boolean hasPath() {
		return path != null;
	}
	
	/**
	 * @return <code>true</code> if a path was found, <code>false</code> otherwise.
	 */
	public boolean findPath(Entity e) {
		return findPath(new Vec3D(e.posX, e.posY, e.posZ));
	}

	/**
	 * @return <code>true</code> if a path was found, <code>false</code> otherwise.
	 */
	public boolean findPath(Vec3D pos) {
		path = player.getNavigator().getPathToXYZ(pos.x, pos.y, pos.z);
		if(path != null) {
			if(path.getCurrentPathLength() > 1) {
				path.setCurrentPathIndex(1);
			}
		}
		return hasPath();
	}
	
	/**
	 * @return <code>true</code> if the path is finished or <code>null</code>, <code>false</code> otherwise.
	 */
	public boolean followPath() {
		if(path == null) return true;
		PathPoint p = path.getPathPointFromIndex(path.getCurrentPathIndex());
		//player.worldObj.setBlock(p.xCoord, p.yCoord-1, p.zCoord, Block.glass.blockID);
		if(tryMoveTo(p.xCoord,p.yCoord,p.zCoord)) {
			path.incrementPathIndex();
			if(path.getCurrentPathIndex()>=path.getCurrentPathLength()) {
				this.clearPath();
				return true;
			}
		}
		return false;
	}
	
	public void clearPath() {
		path = null;
	}
	
	public void addVelocity(int side) {
		switch(side) {
		case POSX: addVelocity(new Vec3D( 1, 0,  0)); break;
		case NEGX: addVelocity(new Vec3D(-1, 0,  0)); break;
		case POSZ: addVelocity(new Vec3D( 0, 0,  1)); break;
		case NEGZ: addVelocity(new Vec3D( 0, 0, -1)); break;
		default: break;
		}
	}
	
	public void addVelocity(Vec3D vec) {
		if(vec.getLen()==0) return;
		checkBlocks(vec);
		if(player.onGround && !player.isInWater()) {
			vec.y = 0;
			if(vec.getLen()==0) return;
			vec.setLen(0.1F);
			player.addVelocity(vec.x, 0, vec.z);
		}
		else if(this.getMaterialRelative(0, 2, 0).isLiquid()) {
			player.setJumping(true);
			player.motionY = 0.2D;
			vec.y = 0;
			if(vec.getLen()==0) return;
			vec.setLen(0.03F);
			player.addVelocity(vec.x, 0, vec.z);
		}
		else if(this.getMaterialRelative(0, 1, 0).isLiquid()) {
			vec.y = 0;
			if(vec.getLen()==0) return;
			vec.setLen(0.04F);
			player.addVelocity(vec.x, 0, vec.z);
		}
		else {
			vec.y = 0;
			if(vec.getLen()==0) return;
			vec.setLen(0.03F);
			player.addVelocity(vec.x, 0, vec.z);
		}
	}
	
	public static final int POSY = 0;
	public static final int NEGY = 1;
	public static final int POSZ = 2;
	public static final int NEGZ = 3;
	public static final int POSX = 4;
	public static final int NEGX = 5;
}
