package d4rk.mc.event.listener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.src.Block;
import net.minecraft.src.Container;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.Packet101CloseWindow;
import net.minecraft.src.Packet102WindowClick;
import d4rk.mc.BlockWrapper;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PostProcessPacketEvent;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.event.PreProcessPacketEvent;
import d4rk.mc.event.PreSendPacketEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.inventory.Click;
import d4rk.mc.inventory.Operation;

public class InventoryHelper implements EventListener {
	private PlayerWrapper pWrap = null;
	private LinkedList<Operation> queue = new LinkedList();
	private boolean processQueue = false;
	private int inventoryType = -1;
	
	public InventoryHelper() {
		pWrap = Hack.getPlayerWrapper();
		instance = this;
	}
	
	public void onOpenInventory(PostProcessPacketEvent event) {
		if(!(event.getPacket() instanceof Packet100OpenWindow))
			return;
		processQueue = true;
		inventoryType = ((Packet100OpenWindow)event.getPacket()).inventoryType;
	}
	
	public void onCloseInventory(PostSendPacketEvent event) {
		if(!(event.getPacket() instanceof Packet101CloseWindow))
			return;
		processQueue = true;
		inventoryType = -1;
	}
	
	public void onTick(TickEvent event) {
		if (!processQueue)
			return;

		try {
			for (int i = 0; i < 1; i++) {
				Operation op = queue.get(0);

				if (!op.canDoOperation(inventoryType)) {
					processQueue = false;
					return;
				}

				op.doOperation();

				if (op.isDone())
					queue.removeFirst();
			}
		} catch (IndexOutOfBoundsException e) {
			processQueue = false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			processQueue = false;
		}
	}
	
	public void addToQueue(Operation op) {
		queue.addLast(op);
		if(pWrap != null && pWrap.isOk())
			op.setPlayerWrapper(pWrap);
		processQueue = true;
	}
	
	private void leftClickOn(int id) {
		addToQueue(new Click(pWrap, inventoryType, id, 0, false));
	}
	
	private void log(Object obj) {
		Hack.log("[InventoryHelper] " + obj);
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	public static int compareItemStack(ItemStack a, ItemStack b) {
		if(a == null) {
			return (b == null) ? 0 : -1;
		}
		if(b == null) {
			return 1;
		}
		if(a.itemID == b.itemID) {
			if(a.getItemDamage() == b.getItemDamage()) {
				Map<Integer, Integer> aEnch = EnchantmentHelper.getEnchantments(a);
				Map<Integer, Integer> bEnch = EnchantmentHelper.getEnchantments(b);
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
	
	public static void clearQueue() {
		instance.queue.clear();
		instance.processQueue = false;
	}
	
	public static InventoryHelper getInstance(PlayerWrapper pWrap) {
		instance.pWrap = pWrap;
		return instance;
	}

	public static InventoryHelper getInstance() {
		return getInstance(Hack.getPlayerWrapper());
	}
	
	private static InventoryHelper instance = null;
}
