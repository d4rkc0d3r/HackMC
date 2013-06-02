package d4rk.mc.inventory;

import java.util.HashMap;
import java.util.HashSet;
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
		HashSet<ItemGroup> grps = new HashSet<ItemGroup>();
		List<ItemStack> inv = pWrap.player.openContainer.getInventory();
		
		for(int i = 0; i < inv.size() - 36; ++i) {
			boolean wasAdded = false;
			ItemStack item = inv.get(i);
			ItemGroup group = new ItemGroup(item);
			for(ItemGroup grp : grps) {
				if(grp.equals(group)) {
					grp.add(item);
					break;
				}
			}
			if(!wasAdded) {
				grps.add(group.add(item));
			}
		}
		
		TreeSet<ItemGroup> sorted_grps = new TreeSet<ItemGroup>();
		sorted_grps.addAll(grps);
		return sorted_grps;
	}
}
