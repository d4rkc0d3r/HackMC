package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskPathTo extends ScriptTask {
	private Vec3D target = null;
	private double minDist = 0.5;

	public TaskPathTo(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		if(cmd.length < 4)
			throw new TooFewArgumentsException(cmd[0]);
		try {
			target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
			minDist = Double.valueOf(cmd[4]);
		} catch(ArrayIndexOutOfBoundsException e) {}
		if(!pWrap.findPath(target)) {
			this.done(NO_PATH_FOUND);
		}
		if(pWrap.getDistance(target) < minDist) {
			this.done(NONE);
		}
	}

	public TaskPathTo(String name) {
		super(name);
	}

	@Override
	public void onTick() {
		if(isStopped || isDone()) return;
		if(runScriptParser()) return;
		if(pWrap.getDistance(target) < minDist) {
			this.done(NONE);
			return;
		}
		if(!pWrap.hasPath()) {
			this.done(NO_PATH_FOUND);
			return;
		}
		if(pWrap.followPath()) {
			this.done(NONE);
		}
	}
}
