package d4rk.mc.inventory;

import java.util.List;

import net.minecraft.src.ItemStack;
import d4rk.mc.PlayerWrapper;

/**
 * Shift click operations on the next opened chest.
 */
public class Withdraw extends Operation {
	/** The number of stacks to be shift clicked */
	private int count;
	private int id;

	/**
	 * Shift clicks everything that is in the chest.
	 */
	public Withdraw(PlayerWrapper pWrap) {
		this(pWrap, -1, 54);
	}

	/**
	 * Shift clicks everything in the chest with the specified id.
	 * 
	 * @param pWrap
	 * @param id
	 *            ItemID to be shift clicked
	 */
	public Withdraw(PlayerWrapper pWrap, Integer id) {
		this(pWrap, id, 54);
	}

	/**
	 * Withdraws items of the next opened chest or double chest.
	 * 
	 * @param pWrap
	 * @param id
	 *            ItemID to be shift clicked
	 * @param count
	 *            The number of stacks to be shift clicked
	 */
	public Withdraw(PlayerWrapper pWrap, Integer id, Integer count) {
		super(pWrap, 0);
		this.id = id;
		this.count = count;
	}
	
	@Override
	public boolean canDoOperation(int currentInventoryType) {
		switch (currentInventoryType) {
		case 0: // Chest
		case 3: // Dispenser
			return pWrap.hasOpenInventoryGUI();
		default:
			return false;
		}
	}

	@Override
	public void doOperation() {
		List inv = pWrap.player.openContainer.getInventory();
		int size = inv.size() - 36;
		int alreadyRemoved = 0;
		for (int index = 0; index < size; index++) {
			ItemStack item = (ItemStack) inv.get(index);
			if (item == null || (item.itemID != id && id != -1))
				continue;

			pWrap.windowClick(index, true);

			if (++alreadyRemoved == count)
				break;
		}
		done();
	}
}
