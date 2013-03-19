package d4rk.mc.inventory;

import d4rk.mc.PlayerWrapper;
import net.minecraft.src.Packet;

public abstract class Operation {
	protected PlayerWrapper pWrap;
	protected int inventoryType;
	private boolean isDone = false;

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
	 * 
	 * @param pWrap
	 */
	public final void setPlayerWrapper(PlayerWrapper pWrap) {
		this.pWrap = pWrap;
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
