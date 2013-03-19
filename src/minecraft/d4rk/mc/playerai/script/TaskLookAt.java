package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskLookAt extends ScriptTask {
	private Vec3D target = null;
	

	public TaskLookAt(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		try {
			target = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new TooFewArgumentsException(cmd[0]);
		}
	}

	public TaskLookAt(String name) {
		super(name);
	}

	public void onTick() {
		if(isStopped) return;
		if(isDone()) return;
		pWrap.lookAt(target);
		done(NONE);
	}
}
