package d4rk.mc.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.src.ItemStack;

import d4rk.mc.PlayerWrapper;

public abstract class GroupSortChest extends Operation {

	public GroupSortChest() {
		this(null);
	}
	
	public GroupSortChest(PlayerWrapper pWrap) {
		super(pWrap, 0);
	}

	@Override
	public boolean canDoOperation(int currentInventoryType) {
		if (currentInventoryType != inventoryType)
			return false;
		return pWrap.hasOpenInventoryGUI();
	}
	
	protected Set<ItemGroup> getItemGroups() {
		HashMap<ItemGroup, Integer> grps = new HashMap<ItemGroup, Integer>();
		
		List<ItemStack> inv = pWrap.player.openContainer.getInventory();
		for(int i = 0; i < inv.size() - 36; ++i) {
			ItemGroup group = new ItemGroup(inv.get(i));
			Integer count = grps.get(group);
			grps.put(group, (count == null) ? 1 : count + 1);
		}
		
		TreeSet<ItemGroup> sorted_grps = new TreeSet<ItemGroup>();
		
		for(Entry<ItemGroup, Integer> e : grps.entrySet()) {
			sorted_grps.add(e.getKey().setCount(e.getValue()));
		}
		
		return sorted_grps;
	}
}
