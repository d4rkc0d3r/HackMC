package d4rk.mc.event;

import d4rk.mc.PlayerString;
import d4rk.mc.event.listener.secretcraft.PMParser;

public class PrivateMessageEvent extends DisableEvent {
	public String msg;
	public PlayerString sender;
	public PlayerString receiver;

	public PrivateMessageEvent(String msg, PlayerString sender, PlayerString receiver) {
		this.msg = msg;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public PrivateMessageEvent(PrivateMessageEvent e) {
		this.msg = e.msg;
		this.sender = e.sender;
		this.receiver = e.receiver;
	}
	
	@Override
	public String toString() {
		return "[" + sender + " -> " + receiver + "] " + msg;
	}
}
