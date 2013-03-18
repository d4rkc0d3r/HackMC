package d4rk.mc.playerai.script;

import net.minecraft.src.Block;
import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskActivateNearestLift extends ScriptTask {
	private int rad = 16;
	private int yOffset = 1;
	private BlockWrapper target = null;

	public TaskActivateNearestLift(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		
		try {
			rad = Integer.parseInt(cmd[1]);
			yOffset = Integer.parseInt(cmd[2]);
		} catch(Exception e) {}
		
		BlockWrapper current = null;
		
		BlockWrapper[] blocks = new BlockWrapper(pWrap.getPosition()).getAllBlocks(rad, yOffset);
		
		for(BlockWrapper block : blocks) {
			if(block.getSignLine(1).equalsIgnoreCase("[Lift Down]") || block.getSignLine(1).equalsIgnoreCase("[Lift Up]")) {
				if(current == null || pWrap.getDistance(current) > pWrap.getDistance(block)) {
					current = block;
				}
			}
		}
		
		if(current == null) {
			throw new ScriptParserException(LIFT_NOT_FOUND);
		}
		
		target = current;
	}

	public TaskActivateNearestLift(String name) {
		super(name);
	}

	public void onTick() {
		if(runScriptParser()) return;
		if(isDone() || isStopped) return;
		
		pWrap.lookAt(target, 20);
		
		if(!pWrap.canReach(target)) {
			pWrap.addVelocity(target.getSideCoords(BlockWrapper.INSIDE).sub(pWrap.getPosition()));
			return;
		}

		this.startScript("rightclick "+target.x+" "+target.y+" "+target.z+" "+target.getNearestSide(pWrap.getPosition()));
		
		done(NONE);
	}
}
