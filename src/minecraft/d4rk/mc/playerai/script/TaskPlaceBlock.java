package d4rk.mc.playerai.script;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskPlaceBlock extends ScriptTask {
	private BlockWrapper target = null;

	public TaskPlaceBlock(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		Vec3D pos;
		try {
			pos = pWrap.getPosition(cmd[1], cmd[2], cmd[3]);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new TooFewArgumentsException("Need a position to place a block at");
		}
		target = new BlockWrapper(pos, pWrap.player.worldObj);
		
		if(target.isReplaceable()) {
			for(int i = 0; i < 6; ++i) {
				BlockWrapper b = target.getRelative(i);
				if(b.isAir() || b.isLiquid()) {
					continue;
				} else {
					int side = -1;
					switch(i) {
					case 0:
						side = 1;
						break;
					case 1:
						side = 0;
						break;
					case 2:
						side = 3;
						break;
					case 3:
						side = 2;
						break;
					case 4:
						side = 5;
						break;
					case 5:
						side = 4;
						break;
					default:
						side = -1;
						break;
					}
					startScript("nolog: rightclick " + b.getString() + " " + side,
							"nolog: sleep 3");
					done();
					return;
				}
			}
			done(BAD);
		} else {
			done(BAD);
		}
	}

	public TaskPlaceBlock(String name) {
		super(name);
	}

	public void onTick() {
		runScriptParser();
	}
}
