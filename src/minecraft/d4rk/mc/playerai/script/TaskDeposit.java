package d4rk.mc.playerai.script;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.inventory.Deposit;
import d4rk.mc.util.Vec3D;

public class TaskDeposit extends ScriptTask {
	private int id = -1;
	private int count = 27;
	private boolean ignoreHotbar = true;

	public TaskDeposit(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		
		if(cmd.length < 4) {
			throw new TooFewArgumentsException("You must specifie at least a position of a chest");
		}
		
		try {
			id = Integer.valueOf(cmd[4]);
			count = Integer.valueOf(cmd[5]);
			ignoreHotbar = Boolean.valueOf(cmd[6]);
		} catch(ArrayIndexOutOfBoundsException e) {
			// we use the the default values instead
		} catch(NumberFormatException e) {
			ScriptParser.log("[Warning] " + e.getMessage());
		}
		
		startScript("nolog: rightclick " + cmd[1] + " " + cmd[2] + " " + cmd[3],
				"nolog: sleep 3");
	}

	public TaskDeposit(String name) {
		super(name);
	}

	@Override
	public void onTick() {
		if(isDone() || runScriptParser() || isStopped) return;

		if(!pWrap.hasOpenInventoryGUI()) {
			return;
		}

		new Deposit(pWrap, id, count, ignoreHotbar).doOperation();
		
		startScript("nolog: sleep 10",
				"nolog: send /close guiscreen",
				"nolog: sleep 3");
		
		done();
		isStopped = true;
	}
}
