package d4rk.mc;

public class ChestShop extends Shop {
	public static ChestShop parse(BlockWrapper block) {
		if (!block.isSign()
				|| block.getSignLine(0).isEmpty()
				|| block.getSignLine(1).isEmpty()
				|| block.getSignLine(2).isEmpty()
				|| block.getSignLine(3).isEmpty()
				|| !(block.getSignLine(2).startsWith("S") || block.getSignLine(2).startsWith("B")))
			return null;
		
		ChestShop shop = new ChestShop();
		shop.block = block;
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
}
