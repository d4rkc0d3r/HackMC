package d4rk.mc.playerai.script;

import d4rk.mc.PlayerWrapper;

public class TaskSelectItem extends ScriptTask {
	private int target = 0;

	public TaskSelectItem(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		try {
			target = Integer.valueOf(cmd[1]);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new ScriptParserException(cmd[0] + " needs an argument.");
		}
		if(pWrap.getCurrentItemID() == target) {
			done(NONE);
			return;
		}
		pWrap.selectItem(target);
	}

	public TaskSelectItem(String name) {
		super(name);
	}
	
	public void onTick() {
		if(isDone() || isStopped) return;
		if(pWrap.getCurrentItemID() == target)
			this.done(NONE);
		else
			this.done(ITEM_NOT_FOUND);
	}
}
