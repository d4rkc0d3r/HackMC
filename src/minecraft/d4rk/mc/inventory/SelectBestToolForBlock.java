package d4rk.mc.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.src.Block;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;

import d4rk.mc.BlockWrapper;
import d4rk.mc.ChatColor;
import d4rk.mc.PlayerWrapper;

/**
 * Doesn't work properly with items in the inventory. Everything is fine with
 * items in the hotbar.
 */
public class SelectBestToolForBlock extends Operation {
	private BlockWrapper block;
	
	public SelectBestToolForBlock(BlockWrapper block) {
		this(null, block);
	}
	
	public SelectBestToolForBlock(PlayerWrapper pWrap, BlockWrapper block) {
		super(pWrap, -1);
		this.block = block;
	}
	
	@Override
	public boolean canDoOperation(int currentInventoryType) {
		return currentInventoryType == inventoryType;
	}

	@Override
	public void doOperation() {
		this.done();
		
		Block b = block.getBlock();
		
		if(b == null) {
			return;
		}
		
		List<ItemStack> inv = pWrap.player.inventoryContainer.getInventory();
		int invStartOffset = 9;
		List<Integer> slotsToCheck = new ArrayList();
		
		boolean hasFortune = false;
		
		/*
		 * Remove the '+ 27' if you fix the swapping of items.
		 */
		for(int i = invStartOffset + 27; i < inv.size(); ++i) {
			ItemStack itemStack = inv.get(i);
			if(itemStack != null) {
				Item item = itemStack.getItem();
				if(item instanceof ItemTool) {
					if(itemStack.getItemDamage() == item.getMaxDamage()) {
						continue;
					}
				}
				if(b.blockMaterial.isToolNotRequired() || itemStack.canHarvestBlock(b)) {
					if(EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) > 0) {
						hasFortune = true;
					}
					slotsToCheck.add(i);
				}
			} else if(b.blockMaterial.isToolNotRequired()) {
				slotsToCheck.add(i);
				inv.set(i, new ItemStack(Block.dirt));
			}
		}
		
		if(hasFortune) {
			if(shouldUseFortuneList.contains(b.blockID)) {
				for(int i = 0; i < slotsToCheck.size(); ++i) {
					if(EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, inv.get(slotsToCheck.get(i))) == 0) {
						slotsToCheck.remove(i--);
					}
				}
			} else {
				for(int i = 0; i < slotsToCheck.size(); ++i) {
					if(EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, inv.get(slotsToCheck.get(i))) != 0) {
						slotsToCheck.remove(i--);
					}
				}
			}
		}
		
		if(slotsToCheck.size() == 0) {
			return;
		}
		
		float currentStrength = 0;
		int currentSlotId = -1;
		
		for(Integer i : slotsToCheck) {
			ItemStack itemStack = inv.get(i);
			float strength = itemStack.getStrVsBlock(b);
			if(strength > currentStrength) {
				currentSlotId = i;
				currentStrength = strength;
			}
		}
		
		int hotbarStartOffset = invStartOffset + 27;
		int currentHeldItemSlotId = pWrap.player.inventory.currentItem + hotbarStartOffset;
		
		if(currentStrength == 1F && slotsToCheck.contains(currentHeldItemSlotId)) {
			ItemStack is = pWrap.player.getCurrentEquippedItem();
			if(is == null || is.getStrVsBlock(b) == 1F) {
				currentSlotId = currentHeldItemSlotId;
			}
		}
		
		if(currentSlotId == currentHeldItemSlotId) {
			return;
		}
		
		// it is in the hotbar
		if(currentSlotId >= hotbarStartOffset) {
			pWrap.player.inventory.currentItem = currentSlotId - hotbarStartOffset;
			return;
		}

		swapInInventory(currentSlotId, currentHeldItemSlotId);
	}

	/**
	 * There are more blocks affected by fortune than in this list, but it is
	 * not worth to harvest them always with it.
	 */
	public static Set<Integer> shouldUseFortuneList = new TreeSet();
	
	static {
		shouldUseFortuneList.add(Block.oreCoal.blockID);
		shouldUseFortuneList.add(Block.oreDiamond.blockID);
		shouldUseFortuneList.add(Block.oreEmerald.blockID);
		shouldUseFortuneList.add(Block.oreLapis.blockID);
		shouldUseFortuneList.add(Block.oreRedstone.blockID);
		shouldUseFortuneList.add(Block.oreRedstoneGlowing.blockID);
		shouldUseFortuneList.add(Block.oreNetherQuartz.blockID);
		
		shouldUseFortuneList.add(Block.carrot.blockID);
		shouldUseFortuneList.add(Block.potato.blockID);
		shouldUseFortuneList.add(Block.netherStalk.blockID);
	}
}
