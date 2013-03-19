package d4rk.mc.event;

/**
 * This is the event for an outgoing chat message.
 */
public class PlayerChatEvent extends DisableEvent
{
    protected String srcMessage = null;
    public String message = null;

    /**
     * This is the event for an outgoing chat message.
     */
    public PlayerChatEvent(String message)
    {
        this.srcMessage = message == null ? "" : message;
        this.message = srcMessage;
    }

    /**
     * The original unmodified message.
     */
    public String getSrc()
    {
        return srcMessage;
    }
}
