package d4rk.mc.playerai;

public class QuarryException extends RuntimeException {

	public QuarryException() {
	}

	public QuarryException(String message) {
		super(message);
	}

	public QuarryException(Throwable cause) {
		super(cause);
	}

	public QuarryException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getMessage() {
		String ret = super.getMessage();
		return ret == null ? new String("(null)") : ret;
	}
}
