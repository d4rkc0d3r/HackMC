package d4rk.mc.event;

import d4rk.mc.PlayerString;

public class GlobalMessageEvent extends DisableEvent {
	public PlayerString sender;
	public String message;

	public GlobalMessageEvent(PlayerString sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	public GlobalMessageEvent(GlobalMessageEvent e) {
		sender = e.sender;
		message = e.message;
	}

	@Override
	public String toString() {
		return sender + ": " + message;
	}
}
