package d4rk.mc.playerai;

import d4rk.mc.BlockWrapper;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class QuarryAI extends BaseAI {
	private Quarry quarry = null;
	private BlockWrapper current = null;
	private Integer lastY = null;
	
	public QuarryAI(PlayerWrapper player, BlockWrapper edge1, BlockWrapper edge2) {
		super(player);
		quarry = new Quarry(edge1, edge2);
		current = quarry.getNext();
		if(current != null) {
			lastY = current.y;
		}
	}
	
	public QuarryAI(PlayerWrapper player, BlockWrapper sign) {
		super(player);
		quarry = new Quarry(sign);
		current = quarry.getNext();
		if(current != null) {
			lastY = current.y;
		}
	}

	public void onTick() {
		if(this.isStopped || this.runScriptParser()) return;
		
		if(current == null) {
			if(quarry.isDone()) {
				this.stop();
				quarry.reset();
				return;
			}
			current = quarry.getCurrent();
		}
		
		if(current != null && current.isAir()) {
			current = quarry.getNext();
		}
		
		if(current == null) {
			this.stop();
			quarry.reset();
			return;
		}
		
		if(lastY > current.y) {
			//TODO: gather items on the ground
			lastY = current.y;
		}
		
		//TODO: if inventory is full then deposit stuff in a chest if he has one in the hotbar
		
		startScript(new String[] {
			"nolog: pathto " + current.getPositionString() + " 3.5",
			"nolog: mineblock " + current.getString()
		});
		
		quarry.oneLayerUp();
	}
}
