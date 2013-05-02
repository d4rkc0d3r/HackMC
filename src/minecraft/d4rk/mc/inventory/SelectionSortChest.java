package d4rk.mc.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ItemStack;

import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.util.Pair;

public class SelectionSortChest extends Operation {
	private boolean up;
	
	public SelectionSortChest(PlayerWrapper pWrap, boolean up) {
		super(pWrap, 0);
		this.up = up;
	}

	@Override
	public boolean canDoOperation(int currentInventoryType) {
		if (currentInventoryType != inventoryType)
			return false;
		return pWrap.hasOpenInventoryGUI();
	}

	@Override
	public void doOperation() {
		List<ItemStack> inv = pWrap.player.openContainer.getInventory();
		int size = inv.size() - 36;
		
		// Selection sort, because not the check is expensive, its the swap operation.
		for (int j = 0; j < size - 1; j++) {
			int swapIndex = j;
			for (int i = j + 1; i < size; i++) {
				if(((InventoryHelper.compareItemStack(inv.get(swapIndex), inv.get(i)) < 0) && up)
						|| (!up && (InventoryHelper.compareItemStack(inv.get(swapIndex), inv.get(i)) > 0))) {
					swapIndex = i;
				}
			}
			if(swapIndex != j) {
				pWrap.swapInInventory(j, swapIndex);
				inv = pWrap.player.openContainer.getInventory();
				return; // so we can see it step by step ;)
			}
		}
		done();
	}
	
	public final boolean UP = true;
	public final boolean DOWN = false;
}
