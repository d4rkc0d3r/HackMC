package d4rk.mc.gui;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.FontRenderer;

public class TextSelectionBox extends TextField {
	private List<String> possibleValues;

	public TextSelectionBox(FontRenderer fontRenderer, List<String> possibleValues, int xPos, int yPos, int width, int height) {
		super(fontRenderer, xPos, yPos, width, height);
		this.possibleValues = possibleValues;
	}

	public TextSelectionBox(FontRenderer fontRenderer, int xPos, int yPos, int width, int height) {
		super(fontRenderer, xPos, yPos, width, height);
		this.possibleValues = new LinkedList();
	}
	
	public void addValue(String str) {
		possibleValues.add(str);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(!possibleValues.contains(getText())) {
			int x = getPosX() + ((getEnableBackgroundDrawing()) ? 1 : 0);
			int y = getPosY() + ((getEnableBackgroundDrawing()) ? 21 : 19);
			
			drawTooltip(possibleValues, x, y);
		}
	}
}
