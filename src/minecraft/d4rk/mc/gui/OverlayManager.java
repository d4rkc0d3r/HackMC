package d4rk.mc.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import d4rk.mc.ChatColor;
import d4rk.mc.Permission;
import d4rk.mc.PlayerString;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.gui.overlay.OreLevelIndicator;
import d4rk.mc.inventory.FillChest;

import net.minecraft.src.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;

public class OverlayManager extends BasicGuiScreen implements EventListener {
	private List<BasicGuiOverlay>[] overlays = new List[3]; 
	private Minecraft mc = null;
	private List<GuiButton> textFieldList = new ArrayList();
	
	public OverlayManager() {
		instance = this;
		overlays[0] = new ArrayList();
		overlays[1] = new ArrayList();
		overlays[2] = new ArrayList();
		mc = Minecraft.getMinecraft();
		EventManager.registerEvents(this);
		overlays[RIGHT_BOTTOM].add(new OreLevelIndicator().setVisible(true));
	}
	
	private int getListFromButtonId(int id) {
		id = (id/2);
		if(id >= overlays[0].size()) {
			if(id >= overlays[0].size() + overlays[1].size()) {
				return 2;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}
	
	private int getIndexFromButtonId(int id) {
		id = (id/2);
		if(id >= overlays[0].size()) {
			if(id >= overlays[0].size() + overlays[1].size()) {
				return id - overlays[0].size() - overlays[1].size();
			} else {
				return id - overlays[0].size();
			}
		} else {
			return id;
		}
	}
	
	@Override
	public void initGui() {
		for(int i = 0; i < (overlays[0].size() + overlays[1].size() + overlays[2].size()); ++i) {
			buttonList.add(new GuiToggleButton(i * 2, 0, i * 21, 20, 20, "V"));
			buttonList.add(new GuiButton(i * 2 + 1, 21, i * 21, 20, 20, "S"));
			textFieldList.add(new GuiNoHoverButton(-1, 0, 0, 120, 20, "NULL"));
		}
	}

	@Override
    protected void actionPerformed(GuiButton btn) {
		if (btn.enabled) {
            int list = getListFromButtonId(btn.id);
            int index = getIndexFromButtonId(btn.id);
			BasicGuiOverlay ov = overlays[list].get(index);
			if (btn.id % 2 == 0) {
				if(ov != null) {
					ov.setVisible(!ov.isVisible());
				}
			} else {
				if(index + 1 >= overlays[list].size()) {
					overlays[list].remove(index);
					overlays[(list + 1) % 3].add(0, ov);
				} else {
					overlays[list].remove(index);
					overlays[list].add(index + 1, ov);
				}
			}
		}
    }
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		
		int outline = 8;
		for (int i = 0; i < this.buttonList.size(); i++){
            GuiButton btn = (GuiButton)this.buttonList.get(i);
            int list = getListFromButtonId(i);
            int index = getIndexFromButtonId(i);
			if(overlays[list].size() > index) {
	            if(list == LEFT_TOP) {
	            	btn.xPosition = 121 + outline + ((i % 2 == 0) ? 0 : 21);
	            	btn.yPosition = outline + index * 21;
	            } else if(list == RIGHT_TOP) {
	            	btn.xPosition = width - outline - ((i % 2 == 0) ? 41 : 20);
	            	btn.yPosition = outline + index * 21;
	            } else if(list == RIGHT_BOTTOM) {
	            	btn.xPosition = width - outline - ((i % 2 == 0) ? 41 : 20);
	            	btn.yPosition = height + 1 - outline - (overlays[RIGHT_BOTTOM].size() - index) * 21;
	            }
				BasicGuiOverlay ov = overlays[list].get(index);
				if(ov != null && (i % 2 == 0)) {
					((GuiToggleButton)btn).setState(ov.isVisible());
				}
				btn.drawButton(this.mc, par1, par2);
			}
        }
		
		for (int i = 0; i < this.textFieldList.size(); ++i) {
            GuiButton btn = this.textFieldList.get(i);
			int list = getListFromButtonId(i * 2);
			int index = getIndexFromButtonId(i * 2);
			if(overlays[list].size() > index) {
				if(list == LEFT_TOP) {
	            	btn.xPosition = outline;
	            	btn.yPosition = outline + index * 21;
	            } else if(list == RIGHT_TOP) {
	            	btn.xPosition = width - outline - 162;
	            	btn.yPosition = outline + index * 21;
	            } else if(list == RIGHT_BOTTOM) {
	            	btn.xPosition = width - outline - 162;
	            	btn.yPosition = height + 1 - outline - (overlays[RIGHT_BOTTOM].size() - index) * 21;
	            }
				BasicGuiOverlay ov = overlays[list].get(index);
				btn.displayString = ChatColor.DARK_AQUA + ((ov == null) ? "null" : ov.getName());
				btn.drawButton(this.mc, par1, par2);
			}
        }
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
	
	public void onCommand(CommandEvent event) {
		if(event.getCommand().equalsIgnoreCase("overlaymgr")) {
			event.setDisabled(true);
			if(!event.getSender().hasPermission(Permission.INFO)) {
				event.getSender().sendSilent(Permission.NO_PERMISSION);
				return;
			}
			
			PlayerString sender = event.getSender();
			
			if(event.getArg(1).equalsIgnoreCase("gui")) {
				mc.displayGuiScreen(this);
			} else {
				sender.sendSilent(ChatColor.RED + "'" + event.getArg(1)
						+ "' is not a valid overlaymgr command.");
			}
		}
	}
	
	public void drawOverlays(int width, int height) {
		glPushMatrix();
		
		int yValue = 0;
		if(!mc.gameSettings.showDebugInfo) {
			for(BasicGuiOverlay ov : overlays[LEFT_TOP]) {
				if(ov == null || !ov.isVisible()) {
					continue;
				}
				glTranslated(0, yValue, 0);
				mc.mcProfiler.startSection(ov.getName());
				ov.draw();
				mc.mcProfiler.endSection();
				glTranslated(0, -yValue, 0);
				yValue += ov.getHeight();
			}
			
			yValue = 0;
			for(BasicGuiOverlay ov : overlays[RIGHT_TOP]) {
				if(ov == null || !ov.isVisible()) {
					continue;
				}
				glTranslated(width - ov.getWidth(), yValue, 0);
				mc.mcProfiler.startSection(ov.getName());
				ov.draw();
				mc.mcProfiler.endSection();
				glTranslated(ov.getWidth() - width, -yValue, 0);
				yValue += ov.getHeight();
			}
		}

		if(!mc.gameSettings.showDebugProfilerChart) {
			yValue = height;
			for(BasicGuiOverlay ov : overlays[RIGHT_BOTTOM]) {
				if(ov == null || !ov.isVisible()) {
					continue;
				}
				glTranslated(width - ov.getWidth(), yValue - ov.getHeight(), 0);
				mc.mcProfiler.startSection(ov.getName());
				ov.draw();
				mc.mcProfiler.endSection();
				glTranslated(ov.getWidth() - width, ov.getHeight() - yValue, 0);
				yValue -= ov.getHeight();
			}
		}
		
		glPopMatrix();
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
	
	private static OverlayManager instance = null;
	
	public static OverlayManager getInstance() {
		return (instance == null) ? new OverlayManager() : instance;
	}
	
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	
	public static final int LEFT = 0;
	public static final int TOP = 1;
	public static final int BOTTOM = 2;
}
