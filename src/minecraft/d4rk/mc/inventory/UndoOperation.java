package d4rk.mc.inventory;

import java.util.LinkedList;

import d4rk.mc.PlayerWrapper;
import d4rk.mc.util.Pair;

public class UndoOperation extends Operation {
	private LinkedList<Pair<Integer, Integer>> undoOperations;

	public UndoOperation(PlayerWrapper pWrap, LinkedList<Pair<Integer, Integer>> undoList) {
		super(pWrap, 0);
		this.undoOperations = undoList;
	}
	
	@Override
	public boolean canDoOperation(int inventoryType) {
		return pWrap.hasOpenInventoryGUI();
	}

	@Override
	public void doOperation() {
		while(!undoOperations.isEmpty()) {
			Pair<Integer, Integer> swap = undoOperations.removeLast();
			swapInInventory(swap.getFirst(), swap.getSecond());
			return; //step by step operation
		}
		done();
	}
}
