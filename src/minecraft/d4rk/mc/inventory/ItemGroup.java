package d4rk.mc.inventory;

import static net.minecraft.src.EnchantmentHelper.getEnchantments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemSword;
import net.minecraft.src.ItemTool;

public class ItemGroup implements Comparable<ItemGroup> {
	private final int hashCode;
	private final String toString;
	
	private final ItemStack ref;
	private final List<ItemCompare> items = new ArrayList<ItemCompare>();
	private boolean isItemListSorted = true;

	public ItemGroup(ItemStack ref) {
		this.ref = ref;
		this.hashCode = 0xc0d3b4b3 ^ ((ref == null) ? 0 : ref.itemID * 0xb00b);
		this.toString = (ref == null) ? "Air" : ref.getDisplayName();
	}
	
	public ItemGroup add(ItemStack item) {
		items.add(new ItemCompare(item));
		isItemListSorted = false;
		return this;
	}
	
	public List<ItemStack> getItems() {
		List<ItemStack> ret = new ArrayList<ItemStack>(items.size());
		
		if(!isItemListSorted) {
			Collections.sort(items, Collections.reverseOrder());
			isItemListSorted = true;
		}
		
		for(int i = 0; i < items.size(); ++i) {
			ret.add(items.get(i).getItem());
		}
		
		return ret;
	}
	
	public int getCount() {
		return items.size();
	}
	
	@Override
	public String toString() {
		return "[" + items.size() + " x " + toString + "]";
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ItemGroup)) {
			return false;
		}
		return equals(ref, ((ItemGroup)obj).ref);
	}

	@Override
	public int compareTo(ItemGroup o) {
		if(getCount() == o.getCount()) {
			if(this.equals(o)) {
				return 0;
			} else {
				int res = toString.compareTo(o.toString);
				return (res == 0) ? ItemCompare.compare(ref, o.ref) : res;
			}
		} else {
			return (getCount() < o.getCount()) ? 1 : -1;
		}
	}
	
	public static boolean equals(ItemStack a, ItemStack b) {
		if(a == null || b == null) {
			return a == b;
		}
		if(a.itemID == b.itemID) {
			boolean checkDamage = true;
			Set<Integer> aEnch = getEnchantments(a).keySet();
			Set<Integer> bEnch = getEnchantments(b).keySet();
			if(a.itemID == Item.enchantedBook.itemID) {
				return aEnch.equals(bEnch);
			}
			if(a.getItem() instanceof ItemTool) {
				if(aEnch.isEmpty() && bEnch.isEmpty()) {
					return true;
				}
				if(aEnch.contains(Enchantment.fortune.effectId)
						&& bEnch.contains(Enchantment.fortune.effectId)) {
					return true;
				}
				if(aEnch.contains(Enchantment.silkTouch.effectId)
						&& bEnch.contains(Enchantment.silkTouch.effectId)) {
					return true;
				}
			} else if(a.getItem() instanceof ItemSword) {
				if(aEnch.isEmpty() && bEnch.isEmpty()) {
					return true;
				}
				if(aEnch.contains(Enchantment.looting.effectId)
						&& bEnch.contains(Enchantment.looting.effectId)) {
					return true;
				}
			}
			if(a.getItem().isDamageable() || a.getItem() instanceof ItemArmor) {
				checkDamage = false;
			}
			if(!aEnch.isEmpty()) {
				return !bEnch.isEmpty();
			} else if(!bEnch.isEmpty()) {
				return false;
			}
			return (checkDamage) ? a.getItemDamage() == b.getItemDamage() : false;
		}
		return false;
	}
}
