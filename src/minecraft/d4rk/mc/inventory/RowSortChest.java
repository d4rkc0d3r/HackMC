package d4rk.mc.inventory;

import java.util.List;
import java.util.Set;

import net.minecraft.src.ItemStack;
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
		
		if(size % 9 == 0) {
			// first check if we could manage to not have to split a line into more than one ItemGroup
			int simpleLinesNeeded = 0;
			for(ItemGroup i : itemGroups) {
				simpleLinesNeeded += (i.getCount() - 1) / 9 + 1;
			}
			
			if(simpleLinesNeeded <= size / 9) {
				int invIndex = 0;
				for(ItemGroup iGroup : itemGroups) {
					int end = invIndex + iGroup.getCount();
					int airEnd = invIndex + ((iGroup.getCount() - 1) / 9 + 1) * 9;
					for(;invIndex < end; ++invIndex) {
						int iSwap = -1;
						for(int search = invIndex; search < size; ++search) {
							if(iGroup.equals(new ItemGroup(inv.get(search)))) {
								if(iSwap == -1) {
									iSwap = search;
								} else if(ItemCompare.compare(inv.get(iSwap), inv.get(search)) < 0) {
									iSwap = search;
								}
							}
						}
						if(swapInInventory(invIndex, iSwap) > 0) {
							inv = pWrap.player.openContainer.getInventory();
							return; // so we can see it step by step ;)
						}
					}
					for(;invIndex < airEnd; ++invIndex) {
						if(inv.get(invIndex) == null) {
							continue;
						}
						for(int search = invIndex + 1; search < size; ++search) {
							if(inv.get(search) == null) {
								swapInInventory(invIndex, search);
								inv = pWrap.player.openContainer.getInventory();
								//return; // so we can see it step by step ;)
							}
						}
					}
				}
			} else {
				ItemStack[][] newInv = new ItemStack[size / 9][9];
				
				boolean fillLeft = true;
				boolean cantSort = false;
				int row = 0;
				
				for(ItemGroup iGroup : itemGroups) {
					int rowsNeeded = (iGroup.getCount() - 1) / 9 + 1;
					if(row + rowsNeeded > newInv.length) {
						if(fillLeft) {
							fillLeft = false;
							row = 0;
						} else {
							cantSort = true;
							break;
						}
					}
					if(fillLeft) {
						if(iGroup.getCount() > 9 && iGroup.getCount() < 13) {
							List<ItemStack> items = iGroup.getItems();
							for(int i = 0; i < items.size(); ++i) {
								newInv[row + i / 6][i % 6] = items.get(i);
							}
						} else {
							List<ItemStack> items = iGroup.getItems();
							for(int i = 0; i < items.size(); ++i) {
								newInv[row + i / 9][i % 9] = items.get(i);
							}
						}
						row += rowsNeeded;
					} else {
						if(rowsNeeded == 1) {
							if(newInv[newInv.length - row - 1][9 - iGroup.getCount()] == null) {
								List<ItemStack> items = iGroup.getItems();
								for(int i = 0; i < items.size(); ++i) {
									newInv[newInv.length - row - 1][8 - i] = items.get(i);
								}
								row += rowsNeeded;
							} else {
								cantSort = true;
								break;
							}
						} else {
							cantSort = true;
							break;
						}
					}
				}
				
				if(cantSort) {
					int invIndex = 0;
					for(ItemGroup iGroup : itemGroups) {
						int end = invIndex + iGroup.getCount();
						for(;invIndex < end; ++invIndex) {
							int iSwap = -1;
							for(int search = invIndex; search < size; ++search) {
								if(iGroup.equals(new ItemGroup(inv.get(search)))) {
									if(iSwap == -1) {
										iSwap = search;
									} else if(ItemCompare.compare(inv.get(iSwap), inv.get(search)) < 0) {
										iSwap = search;
									}
								}
							}
							if(swapInInventory(invIndex, iSwap) > 0) {
								inv = pWrap.player.openContainer.getInventory();
								return; // so we can see it step by step ;)
							}
						}
					}
				} else {
					for(int i = 0; i < size; ++i) {
						if(ItemCompare.compare(inv.get(i), newInv[i / 9][i % 9]) != 0) {
							for(int j = size - 1; j > i; --j) {
								if(ItemCompare.compare(inv.get(j), newInv[i / 9][i % 9]) == 0) {
									if(swapInInventory(j, i) > 0) {
										inv = pWrap.player.openContainer.getInventory();
										return; // so we can see it step by step ;)
									} else {
										break;
									}
								}
							}
						}
					}
				}
			}
		} else {
			
		}
		
		done();
	}
}
