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

public class ItemCompare {
	public static boolean sameItemGroup(ItemStack a, ItemStack b) {
		if(a == null || b == null) {
			return a == b;
		}
		if(a.itemID == b.itemID) {
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
				return false;
			}
			if(a.getItem() instanceof ItemSword) {
				if(aEnch.isEmpty() && bEnch.isEmpty()) {
					return true;
				}
				if(aEnch.contains(Enchantment.looting.effectId)
						&& bEnch.contains(Enchantment.looting.effectId)) {
					return true;
				}
				return false;
			}
			if(aEnch.isEmpty() && bEnch.isEmpty()) {
				return true;
			}
			return !aEnch.isEmpty() && !bEnch.isEmpty();
		}
		return false;
	}
	
	public static int sort(ItemStack a, ItemStack b) {
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
				return 0;
			}
			return (a.getItemDamage() < b.getItemDamage()) ? 1 : -1;
		} else {
			return (a.itemID < b.itemID) ? 1 : -1;
		}
	}
}
