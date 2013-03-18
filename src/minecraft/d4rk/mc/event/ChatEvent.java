package d4rk.mc.event;

/**
 * This is the event for an ingoing chat message.
 */
public class ChatEvent extends DisableEvent {
	protected String srcMessage = null;
	public String message = null;
	
	/**
	 * This is the event for an ingoing chat message.
	 */
	public ChatEvent(String message) {
		this.srcMessage = message == null ? "" : message;
		this.message = srcMessage;
	}
	
	/**
	 * @return the unmodified message that caused the event.
	 */
	public String getSrc() {
		return srcMessage;
	}
}
