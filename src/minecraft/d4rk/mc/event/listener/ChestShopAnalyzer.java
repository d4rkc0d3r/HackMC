package d4rk.mc.event.listener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.Packet130UpdateSign;
import net.minecraft.src.Packet132TileEntityData;
import d4rk.mc.BlockWrapper;
import d4rk.mc.ChatColor;
import d4rk.mc.ChestShop;
import d4rk.mc.Permission;
import d4rk.mc.PlayerString;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PostProcessPacketEvent;
import d4rk.mc.util.Pair;

public class ChestShopAnalyzer implements EventListener {
	HashSet<ChestShop> shopList = new HashSet<ChestShop>();
	
	public ChestShopAnalyzer() {
		
	}
	
	public void onSignUpdate(PostProcessPacketEvent event) {
		if(!(event.getPacket() instanceof Packet130UpdateSign))
			return;
		Packet130UpdateSign p = (Packet130UpdateSign) event.getPacket();
		BlockWrapper block = new BlockWrapper(p.xPosition, p.yPosition, p.zPosition);
		ChestShop shop = ChestShop.parse(block);
		if(shop != null) {
			shopList.add(shop);
		}
	}
	
	public void onCommand(CommandEvent event) {
		if(event.getCommand().equalsIgnoreCase("shop")) {
			event.setDisabled(true);
			if(!event.getSender().hasPermission(Permission.INFO)) {
				event.getSender().sendSilent(Permission.NO_PERMISSION);
				return;
			}
			
			PlayerString sender = event.getSender();
			String name = event.getArg(2).replace("_", " ");
			boolean up = event.getArg(1).equalsIgnoreCase("sell");
			if(event.getArg(3).equalsIgnoreCase("up")) {
				up = true;
			} else if(event.getArg(3).equalsIgnoreCase("down")) {
				up = false;
			}
			int count;
			try {
				count = Integer.valueOf(event.getArg(4));
			} catch(NumberFormatException e1) {
				try {
					count = Integer.valueOf(event.getArg(3));
				} catch(NumberFormatException e2) {
					count = 5;
				}
			}
			
			if(event.getArg(1).equalsIgnoreCase("name")) {
				HashSet<String> hs = new HashSet();
				for(ChestShop s : shopList) {
					if(s.getItemName().toLowerCase().contains(name.toLowerCase())) {
						hs.add(s.getItemName());
					}
				}
				sender.sendSilent("Item's containing '" + name + "':");
				sender.sendSilent(" " + hs.toString());
			} else if(event.getArg(1).equalsIgnoreCase("sell")
					|| event.getArg(1).equalsIgnoreCase("buy")) {
				boolean isSell = event.getArg(1).equalsIgnoreCase("sell");
				List<Pair<Double, ChestShop>> list = new LinkedList();
				
				// Initializing the list with the prices.
				for (ChestShop cs : shopList) {
					if (cs.getItemName().equalsIgnoreCase(name)) {
						if (isSell && cs.isSell()) {
							list.add(new Pair(cs.getSinglePriceSell(), cs));
						} else if (!isSell && cs.isBuy()) {
							list.add(new Pair(cs.getSinglePriceBuy(), cs));
						}
					}
				}
				
				// Check for an empty list and limit count by list.size()
				if (list.isEmpty()) {
					sender.sendSilent("No " + event.getArg(1)
							+ " shop with item '" + name + "' found.");
					return;
				}
				count = Math.min(count, list.size());

				/*
				 * Selection sort, because we just want the first count
				 * elements. So selection sort is the simple & fast solution.
				 */
				for (int i = 0; i < count; i++) {
					Pair<Double, ChestShop> sel = list.get(i);
					int indexSel = i;
					for (int j = i + 1; j < list.size(); j++) {
						Pair<Double, ChestShop> cur = list.get(j);
						if ((up && (cur.getFirst() > sel.getFirst()))
								|| (!up && (cur.getFirst() < sel.getFirst()))) {
							indexSel = j;
							sel = cur;
						}
					}
					if (indexSel != i) {
						Pair tmp = list.get(i);
						list.set(i, list.get(indexSel));
						list.set(indexSel, tmp);
					}
				}
				
				// Now send the command sender the result.
				sender.sendSilent("The top " + count + " " + event.getArg(1) + " shops for '" + name + "':");
				int perCountItems = 64;
				for(int i = 0; i< count; i++) {
					Pair<Double, ChestShop> e = list.get(i);
					sender.sendSilent(String.format(" %.2f/", e.getFirst() * perCountItems)
							+ perCountItems + " at " + e.getSecond().getBlock() 
							+ " from " + e.getSecond().getUserName());
				}
			} else if(event.getArg(1).equalsIgnoreCase("check")) {
				EventManager.fireEvent(new CommandEvent(("shop buy " + event.getArg(2) + " 3").split(" "), sender));
				EventManager.fireEvent(new CommandEvent(("shop sell " + event.getArg(2) + " 2").split(" "), sender));
			} else if(event.getArg(1).equalsIgnoreCase("count")) {
				sender.sendSilent("Number of shop's in total: " + shopList.size());
			} else if(event.getArg(1).equalsIgnoreCase("countp")) {
				if(event.getArg(2).isEmpty()) {
					sender.sendSilent("Number of shop's in total: " + shopList.size());
				} else {
					int countp = 0;
					for(ChestShop cs : shopList) {
						if(cs.getUserName().equalsIgnoreCase(event.getArg(2))) {
							countp++;
						}
					}
					sender.sendSilent(event.getArg(2) + " has " + countp + " shops.");
				}
			} else if(event.getArg(1).equalsIgnoreCase("list")) {
				sender.sendSilent("Shop list:");
				for(ChestShop cs : shopList) {
					if(event.getArg(2).isEmpty() || cs.getUserName().equalsIgnoreCase(event.getArg(2))) {
						sender.sendSilent(" " + cs);
					}
				}
			} else {
				sender.sendSilent(ChatColor.RED + "'" + event.getArg(1)
						+ "' is not a valid shop command.");
			}
		}
	}
}
