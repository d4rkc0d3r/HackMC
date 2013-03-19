package d4rk.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class PlayerAiInventoryHelper {
	private Minecraft mc = null;
	private EntityClientPlayerMP player = null;
	private World world = null;
	
	public PlayerAiInventoryHelper() {
		update();
	}
	
	public void update() {
		mc = Minecraft.getMinecraft();
		player = mc.thePlayer;
		world = player.worldObj;
	}
	
	public boolean isFull() {
		for(ItemStack item : player.inventory.mainInventory) if(item == null) return false; return true;
	}
	
	public void swap(int index) {
		swap(player.inventory.currentItem, index);
	}
	
	public void swap(int src, int tar) {
		ItemStack[] items = player.inventory.mainInventory;
		ItemStack a = items[src];
		ItemStack b = items[tar];
		items[src] = b;
		items[tar] = a;
	}
	
	public boolean select(int[] itemIDList) {
		for(int i=itemIDList.length-1;i>=0;i--) {
			if(select(itemIDList[i]))
				return true;
		}
		return false;
	}
	
	public boolean select(int id) {
		player.inventory.setCurrentItem(id, 0, false, false);
		if(player.inventory.getCurrentItem() != null && player.getCurrentEquippedItem().itemID == id)
			return true;
		return false;
	}
	
	public int find(int id) {
		ItemStack[] items = player.inventory.mainInventory;
		for(int i=0;i<items.length;i++) {
			if(items[i] != null && items[i].getItem().itemID == id)
				return i;
		}
		return -1;
	}
}
