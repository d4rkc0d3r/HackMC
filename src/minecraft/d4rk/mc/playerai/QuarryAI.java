package d4rk.mc.playerai;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
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
			quarry.oneLayerUp();
		}
		
		if(current == null) {
			this.stop();
			quarry.reset();
			return;
		}
		
		if(lastY > current.y) {
			lastY = current.y;
			startScript("nolog: chunk.collectitems"); //TODO: not only collect items in the chunk
			return;
		}
		
		boolean isFull = true;
		boolean hasChest = false;
		for(int i = 0; i < pWrap.player.inventory.mainInventory.length; i++) {
			ItemStack item = pWrap.player.inventory.mainInventory[i];
			if(item == null) {
				isFull = false;
				break;
			} else if(item.isItemEqual(new ItemStack(Block.chest))) {
				hasChest = true;
			}
		}
		if(isFull && hasChest) {
			BlockWrapper[] stair = quarry.calculateStaircaise();
			for(BlockWrapper step : stair) {
				if(step.y - 1 == lastY) {
					startScript("nolog: pathto " + step.getPositionString() + " 3.8",
							"nolog: mineblock " + step.getString(),
							"nolog: selectitem " + Block.chest.blockID,
							"nolog: sleep 5",
							"nolog: placeblock " + step.getString(),
							"nolog: sleep 5",
							"nolog: deposit " + step.getString() + " -1 27 true");
					return;
				}
			}
		}
		
		if(pWrap.getPosition().getY() > current.y + 8) {
			int playerY = pWrap.getPosition().getY();
			BlockWrapper[] stair = quarry.calculateStaircaise();
			for(BlockWrapper step : stair) {
				if(step.y < playerY - 4) {
					startScript("nolog: pathto " + step.getPositionString());
					return;
				}
			}
		} else {
			startScript("nolog: pathto " + current.getPositionString() + " 3.8",
					"nolog: mineblock " + current.getString());
		}
	}
}
