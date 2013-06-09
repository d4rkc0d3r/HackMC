package d4rk.mc;

import java.util.HashMap;
import java.util.regex.Pattern;

public enum ChatColor {
	BLACK('0', 0x00),
	DARK_BLUE('1', 0x1),
	DARK_GREEN('2', 0x2),
    DARK_AQUA('3', 0x3),
	DARK_RED('4', 0x4),
	DARK_PURPLE('5', 0x5),
	GOLD('6', 0x6),
	GRAY('7', 0x7),
	DARK_GRAY('8', 0x8),
	BLUE('9', 0x9),
	GREEN('a', 0xA),
	AQUA('b', 0xB),
	RED('c', 0xC),
	LIGHT_PURPLE('d', 0xD),
	YELLOW('e', 0xE),
	WHITE('f', 0xF),
	MAGIC('k', 0x10, true),
	BOLD('l', 0x11, true),
	STRIKETHROUGH('m', 0x12, true),
	UNDERLINE('n', 0x13, true),
	ITALIC('o', 0x14, true),
	RESET('r', 0x15);
	
	public static final char COLOR_CHAR = '\u00A7';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");
	
	private final int intCode;
	private final char code;
	private final boolean isFormat;
	private final String toString;
	private final static HashMap<Integer, ChatColor> BY_ID = new HashMap<Integer, ChatColor>();
	private final static HashMap<Character, ChatColor> BY_CHAR = new HashMap<Character, ChatColor>();
	
	private ChatColor(char code, int intCode) {
		this(code, intCode, false);
	}

	private ChatColor(char code, int intCode, boolean isFormat) {
		this.code = code;
		this.intCode = intCode;
	    this.isFormat = isFormat;
		this.toString = new String(new char[] {COLOR_CHAR, code});
	}

	public char getChar() {
		return code;
	}
	
	@Override
	public String toString() {
		return toString;
	}

	public boolean isFormat() {
		return isFormat;
	}

	public boolean isColor() {
		return !isFormat && this != RESET;
	}
	
	public static String formatSubstring(final String str, final String sub, final ChatColor format) {
		return formatSubstring(str, sub, RESET + format.toString);
	}
	
	public static String formatSubstring(final String str, final String sub, final String format) {
		StringBuilder sb = new StringBuilder();
		formatSubstringRecursiv(sb, str, sub, format, false);
		return sb.toString();
	}
	
	public static String formatSubstringIgnoreCase(final String str, final String sub, final ChatColor format) {
		return formatSubstringIgnoreCase(str, sub, RESET + format.toString);
	}
	
	public static String formatSubstringIgnoreCase(final String str, final String sub, final String format) {
		StringBuilder sb = new StringBuilder();
		formatSubstringRecursiv(sb, str, sub, format, true);
		return sb.toString();
	}
	
	private static void formatSubstringRecursiv(StringBuilder sb, final String str, final String sub, final String format, boolean ignoreCase) {
		int index = (ignoreCase) ? str.toLowerCase().indexOf(sub.toLowerCase()) : str.indexOf(sub);
		if(index == -1 || sub.isEmpty()) {
			sb.append(str);
			return;
		}
		String pre = str.substring(0, index);
		sb.append(pre);
		sb.append(format);
		sb.append((ignoreCase) ? str.substring(index, index + sub.length()) : sub);
		if(index + sub.length() < str.length()) {
			sb.append(getColorAndFormatting(pre));
			formatSubstringRecursiv(sb, str.substring(index + sub.length()), sub, format, ignoreCase);
		}
	}
	
	public static String getColorAndFormatting(final String str) {
		StringBuilder sb = new StringBuilder();
		ChatColor color = RESET;
		for(int i = 0; i < str.length() - 1; i++) {
			if(str.charAt(i) == COLOR_CHAR) {
				ChatColor current = BY_CHAR.get(str.charAt(i + 1));
				if(current == null || current == MAGIC) {
					continue;
				} else if(current == RESET) {
					sb = new StringBuilder();
					color = RESET;
				} else if(current.isFormat()) {
					sb.append(current);
				} else {
					color = current;
				}
			}
		}
		sb.insert(0, color);
		return sb.toString();
	}
	
	/**
	 * Synonym for {@link #stripColor(String)}.<br>
	 * Removes all color and formatting codes in the specified String and
	 * returns it as result.
	 */
	public static String remove(final String str) {
		return (str == null) ? null : STRIP_COLOR_PATTERN.matcher(str).replaceAll("");
	}
	
	/**
	 * Removes all color and formatting codes in the specified String and
	 * returns it as result.
	 */
	public static String stripColor(final String str) {
		return (str == null) ? null : STRIP_COLOR_PATTERN.matcher(str).replaceAll("");
	}
	
	static {
		for (ChatColor color : values()) {
			BY_ID.put(color.intCode, color);
			BY_CHAR.put(color.code, color);
		}
	}
}
