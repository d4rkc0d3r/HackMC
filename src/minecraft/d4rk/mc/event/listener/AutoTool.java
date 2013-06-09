package d4rk.mc.event.listener;

import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet14BlockDig;
import d4rk.mc.BlockWrapper;
import d4rk.mc.Hack;
import d4rk.mc.Permission;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.event.TickEvent;
import d4rk.mc.inventory.ItemCompare;
import d4rk.mc.inventory.SelectBestToolForBlock;

public class AutoTool implements EventListener {
	private ItemStack lastSelection = null;
	private int ticksToRevert = -1;
	private boolean isPaused = false;
	private boolean isMining = false;

	public AutoTool() {
		isPaused = !Hack.cfg.getBoolean("autotool");
	}
	
	public void onTick(TickEvent event) {
		if(!isMining) {
			if(ticksToRevert > 0) {
				--ticksToRevert;
			} else if(ticksToRevert == 0) {
				if(!ItemCompare.equals(lastSelection, Hack.getPlayerWrapper().getCurrentItem())
						&& lastSelection != null) {
					Hack.getPlayerWrapper().selectItem(lastSelection.getItem());
				}
				ticksToRevert = -1;
			}
		}
	}
	
	public void onCommand(CommandEvent event) {
		if(!event.getCommand().equalsIgnoreCase("autotool")) {
			return;
		}
		
		if(!event.getSender().hasPermission(Permission.LOCALE)) {
			event.getSender().sendSilent(Permission.NO_PERMISSION);
		}
		
		isPaused = !isPaused;
		
		event.getSender().sendSilent("AutoTool is now " + ((isPaused) ? "off" : "on"));
		
		event.setDisabled(true);
	}
	
	public void onPostSendPacketEvent(PostSendPacketEvent event) {
		if(!(event.getPacket() instanceof Packet14BlockDig) || isPaused) {
			return;
		}
		Packet14BlockDig p = (Packet14BlockDig) event.getPacket();
		if(p.status > 0) {
			isMining = false;
			return;
		}
		if(ticksToRevert == -1) {
			lastSelection = Hack.getPlayerWrapper().getCurrentItem();
		}
		isMining = true;
		ticksToRevert = 10;
		Hack.getPlayerWrapper().selectToolForBlock(new BlockWrapper(p.xPosition, p.yPosition, p.zPosition));
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
