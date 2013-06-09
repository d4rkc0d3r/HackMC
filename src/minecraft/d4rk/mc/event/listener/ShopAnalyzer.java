package d4rk.mc.event.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet130UpdateSign;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.Packet9Respawn;
import net.minecraft.src.RenderHelper;
import d4rk.mc.BlockWrapper;
import d4rk.mc.ChatColor;
import d4rk.mc.ChestShop;
import d4rk.mc.ImproveChat;
import d4rk.mc.Permission;
import d4rk.mc.PlayerString;
import d4rk.mc.Shop;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PostProcessPacketEvent;
import d4rk.mc.gui.BasicGuiScreen;
import d4rk.mc.util.Pair;

public class ShopAnalyzer extends BasicGuiScreen implements EventListener {
	private HashSet<Shop> shopList = new HashSet();
	private GuiTextField itemNameField;
	private GuiTextField modeField;
	private boolean isGuiOpen = false;
	private String modeText = "check";
	private String itemNameText = "";
	private List<String> possibleModes = new ArrayList();
	private int inputGuiTop = 35;
	private long lastOperationDuration = 0;
	private Iterator<String> autoComplete = null;
	private int autoCompleteIndex = -1;
	
	private Set<String> itemNameResultSet = new TreeSet();
	private List<String> viewResult = new ArrayList();
	
	private int count = 30;
	
	public ShopAnalyzer() {
		mc = Minecraft.getMinecraft();
		possibleModes.add("name");
		possibleModes.add("buy");
		possibleModes.add("sell");
		possibleModes.add("check");
	}
	
	@Override
	public void initGui() {
		this.itemNameField = new GuiTextField(fontRenderer, width / 2 - 160, inputGuiTop, 80, fontRenderer.FONT_HEIGHT);
        this.itemNameField.setMaxStringLength(20);
        this.itemNameField.setEnableBackgroundDrawing(true);
        this.itemNameField.setVisible(true);
        this.itemNameField.setTextColor(16777215);
        this.itemNameField.setText(itemNameText);
        
		this.modeField = new GuiTextField(fontRenderer, width / 2 - 160, inputGuiTop + 30, 80, fontRenderer.FONT_HEIGHT);
        this.modeField.setMaxStringLength(20);
        this.modeField.setEnableBackgroundDrawing(true);
        this.modeField.setVisible(true);
        this.modeField.setTextColor(16777215);
        this.modeField.setText(modeText);
        
        autoComplete = null;
        autoCompleteIndex = -1;
        
        isGuiOpen = true;
        updateResult();
        
        Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void onGuiClosed() {
		isGuiOpen = false;
        Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		itemNameField.mouseClicked(par1, par2, par3);
		modeField.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) {
		if(par2 == Keyboard.KEY_TAB) { // tab key was pressed
			if(itemNameField.isFocused()) {
				if(itemNameResultSet.size() > 0) {
					if(autoComplete == null || !autoComplete.hasNext()) {
						autoComplete = itemNameResultSet.iterator();
						autoCompleteIndex = -1;
					}
					++autoCompleteIndex;
					String newName = autoComplete.next();
					long startTime = System.nanoTime();
			        if(modeText.equalsIgnoreCase("buy") || modeText.equalsIgnoreCase("sell")) {
						viewResult.clear();
						count = 20;
						addToResult(modeText, newName);
					} else if(modeText.equalsIgnoreCase("check")) {
						viewResult.clear();
						count = 8;
						addToResult("buy", newName);
						addToResult("sell", newName);
					}
			        lastOperationDuration = (System.nanoTime() - startTime) / 1000;
			        int i = newName.toLowerCase().indexOf(itemNameText.toLowerCase());
			        StringBuilder sb = new StringBuilder();
			        if(i > 0) {
			        	sb.append(ChatColor.RED);
			        	sb.append(newName.substring(0, i));
			        }
		        	sb.append(ChatColor.WHITE);
		        	sb.append(newName.substring(i, i + itemNameText.length()));
		        	if(i + itemNameText.length() < newName.length()) {
			        	sb.append(ChatColor.RED);
			        	sb.append(newName.substring(i + itemNameText.length()));
		        	}
			        itemNameField.setText(sb.toString());
				}
			} else if(modeField.isFocused()) {
				itemNameField.setFocused(true);
				modeField.setFocused(false);
			} else {
				modeField.setFocused(true);
			}
		} else if(par2 == Keyboard.KEY_RETURN) {
			if(autoCompleteIndex != -1) {
				itemNameText = ChatColor.remove(itemNameField.getText());
				itemNameField.setText(itemNameText);
				autoComplete = null;
				autoCompleteIndex = -1;
			}
		} else if(itemNameField.textboxKeyTyped(par1, par2) || modeField.textboxKeyTyped(par1, par2)) {
			if(!modeText.equals(modeField.getText()) || !itemNameText.equals(itemNameField.getText())) {
				modeText = modeField.getText();
				if(autoCompleteIndex != -1) {
					itemNameField.setText(itemNameText);
					autoComplete = null;
					autoCompleteIndex = -1;
				} else {
					itemNameText = itemNameField.getText();
				}
				updateResult();
			}
		}
		super.keyTyped(par1, par2);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		
		if(!possibleModes.contains(modeField.getText().toLowerCase())) {
			drawTooltip(possibleModes, width / 2 - 159, inputGuiTop + 51);
		} else {
			if(modeField.getText().equalsIgnoreCase("name")) {
				int i = 0;
				for(String s : itemNameResultSet) {
					drawString(fontRenderer, s, width / 2 - 65 + (i % 3) * 85, inputGuiTop - 12 + (i / 3) * 12, 16777215);
					++i;
				}
			} else {
				for(int i = 0; i < viewResult.size(); ++i) {
					drawString(fontRenderer, viewResult.get(i), width / 2 - 60, inputGuiTop - 12 + i * 12, 16777215);
				}
				int i = 0;
				for(String s : itemNameResultSet) {
					if(i == autoCompleteIndex) {
						drawString(fontRenderer, "=>", width / 2 - 175, inputGuiTop + 50 + i * 12, 16777215);
					}
					drawString(fontRenderer, s, width / 2 - 160, inputGuiTop + 50 + i * 12, 16777215);
					++i;
				}
			}
		}

		drawString(fontRenderer, "Time needed: " + getTimeNeeded(), width / 2 - 160, 8, 16777215);
		drawString(fontRenderer, "Total shop count: " + shopList.size(), width / 2, 8, 16777215);
		
		drawString(fontRenderer, "Item Name", width / 2 - 160, inputGuiTop - 12, 16777215);
		itemNameField.drawTextBox();
		
		drawString(fontRenderer, "Mode", width / 2 - 160, inputGuiTop + 18, 16777215);
		modeField.drawTextBox();
		
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void clearResult() {
		viewResult.clear();
	}
	
	private void updateResult() {
		long startTime = System.nanoTime();
		itemNameResultSet.clear();
		for(Shop s : shopList) {
			if(s.getItemName().toLowerCase().contains(itemNameText.toLowerCase())) {
				itemNameResultSet.add(s.getItemName());
			}
		}
        if(modeText.equalsIgnoreCase("buy") || modeText.equalsIgnoreCase("sell")) {
			viewResult.clear();
			count = 20;
			addToResult(modeText);
		} else if(modeText.equalsIgnoreCase("check")) {
			viewResult.clear();
			count = 8;
			addToResult("buy");
			addToResult("sell");
		}
        lastOperationDuration = (System.nanoTime() - startTime) / 1000;
	}
	
	public void addToResult(String modeText) {
		addToResult(modeText, itemNameText);
	}
	
	public void addToResult(String modeText, String name) {
		if(modeText.equalsIgnoreCase("buy") || modeText.equalsIgnoreCase("sell")) {
			boolean isSell = modeText.equalsIgnoreCase("sell");
			boolean up = isSell;
			List<Pair<Double, Shop>> list = new LinkedList();
			
			// Initializing the list with the prices.
			for (Shop cs : shopList) {
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
				viewResult.add("No " + modeText.toLowerCase()
						+ " shop with item '" + name + "' found.");
				return;
			}
			int count = Math.min(this.count, list.size());

			/*
			 * Selection sort, because we just want the first count
			 * elements. So selection sort is the simple & fast solution.
			 */
			for (int i = 0; i < count; i++) {
				Pair<Double, Shop> sel = list.get(i);
				int indexSel = i;
				for (int j = i + 1; j < list.size(); j++) {
					Pair<Double, Shop> cur = list.get(j);
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
			viewResult.add("The top " + count + " " + modeText + " shops for '" + name + "':");
			int perCountItems = 64;
			for(int i = 0; i < count; i++) {
				Pair<Double, Shop> e = list.get(i);
				perCountItems = (e.getFirst() >= 1.2D) ? 1 : 64;
				viewResult.add(String.format(" %.2f/", e.getFirst() * perCountItems)
						+ perCountItems + " at " + e.getSecond().getBlock() 
						+ " from " + e.getSecond().getUserName());
			}
		}
	}
	
	private String getTimeNeeded() {
		String str = "" + lastOperationDuration;
		
		if(str.length() == 3) {
			str = "0." + str;
		} else if (str.length() == 2) {
			str = "0.0" + str;
		} else if (str.length() == 1) {
			str = "0.00" + str;
		} else if (str.length() == 0) {
			str = "0";
		} else {
			str = str.substring(0, str.length() - 3) + "," + str.substring(str.length() - 3);
		}
		
		return str + " ms";
	}
	
	public void onRespawn(PostProcessPacketEvent event) {
		if(!(event.getPacket() instanceof Packet9Respawn)) {
			return;
		}
		shopList.clear();
	}
	
	public void onSignUpdate(PostProcessPacketEvent event) {
		if(!(event.getPacket() instanceof Packet130UpdateSign))
			return;
		Packet130UpdateSign p = (Packet130UpdateSign) event.getPacket();
		BlockWrapper block = new BlockWrapper(p.xPosition, p.yPosition, p.zPosition);
		Shop shop = ChestShop.parse(block);
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
			itemNameText = event.getArg(2).replace("_", " ");
			try {
				count = Integer.valueOf(event.getArg(3));
			} catch(NumberFormatException e2) {
				count = 5;
			}
			
			if(event.getArg(1).equalsIgnoreCase("name")) {
				modeText = "name";
				updateResult();
				sender.sendSilent("Item's containing '" + itemNameText + "':");
				sender.sendSilent(" " + itemNameResultSet.toString());
			} else if(event.getArg(1).equalsIgnoreCase("sell")
					|| event.getArg(1).equalsIgnoreCase("buy")) {
				modeText = event.getArg(1).toLowerCase();
				updateResult();
				for(String s : viewResult) {
					sender.sendSilent(s);
				}
			} else if(event.getArg(1).equalsIgnoreCase("check")) {
				onCommand(new CommandEvent(("shop buy " + event.getArg(2) + " 3").split(" "), sender));
				onCommand(new CommandEvent(("shop sell " + event.getArg(2) + " 2").split(" "), sender));
			} else if(event.getArg(1).equalsIgnoreCase("count")) {
				sender.sendSilent("Number of shop's in total: " + shopList.size());
			} else if(event.getArg(1).equalsIgnoreCase("countp")) {
				if(event.getArg(2).isEmpty()) {
					sender.sendSilent("Number of shop's in total: " + shopList.size());
				} else {
					int countp = 0;
					for(Shop cs : shopList) {
						if(cs.getUserName().equalsIgnoreCase(event.getArg(2))) {
							countp++;
						}
					}
					sender.sendSilent(event.getArg(2) + " has " + countp + " shops.");
				}
			} else if(event.getArg(1).equalsIgnoreCase("list")) {
				sender.sendSilent("Shop list:");
				for(Shop cs : shopList) {
					if(event.getArg(2).isEmpty() || cs.getUserName().equalsIgnoreCase(event.getArg(2))) {
						sender.sendSilent(" " + cs);
					}
				}
			} else if(event.getArg(1).equalsIgnoreCase("gui")) {
				mc.displayGuiScreen(this);
			} else {
				sender.sendSilent(ChatColor.RED + "'" + event.getArg(1)
						+ "' is not a valid shop command.");
			}
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
