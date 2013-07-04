package d4rk.mc;

public abstract class Shop {
	protected BlockWrapper block;
	protected String userName;
	protected int count;
	protected double priceBuy = -1;
	protected double priceSell = -1;
	protected boolean isBuy;
	protected boolean isSell;
	protected String itemName;
	protected String toString = null;
	
	protected Shop() {
		
	}
	
	public BlockWrapper getBlock() {
		return block;
	}

	public String getUserName() {
		return userName;
	}

	public int getCount() {
		return count;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public boolean isSell() {
		return isSell;
	}
	
	public double getPriceBuy() {
		return priceBuy;
	}

	public double getPriceSell() {
		return priceSell;
	}
	
	public double getSinglePriceBuy() {
		return priceBuy / count;
	}
	
	public double getSinglePriceSell() {
		return priceSell / count;
	}

	public String getItemName() {
		return itemName;
	}
	
	@Override
	public String toString() {
		if(toString == null) {
			toString = "[" + userName
					+ " | " + itemName
					+ " | " + count
					+ " | " + String.format("B:%.2f", priceBuy).replace(",00", "")
					+ " | " + String.format("S:%.2f", priceSell).replace(",00", "")
					+ " | " + block + "]";
		}
		return toString;
	}
	
	@Override
	public int hashCode() {
		return block.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			return ((ChestShop)obj).block.equals(block);
		} catch(Exception e) {
			return false;
		}
	}
}
