package d4rk.mc.inventory;

import d4rk.mc.PlayerWrapper;
import net.minecraft.src.Container;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet102WindowClick;
import net.minecraft.src.Packet103SetSlot;

public class Click extends Operation {
	public boolean holdingShift;
	public int inventorySlot;
	public int mouseClick;

	public Click(PlayerWrapper pWrap, int inventoryType, int slot,
			int mouseClick, boolean shift) {
		super(pWrap, inventoryType);
		this.holdingShift = shift;
		this.inventorySlot = slot;
		this.mouseClick = mouseClick;
	}

	@Override
	public void doOperation() {
		pWrap.windowClick(inventorySlot, mouseClick, holdingShift);
		done();
	}
}
