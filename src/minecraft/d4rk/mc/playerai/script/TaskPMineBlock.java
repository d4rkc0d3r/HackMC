package d4rk.mc.playerai.script;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskPMineBlock extends ScriptTask {
	private Vec3D target = null;
	private double minDist = 2.8;
	private BlockWrapper block = null;
	private int side = 1;

	public TaskPMineBlock(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		if(cmd.length < 4)
			throw new TooFewArgumentsException(cmd[0]);
		try {
			target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
			minDist = Double.valueOf(cmd[4]);
		} catch(ArrayIndexOutOfBoundsException e) {}
		
		block = new BlockWrapper(target);
		side = this.block.getNearestSide(pWrap.getPosition());
		
		if(!pWrap.findPath(block.getRelative(0, 1, 0).getSideCoords(BlockWrapper.INSIDE))) {
			this.done(NO_PATH_FOUND);
		}
		
		pWrap.selectBlock(block);
	}

	public TaskPMineBlock(String name) {
		super(name);
	}

	@Override
	public void onTick() {
		if(isStopped || isDone()) return;
		if(runScriptParser()) return;
		
		if(pWrap.canReach()) {
			pWrap.lookAt(block.getSideCoords(side));
			pWrap.selectToolForBlock(block);
			
			if(!block.isMineable()) {
				pWrap.stopMining();
				done(NONE);
				return;
			}
			
			pWrap.updateMining();
			if(pWrap.hasPath() && pWrap.getDistance(target) > minDist) {
				pWrap.followPath();
			}
		} else {
			if(!pWrap.hasPath()) {
				this.done(NO_PATH_FOUND);
				return;
			} else {
				pWrap.followPath();
			}
		}
	}
}
