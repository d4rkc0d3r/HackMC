package d4rk.mc.event.listener;

import java.util.ArrayList;
import java.util.HashSet;
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
import net.minecraft.src.RenderHelper;
import d4rk.mc.BlockWrapper;
import d4rk.mc.ChatColor;
import d4rk.mc.ChestShop;
import d4rk.mc.Permission;
import d4rk.mc.PlayerString;
import d4rk.mc.Shop;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PostProcessPacketEvent;
import d4rk.mc.util.Pair;

public class ShopAnalyzer extends GuiScreen implements EventListener {
	private HashSet<Shop> shopList = new HashSet();
	private GuiTextField searchField;
	private GuiTextField modeField;
	private int guiLeft;
	private int guiTop;
	private boolean isGuiOpen = false;
	private String modeText = "name";
	private String itemNameText = "";
	private List<String> possibleModes = new ArrayList();
	
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
		buttonList.clear();
		this.searchField = new GuiTextField(this.fontRenderer, width / 2 - 160, 20, 80, this.fontRenderer.FONT_HEIGHT);
        this.searchField.setMaxStringLength(20);
        this.searchField.setEnableBackgroundDrawing(true);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setText(itemNameText);
        
		this.modeField = new GuiTextField(this.fontRenderer, width / 2 - 160, 50, 80, this.fontRenderer.FONT_HEIGHT);
        this.modeField.setMaxStringLength(20);
        this.modeField.setEnableBackgroundDrawing(true);
        this.modeField.setVisible(true);
        this.modeField.setTextColor(16777215);
        this.modeField.setText(modeText);
        
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
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch(par1GuiButton.id) {
		default:
			break;
		}
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		searchField.mouseClicked(par1, par2, par3);
		modeField.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) {
		if(par2 == 15) { // tab key was pressed
			if(searchField.isFocused()) {
				if(itemNameResultSet.size() > 0) {
					searchField.setText(itemNameResultSet.iterator().next());
				}
			} else if(modeField.isFocused()) {
				searchField.setFocused(true);
				modeField.setFocused(false);
			} else {
				modeField.setFocused(true);
			}
		}
		
		if(searchField.textboxKeyTyped(par1, par2) || modeField.textboxKeyTyped(par1, par2) || par2 == 15) {
			modeText = modeField.getText();
			itemNameText = searchField.getText();
			updateResult();
		}
		super.keyTyped(par1, par2);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		
		drawString(fontRenderer, "Item Name", width / 2 - 160, 8, 16777215);
		searchField.drawTextBox();
		
		drawString(fontRenderer, "Mode", width / 2 - 160, 38, 16777215);
		modeField.drawTextBox();
		
		drawString(fontRenderer, ChatColor.YELLOW + "Shop count: " + shopList.size(), width / 2 - 160, 65, 16777215);
		
		if(!possibleModes.contains(modeField.getText().toLowerCase())) {
			drawTooltip(possibleModes, width / 2 - 159, 71);
		} else {
			if(modeField.getText().equalsIgnoreCase("name")) {
				int i = 0;
				for(String s : itemNameResultSet) {
					drawString(fontRenderer, s, width / 2 - 60 + (i % 3) * 75, 16 + (i / 3) * 12, 16777215);
					++i;
				}
			} else {
				for(int i = 0; i < viewResult.size(); ++i) {
					drawString(fontRenderer, viewResult.get(i), width / 2 - 60, 16 + i * 12, 16777215);
				}
				int i = 0;
				for(String s : itemNameResultSet) {
					drawString(fontRenderer, s, width / 2 - 160, 86 + i*12, 16777215);
					++i;
				}
			}
		}
		
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	protected void drawTooltip(List<String> list, int par2, int par3)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        List var4 = list;

        if (!var4.isEmpty())
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < var4.size(); ++var6)
            {
                var7 = this.fontRenderer.getStringWidth((String)var4.get(var6));

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = par2 + 3;
            var7 = par3 - 7;
            int var9 = 8;

            if (var4.size() > 1)
            {
                var9 += 2 + (var4.size() - 1) * 10;
            }

            if (this.guiTop + var7 + var9 + 6 > this.height)
            {
                var7 = this.height - var9 - this.guiTop - 6;
            }

            this.zLevel = 300.0F;
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (int var13 = 0; var13 < var4.size(); ++var13)
            {
                String var14 = (String)var4.get(var13);

                this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

                if (var13 == 0)
                {
                    var7 += 2;
                }

                var7 += 10;
            }

            this.zLevel = 0.0F;
        }
    }
	
	private void clearResult() {
		viewResult.clear();
	}
	
	private void updateResult() {
		itemNameResultSet.clear();
		for(Shop s : shopList) {
			if(s.getItemName().toLowerCase().contains(itemNameText.toLowerCase())) {
				itemNameResultSet.add(s.getItemName());
			}
		}
        if(modeText.equalsIgnoreCase("buy") || modeText.equalsIgnoreCase("sell")) {
			viewResult.clear();
			count = 19;
			addToResult(modeText);
		} else if(modeText.equalsIgnoreCase("check")) {
			viewResult.clear();
			count = 8;
			addToResult("buy");
			addToResult("sell");
		}
	}
	
	public void addToResult(String modeText) {
		if(modeText.equalsIgnoreCase("buy") || modeText.equalsIgnoreCase("sell")) {
			boolean isSell = modeText.equalsIgnoreCase("sell");
			boolean up = isSell;
			List<Pair<Double, Shop>> list = new LinkedList();
			
			String name = itemNameText;
			
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
				viewResult.add(String.format(" %.2f/", e.getFirst() * perCountItems)
						+ perCountItems + " at " + e.getSecond().getBlock() 
						+ " from " + e.getSecond().getUserName());
			}
		}
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
}
