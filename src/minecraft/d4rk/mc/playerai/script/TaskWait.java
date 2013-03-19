package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;

public class TaskWait extends ScriptTask {
	int ticksRemaining = 0;
	
	protected TaskWait(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		try {
			ticksRemaining = Integer.parseInt(cmd[1]);
		} catch(Exception e) {
			ticksRemaining = 20;
		}
		if(ticksRemaining <= 0)
			done(NONE);
	}
	
	public TaskWait(String name) {
		super(name);
	}
	
	public void onTick() {
		if(--ticksRemaining <= 0) {
			this.done(NONE);
		}
	}
}
