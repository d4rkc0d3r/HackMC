package d4rk.mc.playerai;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class QuarryAI extends BaseAI {
	private Quarry quarry = null;
	private BlockWrapper current = null;
	
	public QuarryAI(PlayerWrapper player, BlockWrapper edge1, BlockWrapper edge2) {
		super(player);
		quarry = new Quarry(edge1, edge2);
		current = quarry.getNext();
	}
	
	public QuarryAI(PlayerWrapper player, BlockWrapper sign) {
		super(player);
		quarry = new Quarry(sign);
		current = quarry.getNext();
	}

	public void onTick() {
		if(this.isStopped) return;
		if(this.runScriptParser()) return;
		
		if(current == null) {
			if(quarry.isDone()) {
				this.stop();
				quarry.reset();
				return;
			}
			current = quarry.getCurrent();
		}
		
		if(current.isAir()) {
			current = quarry.getNext();
		}
		
		if(current == null) {
			this.stop();
			quarry.reset();
			return;
		}
		
		startScript(new String[] {
			"nolog: pathto " + current.getPositionString() + " 3.5",
			"nolog: mineblock " + current.getString()
		});
	}
}
