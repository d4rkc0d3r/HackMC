package d4rk.mc.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;

public class OverlayManager {
	private List<BasicGuiOverlay>[] overlays = new List[3]; 
	private Minecraft mc = null;
	
	public OverlayManager() {
		instance = this;
		overlays[0] = new ArrayList();
		overlays[1] = new ArrayList();
		overlays[2] = new ArrayList();
		mc = Minecraft.getMinecraft();
	}
	
	public void addOverlay(BasicGuiOverlay ov, int index) {
		removeOverlay(ov.getName());
		overlays[index].add(ov);
	}
	
	public void removeOverlay(String name) {
		for(int i = 0; i < 3; ++i) {
			List<BasicGuiOverlay> list = overlays[i];
			for(int j = 0; j < list.size(); ++j) {
				if(list.get(j).getName().equals(name)) {
					list.remove(j--);
				}
			}
		}
	}
	
	public void showOverlay(String name) {
		for(int i = 0; i < 3; ++i) {
			hideOverlay(i);
			for(BasicGuiOverlay ov : overlays[i]) {
				if(ov.getName().equals(name)) {
					ov.setVisible(true);
				}
			}
		}
	}
	
	public void hideOverlay(String name) {
		for(int i = 0; i < 3; ++i) {
			for(BasicGuiOverlay ov : overlays[i]) {
				if(ov.getName().equals(name)) {
					ov.setVisible(false);
				}
			}
		}
	}
	
	public void hideOverlay(int index) {
		for(BasicGuiOverlay ov : overlays[index]) {
			if(!ov.isPersistent()) {
				ov.setVisible(false);
			}
		}
	}
	
	public void nextOverlay(int index) {
		List<BasicGuiOverlay> list = overlays[index];
		int curSel = -1;
		for(int i = 0; i < list.size(); ++i) {
			BasicGuiOverlay ov = list.get(i);
			if(!ov.isPersistent()) {
				if(ov.isVisible() && curSel == -1) {
					curSel = i;
				}
				ov.setVisible(false);
			}
		}
		if(curSel == -1) {
			for(BasicGuiOverlay ov : overlays[index]) {
				if(!ov.isPersistent()) {
					ov.setVisible(true);
					return;
				}
			}
		} else {
			for(int i = curSel; i < list.size(); ++i) {
				BasicGuiOverlay ov = list.get(i);
				if(!ov.isPersistent()) {
					ov.setVisible(true);
					return;
				}
			}
		}
	}
	
	public void draw(int width, int height) {
        mc.mcProfiler.startSection("overlay");
		glPushMatrix();
		
		int yValue = 0;
		if(!mc.gameSettings.showDebugInfo) {
			for(BasicGuiOverlay ov : overlays[LEFT_TOP]) {
				if(!ov.isVisible()) {
					continue;
				}
				glTranslated(0, yValue, 0);
				ov.draw();
				glTranslated(0, -yValue, 0);
				yValue += ov.getHeight();
			}
			
			yValue = 0;
			for(BasicGuiOverlay ov : overlays[RIGHT_TOP]) {
				if(!ov.isVisible()) {
					continue;
				}
				glTranslated(width - ov.getWidth(), yValue, 0);
				ov.draw();
				glTranslated(ov.getWidth() - width, -yValue, 0);
				yValue += ov.getHeight();
			}
		}

		if(!mc.gameSettings.showDebugProfilerChart) {
			yValue = height;
			for(BasicGuiOverlay ov : overlays[RIGHT_BOTTOM]) {
				if(!ov.isVisible()) {
					continue;
				}
				glTranslated(width - ov.getWidth(), yValue - ov.getHeight(), 0);
				ov.draw();
				glTranslated(ov.getWidth() - width, ov.getHeight() - yValue, 0);
				yValue -= ov.getHeight();
			}
		}
		
		glPopMatrix();
        mc.mcProfiler.endSection();
	}
	
	private static OverlayManager instance = null;
	
	public static OverlayManager getInstance() {
		return (instance == null) ? new OverlayManager() : instance;
	}
	
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
}
