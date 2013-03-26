package d4rk.mc.event.listener;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Packet14BlockDig;

import d4rk.mc.ChatColor;
import d4rk.mc.McmmoSkill;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.gui.BasicGuiScreen;

public class McmmoGui extends BasicGuiScreen implements EventListener {
	private ArrayList<McmmoSkill> skills = new ArrayList();
	private boolean isUpdatingSkills = false;
	private boolean isWaitingForSkillUpdate = false;
	private boolean needUpdate = true;
	private boolean show = false;
	private int powerLevel = 0;
	private long lastUpdate = System.currentTimeMillis();

	public McmmoGui() {
		instance = this;
		mc = Minecraft.getMinecraft();
	}
	
	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
	}
	
	public void draw(FontRenderer r) {
		if(!show) {
			return;
		}
		
		fontRenderer = r;

		drawString(ChatColor.DARK_RED + "POWER LEVEL:", 8, 8);
		drawString(ChatColor.GREEN.toString() + powerLevel, 90, 8);
		
		int i = 0;
		for(McmmoSkill skill : skills) {
			drawString(ChatColor.YELLOW + skill.getName() +":", 10 , 20 + i * 12);
			drawString(ChatColor.GREEN.toString() + skill.getLevel() , 90 , 20 + i * 12);
			drawString(ChatColor.AQUA + String.format(" %.1f%%", skill.getExpBarValue() * 100), 110 , 20 + i * 12);
			++i;
		}
	}
	
	public void onTickEvent(TickEvent event) {
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastUpdate > ((needUpdate) ? 5000 : 15000)) {
			lastUpdate = currentTime;
			updateSkills();
		}
	}
	
	public void onSendPacketEvent(PostSendPacketEvent event) {
		if(event.getPacket() instanceof Packet14BlockDig) {
			needUpdate = true;
		}
	}
	
	public void onChatEvent(ChatEvent event) {
		String str = ChatColor.remove(event.message);
		
		updateSkill(McmmoSkill.parseLevelUp(str));
		
		if(isWaitingForSkillUpdate) {
			if(str.equals("[mcMMO] Werte")) {
				isUpdatingSkills = true;
				isWaitingForSkillUpdate = false;
				event.setDisabled(true);
			}
			return;
		}
		
		if(isUpdatingSkills) {
			event.setDisabled(true);
			McmmoSkill skill = McmmoSkill.parseMcStats(str);
			if(skill != null) {
				updateSkill(skill);
			} else {
				if(str.startsWith("POWER LEVEL:") || str.startsWith("KRAFT LEVEL:")) {
					try {
						isUpdatingSkills = false;
						needUpdate = false;
						show = true;
						powerLevel = Integer.valueOf(str.split(":")[1].trim());
					} catch(Exception e) {}
				}
			}
		}
	}
	
	public void updateSkills() {
		if(!isWaitingForSkillUpdate) {
			isWaitingForSkillUpdate = true;
			mc.thePlayer.sendChatMessage("/mcstats");
		} else {
			show = false;
		}
	}
	
	public void updateSkill(McmmoSkill skill) {
		if(skill == null) {
			return;
		}
		int index = skills.indexOf(skill);
		if(index == -1) {
			skills.add(skill);
		} else {
			skills.get(index).setValues(skill);
		}
	}
	
	public static McmmoGui getInstance() {
		return instance;
	}
	
	private static McmmoGui instance;
}
