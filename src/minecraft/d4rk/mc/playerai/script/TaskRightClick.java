package d4rk.mc.playerai.script;

import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import d4rk.mc.BlockWrapper;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskRightClick extends ScriptTask {
	private BlockWrapper target = null;
	private int side = 1;

	public TaskRightClick(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		Vec3D target = new Vec3D(pWrap.player);
		target.x += 1;
		try {
			side = (cmd.length>4) ? pWrap.blockSideFromString(cmd[4]) : -1;
			target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
		} catch(Exception e) {} // like array index out of bounds exception
		
		this.target = new BlockWrapper(target, pWrap.player.worldObj);
		
		if(side == -1) {
			side = this.target.getNearestSide(pWrap.getPosition());
		}
		
		Vec3D vec = this.target.getSideCoords(side);
		this.startScript("nolog: lookat "+vec.x+" "+vec.y+" "+vec.z);
	}

	public TaskRightClick(String name) {
		super(name);
	}

	public void onTick() {
		if(runScriptParser()) return;
		if(isDone() || isStopped) return;
		
		done(NONE);
		
		pWrap.player.swingItem();
		
		if(Hack.onBlockRightClick(target.x, target.y, target.z, side)) return;
		
		Vec3D off = target.getSideOffset(side);
		pWrap.send(new Packet15Place(target.x, target.y, target.z, side, pWrap.player.getCurrentEquippedItem(),
									 (float)off.x, (float)off.y, (float)off.z));
	}
}
