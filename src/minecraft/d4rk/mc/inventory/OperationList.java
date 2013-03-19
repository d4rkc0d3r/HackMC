package d4rk.mc.inventory;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import d4rk.mc.PlayerWrapper;

public class OperationList extends Operation {
	private LinkedList<Operation> queue = new LinkedList<Operation>();

	public OperationList() {
		super(null, 0);
	}

	public OperationList(PlayerWrapper pWrap) {
		super(pWrap, 0);
	}
	
	public OperationList add(Operation op) {
		queue.add(op);
		if(pWrap != null && pWrap.isOk())
			op.setPlayerWrapper(pWrap);
		return this;
	}

	@Override
	public boolean canDoOperation(int currentInventoryType) {
		if(queue.size() == 0)
			return true;
		return queue.getFirst().canDoOperation(currentInventoryType);
	}

	@Override
	public void doOperation() {
		while(queue.size() > 0) {
			Operation op = queue.getFirst();
			
			if(!op.canDoOperation(inventoryType)) {
				return;
			}
			
			op.doOperation();
			
			if(op.isDone())
				queue.removeFirst();
		}
		done();
	}

	@Override
	public int getInventoryType() {
		if(queue.size() == 0)
			return 0;
		return queue.getFirst().getInventoryType();
	}
	
	public int size() {
		return queue.size();
	}
}
