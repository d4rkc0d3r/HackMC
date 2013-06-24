package d4rk.mc.inventory;

import static net.minecraft.src.EnchantmentHelper.getEnchantments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemBow;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemSword;
import net.minecraft.src.ItemTool;

public class ItemGroup implements Comparable<ItemGroup> {
	private final int hashCode;
	private final String toString;
	
	private final ItemStack ref;
	private final List<ItemCompare> items = new ArrayList<ItemCompare>();
	private boolean isItemListSorted = true;

	/**
	 * Does not {@link #add(ItemStack) add} the ref to the internal list.
	 * 
	 * @param ref
	 */
	public ItemGroup(ItemStack ref) {
		this.ref = ref;
		this.hashCode = 0xc0d3b4b3 ^ ((ref == null) ? 0 : ref.itemID * 0xb00b);
		this.toString = (ref == null) ? "Air" : ref.getDisplayName();
	}
	
	/**
	 * Adds the item to the internal list.
	 * 
	 * @param item The {@link net.minecraft.src.ItemStack item} to add.
	 * @return {@code this}
	 */
	public ItemGroup add(ItemStack item) {
		items.add(new ItemCompare(item));
		isItemListSorted = false;
		return this;
	}
	
	/**
	 * Returns a sorted copy of the internal item list.
	 * 
	 * @return The sorted copy of the internal item list.
	 */
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
	
	/**
	 * Returns the number of {@link net.minecraft.src.ItemStack ItemStacks}
	 * stored in the internal list.<br>
	 * The {@link #compareTo(ItemGroup) compareTo} method will sort the
	 * {@link ItemGroup} with this as sorting key.
	 * 
	 * @return The number of {@link net.minecraft.src.ItemStack ItemStacks} in
	 *         the internal list.
	 */
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
		if(this.equals(o)) {
			return 0;
		} else if(getCount() == o.getCount()) {
			return -ItemCompare.compare(ref, o.ref);
		} else {
			return (getCount() < o.getCount()) ? 1 : -1;
		}
	}
	
	public static boolean equals(ItemStack a, ItemStack b) {
		if(a == null || b == null) {
			return a == b;
		}
		if(a.itemID != b.itemID) {
			return false;
		}
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
					|| bEnch.contains(Enchantment.fortune.effectId)) {
				return aEnch.contains(Enchantment.fortune.effectId)
						&& bEnch.contains(Enchantment.fortune.effectId);
			}
			if(aEnch.contains(Enchantment.silkTouch.effectId)
					|| bEnch.contains(Enchantment.silkTouch.effectId)) {
				return aEnch.contains(Enchantment.silkTouch.effectId)
						&& bEnch.contains(Enchantment.silkTouch.effectId);
			}
			checkDamage = false;
		}
		if(a.getItem() instanceof ItemSword) {
			if(aEnch.isEmpty() && bEnch.isEmpty()) {
				return true;
			}
			if(aEnch.contains(Enchantment.looting.effectId)
					|| bEnch.contains(Enchantment.looting.effectId)) {
				return aEnch.contains(Enchantment.looting.effectId)
						&& bEnch.contains(Enchantment.looting.effectId);
			}
			checkDamage = false;
		}
		if(a.getItem().isDamageable() || a.getItem() instanceof ItemArmor
				|| a.getItem() instanceof ItemBow) {
			checkDamage = false;
		}
		if(!aEnch.isEmpty()) {
			return !bEnch.isEmpty();
		} else if(!bEnch.isEmpty()) {
			return false;
		}
		return (checkDamage) ? a.getItemDamage() == b.getItemDamage() : false;
	}
}
