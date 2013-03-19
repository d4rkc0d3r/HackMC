package d4rk.mc.playerai.script;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Chunk;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Vec3D;

public class TaskShearSheepOnChunk extends ScriptTask {
	int index = -1;
	ArrayList<EntitySheep> targets = new ArrayList<EntitySheep>();

	public TaskShearSheepOnChunk(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		
		// get all the unsheared sheep's in the players chunk
		for(List l : pWrap.getChunk().entityLists)
			for(int i=0;i<l.size();i++)
				if(l.get(i) instanceof EntitySheep)
					if(!((EntitySheep)l.get(i)).getSheared())
						targets.add((EntitySheep) l.get(i));
		
		selectNextTarget();
	}

	public TaskShearSheepOnChunk(String name) {
		super(name);
	}
	
	private boolean selectNextTarget() {
		if(index != -1)
			targets.remove(index);
		if(targets.size() == 0) {
			done(NONE);
			return false;
		}
		index = pWrap.getNearest(targets);
		if(!pWrap.canMoveTo(targets.get(index)))
			return selectNextTarget();
		return true;
	}
	
	public void onTick() {
		if(runScriptParser()) return;
		if(isDone() || isStopped) return;
		
		EntitySheep target = targets.get(index);
		
		pWrap.lookAt(target, 180);
	
		if(pWrap.getCurrentItemID() != Item.shears.itemID) {
			if(!pWrap.selectItem(Item.shears))
				done(ITEM_NOT_FOUND);
			return;
		}
		
		if(!pWrap.canReach(target)) {
			pWrap.addVelocityToEntity(target);
			return;
		}
		
		pWrap.rightClickEntity(target);
		
		if(!selectNextTarget())
			return;
		
		this.startScript("nolog: wait 3");
	}
}
