package d4rk.mc.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Packet101CloseWindow;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;

public class CloseWindow extends Operation {
	public CloseWindow(PlayerWrapper pWrap) {
		super(pWrap, 0);
	}

	@Override
	public boolean canDoOperation(int currentInventoryType) {
		return Minecraft.getMinecraft().currentScreen != null;
	}

	@Override
	public void doOperation() {
		try {
			Minecraft.getMinecraft().setIngameFocus();
			Minecraft.getMinecraft().getNetHandler().addToSendQueue(
					new Packet101CloseWindow(pWrap.player.openContainer.windowId));
		} catch(Exception e) {
			e.printStackTrace();
		}
		done();
	}
}
