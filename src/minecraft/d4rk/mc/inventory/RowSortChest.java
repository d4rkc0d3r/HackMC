package d4rk.mc.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.src.ItemStack;

import d4rk.mc.ImproveChat;
import d4rk.mc.PlayerWrapper;

public class RowSortChest extends GroupSortChest {

	public RowSortChest() {
		this(null);
	}

	public RowSortChest(PlayerWrapper pWrap) {
		super(pWrap);
	}

	@Override
	public void doOperation() {
		Set<ItemGroup> itemGroups = getItemGroups();
		
		List<ItemStack> inv = pWrap.player.openContainer.getInventory();
		int size = inv.size() - 36;
		
		ItemGroup air = new ItemGroup(null);
		
		for(ItemGroup i : itemGroups) {
			if(i.equals(air)) {
				air = i;
				break;
			}
		}
		
		itemGroups.remove(air);
		
//		ImproveChat.addToChatGui("Chest(" + (size - air.getCount()) + "/" + size + ") analysis:");
//		for(ItemGroup i : itemGroups) {
//			ImproveChat.addToChatGui(" " + i);
//		}

		// first check if we could manage to not have to split a line into more than one ItemGroup
		int simpleLinesNeeded = 0;
		for(ItemGroup i : itemGroups) {
			simpleLinesNeeded += (i.getCount() % 9 == 0) ? (i.getCount() / 9) : i.getCount() / 9 + 1;
		}
		
		if(simpleLinesNeeded <= size / 9) {
			int invIndex = 0;
			for(ItemGroup i : itemGroups) {
				int end = invIndex + i.getCount();
				int airEnd = invIndex + (i.getCount() / 9) * 9 + ((i.getCount() % 9 == 0) ? 0 : 9);
				for(;invIndex < end; ++invIndex) {
					if(i.equals(new ItemGroup(inv.get(invIndex)))) {
						continue;
					}
					for(int search = invIndex + 1; search < size; ++search) {
						if(i.equals(new ItemGroup(inv.get(search)))) {
							pWrap.swapInInventory(invIndex, search);
							inv = pWrap.player.openContainer.getInventory();
							return; // so we can see it step by step ;)
						}
					}
				}
				for(;invIndex < airEnd; ++invIndex) {
					if(inv.get(invIndex) == null) {
						continue;
					}
					for(int search = invIndex + 1; search < size; ++search) {
						if(inv.get(search) == null) {
							pWrap.swapInInventory(invIndex, search);
							inv = pWrap.player.openContainer.getInventory();
							//return; // so we can see it step by step ;)
						}
					}
				}
			}
		}
		
		done();
	}
}
