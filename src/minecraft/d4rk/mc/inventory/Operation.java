package d4rk.mc.inventory;

import java.util.LinkedList;
import java.util.List;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Pair;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;

public abstract class Operation {
	protected PlayerWrapper pWrap;
	protected int inventoryType;
	private boolean isDone = false;
	private LinkedList<Pair<Integer, Integer>> undoList = new LinkedList<Pair<Integer, Integer>>();

	public Operation(PlayerWrapper pWrap, int inventoryType) {
		this.pWrap = pWrap;
		this.inventoryType = inventoryType;
	}

	/**
	 * Basically just:
	 * 
	 * <pre>
	 * {@code
	 * if (currentInventoryType != inventoryType)
	 *     return false;
	 * return pWrap.hasOpenInventoryGUI();
	 * }
	 * </pre>
	 * 
	 * Override this for chests in case that your method only provides support
	 * for one of the two chest sizes. Or you actually cover multiple Gui's with
	 * one operation.<br>
	 * Another need for overloading is if you want to edit the players inventory
	 * without opening it.
	 */
	public boolean canDoOperation(int currentInventoryType) {
		if (currentInventoryType != inventoryType)
			return false;
		return pWrap.hasOpenInventoryGUI();
	}

	/**
	 * Used by
	 * {@link d4rk.mc.event.listener.InventoryHelper#addToQueue(Operation)
	 * InventoryHelper.addToQueue(Operation)} and
	 * {@link OperationList#add(Operation)}so you can actually call the
	 * {@link #constructor(PlayerWrapper, int) Constructor} with an {@code null}
	 * argument.
	 */
	public final void setPlayerWrapper(PlayerWrapper pWrap) {
		this.pWrap = pWrap;
	}
	
	/**
	 * Swaps the content of the two slots indexed by the two parameters in the
	 * currently open inventory of the {@link Operation#pWrap pWrap} instance.<br>
	 * Returns -1 on failure. This happens if at least one of the two parameter
	 * indexes are out of bounds of the inventory array. Or it can not swap the
	 * two stacks. This only occurs if the whole inventory is filled with the
	 * same items and there is not a single full stack of it in the inventory.
	 * 
	 * @return number of mouse clicks performed or -1 if the swap failed
	 */
	public int swapInInventory(int indexA, int indexB) {
		int ret = swapInInventoryWorker(indexA, indexB);
		if(ret > 0) {
			undoList.addLast(new Pair(indexA, indexB));
		}
		return ret;
	}
	
	public LinkedList<Pair<Integer, Integer>> getUndoList() {
		return undoList;
	}
	
	private int swapInInventoryWorker(int indexA, int indexB) {
		try {
			List<ItemStack> inv = pWrap.player.openContainer.getInventory();
			
			ItemStack a = inv.get(indexA);
			ItemStack b = inv.get(indexB);
			
			if(indexA == indexB) {
				return 0;
			}
			
			if(ItemCompare.equals(a, b)) {
				if(a == null) {
					return 0;
				} else if(a.stackSize == a.getItem().getItemStackLimit()) {
					if(b.stackSize == a.stackSize) {
						return 0;
					}
					pWrap.windowClick(indexA);
					pWrap.windowClick(indexB);
					pWrap.windowClick(indexA);
					return 3;
				} else if(b.stackSize == b.getItem().getItemStackLimit()) {
					if(b.stackSize == a.stackSize) {
						return 0;
					}
					pWrap.windowClick(indexB);
					pWrap.windowClick(indexA);
					pWrap.windowClick(indexB);
					return 3;
				} else {
					int nullIndex = inv.indexOf(null);
					if(nullIndex != -1) {
						int count = swapInInventoryWorker(indexA, nullIndex);
						count += swapInInventoryWorker(indexA, indexB);
						return count + swapInInventoryWorker(indexB, nullIndex);
					} else {
						for(int i = 0; i < inv.size(); ++i) {
							if(i == indexA || i == indexB || (ItemCompare.equals(a, inv.get(i))
									&& (inv.get(i).stackSize != inv.get(i).getItem().getItemStackLimit()))) {
								continue;
							}
							int count = swapInInventoryWorker(indexA, i);
							count += swapInInventoryWorker(indexA, indexB);
							return count + swapInInventoryWorker(indexB, i);
						}
						return -1;
					}
				}
			} else {
				if(a == null) {
					pWrap.windowClick(indexB);
					pWrap.windowClick(indexA);
					return 2;
				} else if(b == null) {
					pWrap.windowClick(indexA);
					pWrap.windowClick(indexB);
					return 2;
				} else {
					pWrap.windowClick(indexA);
					pWrap.windowClick(indexB);
					pWrap.windowClick(indexA);
					return 3;
				}
			}
		} catch(Exception e) {
			return -1;
		}
	}

	protected void done() {
		isDone = true;
	}

	public boolean isDone() {
		return isDone;
	}

	public abstract void doOperation();

	public int getInventoryType() {
		return inventoryType;
	}
}
