package d4rk.mc.playerai.script;

import net.minecraft.src.Packet3Chat;
import d4rk.mc.PlayerWrapper;

public class TaskSend extends ScriptTask {
	String message = null;
	
	public TaskSend(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		StringBuilder sb = new StringBuilder();
		for(int i=1;i<cmd.length;i++) {
			sb.append(cmd[i]).append(" ");
		}
		message = sb.toString().trim();
	}

	public TaskSend(String name) {
		super(name);
	}

	public void onTick() {
		if(isStopped || isDone()) return;
		pWrap.send(new Packet3Chat(message));
		done();
	}
}
