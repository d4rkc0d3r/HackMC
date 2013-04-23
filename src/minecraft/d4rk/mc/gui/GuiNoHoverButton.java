package d4rk.mc.gui;

import net.minecraft.src.GuiButton;

public class GuiNoHoverButton extends GuiButton {

	public GuiNoHoverButton(int par1, int par2, int par3, String par4Str) {
		super(par1, par2, par3, par4Str);
	}

	public GuiNoHoverButton(int par1, int par2, int par3, int par4, int par5,
			String par6Str) {
		super(par1, par2, par3, par4, par5, par6Str);
	}

	@Override
    protected int getHoverState(boolean par1) {
		return (!this.enabled) ? 0 : 1;
    }
}
