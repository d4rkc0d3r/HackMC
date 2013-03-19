package d4rk.mc;

public class ChestShop {
	private BlockWrapper block;
	private String userName;
	private int count;
	private double priceBuy = -1;
	private double priceSell = -1;
	private boolean isBuy;
	private boolean isSell;
	private String itemName;
	private String toString = null;

	private ChestShop(BlockWrapper block) {
		this.block = block;
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
	
	public static ChestShop parse(BlockWrapper block) {
		if (!block.isSign()
				|| block.getSignLine(0).isEmpty()
				|| block.getSignLine(1).isEmpty()
				|| block.getSignLine(2).isEmpty()
				|| block.getSignLine(3).isEmpty()
				|| !(block.getSignLine(2).startsWith("S") || block.getSignLine(2).startsWith("B")))
			return null;
		
		ChestShop shop = new ChestShop(block);
		shop.userName = block.getSignLine(0);
		try {
			shop.count = Integer.valueOf(block.getSignLine(1));
		} catch(NumberFormatException e) {
			return null;
		}
		shop.itemName = block.getSignLine(3);
		
		String str = block.getSignLine(2);
		shop.isBuy = str.contains("B");
		shop.isSell = str.contains("S");
		if(str.startsWith("S")) {
			try {
				shop.priceSell = Double.valueOf(str.substring(1).trim());
			} catch(NumberFormatException e) {
				return null;
			}
		} else {
			try {
				String[] split = str.substring(1).split(":");
				shop.priceBuy = Double.valueOf(split[0].trim());
				if(split.length > 1) {
					shop.priceSell = Double.valueOf(split[1].replace("S", "").trim());
				}
			} catch(NumberFormatException e) {
				return null;
			}
		}
		
		return shop;
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
