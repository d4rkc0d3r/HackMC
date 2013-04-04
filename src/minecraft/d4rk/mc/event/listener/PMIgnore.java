package d4rk.mc.event.listener;

import java.util.ArrayList;
import java.util.List;

import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.Permission;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PrivateMessageEvent;

public class PMIgnore implements EventListener {
	private List<String> ignoOnce = new ArrayList<String>();
	private boolean ignoAll = false;
	private boolean sendIgnoMessage = true;

	public PMIgnore() {
		lastInstance = this;
	}
	
	public void onCommand(CommandEvent event) {
		if(event.getCommand().equals("tigno")) {
			if(event.getArg(1).equals("pm")) {
				if(event.getArg(2).equals("all")) {
					event.setDisabled(true);
					if(!event.getSender().hasPermission(Permission.LOCALE)) {
						event.getSender().sendSilent("§cYou don't have permission.");
						return;
					}
					ignoAll = !ignoAll;
					event.getSender().sendSilent(ChatColor.DARK_AQUA + "Ignore all pm: " + ignoAll);
				}
			}
		}
	}

	public void onPM(PrivateMessageEvent e) {
		if(ignoAll) {
			if(sendIgnoMessage) {
				e.sender.sendSilent("Bitte schreibe mir im Forum oder per /mail send.");
			}
			e.setDisabled(true);
			return;
		}
		
		int index = ignoOnce.indexOf(e.msg);
		if (index != -1) {
			e.setDisabled(true);
			ignoOnce.remove(index);
		}
	}

	/**
	 * If you receive a PM and the message is the same as the specified msg,
	 * then it won't be displayed in the chat, but only once.
	 * 
	 * @param msg
	 * @throws NullPointerException
	 *             if the constructor was never called before.
	 */
	public static void addOnce(String msg) {
		lastInstance.ignoOnce.add(msg);
	}

	private static PMIgnore lastInstance = null;

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
