package d4rk.mc.playerai.script;

import net.minecraft.src.Packet19EntityAction;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;

public class TaskSneak extends ScriptTask {

	public TaskSneak(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		
		int state = 0;
		
		if(cmd.length < 2) {
			state = 1;
		} else {
			if(cmd[2].equalsIgnoreCase("toggle")) {
				state = (pWrap.player.isSneaking()) ? 2 : 1;
			} else if(cmd[2].equalsIgnoreCase("on") || cmd[2].equalsIgnoreCase("true")) {
				state = 1;
			} else if(cmd[2].equalsIgnoreCase("off") || cmd[2].equalsIgnoreCase("false")) {
				state = 2;
			} else {
				throw new ScriptParserException(cmd[1] + " invalid argument: '" + cmd[2] + "' must be (on|off|toggle)");
			}
		}
		
		if(pWrap.player.isSneaking() != (state == 1)) {
			pWrap.player.setSneaking(state == 1);
			Hack.addPacket(new Packet19EntityAction(pWrap.player, state));
			startScript("nolog: sleep 3");
		}
		
		done();
	}

	public TaskSneak(String name) {
		super(name);
	}

	@Override
	public void onTick() {
		runScriptParser();
	}
}
