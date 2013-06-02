package d4rk.mc.inventory;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.ItemSword;
import static net.minecraft.src.EnchantmentHelper.getEnchantments;

public class ItemCompare implements Comparable<ItemCompare> {
	private ItemStack item;
	private int hashCode;
	private String toString;
	
	public ItemCompare(ItemStack item) {
		this.item = item;
		this.hashCode = 0xc0d3b4b3 ^ ((item == null) ? 0 :item.itemID * 0xb00b);
		this.toString = (item == null) ? "Air" : item.getDisplayName();
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		return toString();
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof ItemCompare) ? equals(item, ((ItemCompare)o).item) : false;
	}

	@Override
	public int compareTo(ItemCompare o) {
		return compare(item, o.item);
	}
	
	public static boolean equals(ItemStack a, ItemStack b) {
		return compare(a, b, true) == 0;
	}
	
	public static int compare(ItemStack a, ItemStack b) {
		return compare(a, b, false);
	}
	
	public static int compare(ItemStack a, ItemStack b, boolean ignoreStackSize) {
		if(a == null) {
			return (b == null) ? 0 : -1;
		}
		if(b == null) {
			return 1;
		}
		if(a.itemID == b.itemID) {
			if(a.getItemDamage() == b.getItemDamage()) {
				Map<Integer, Integer> aEnch = getEnchantments(a);
				Map<Integer, Integer> bEnch = getEnchantments(b);
				Set<Integer> allEnchIds = new TreeSet<Integer>();
				allEnchIds.addAll(aEnch.keySet());
				allEnchIds.addAll(bEnch.keySet());
				for(Integer i : allEnchIds) {
					Integer ae = aEnch.get(i);
					Integer be = bEnch.get(i);
					if(ae == be) {
						continue;
					} else if(ae == null) {
						return -1;
					} else if(be == null) {
						return 1;
					} else {
						return (ae < be) ? 1 : -1;
					}
				}
				return (ignoreStackSize || a.stackSize == b.stackSize) ? 0
						: (a.stackSize < b.stackSize) ? -1 : 1;
			}
			return (a.getItemDamage() < b.getItemDamage()) ? 1 : -1;
		} else {
			return (a.itemID < b.itemID) ? 1 : -1;
		}
	}
}
