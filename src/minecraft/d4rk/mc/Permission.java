package d4rk.mc;

import java.util.HashMap;

public class Permission {
	public static HashMap<String, Integer> p = new HashMap<String, Integer>();
	public static HashMap<String, Integer> r = new HashMap<String, Integer>();

	public static void reload() {
		r.clear();
		r.put("LOCALE", LOCALE);

		p.clear();
		p.put("mir", LOCALE);
		p.put("d4rpl4y3r", ALL); // why not? :D
	}

	/**
	 * @deprecated Use the {@link PlayerString} version instead.
	 */
	static public boolean has(String player, String rank, int level) {
		int perm = DEFAULT;
		if (r.containsKey(rank))
			perm = r.get(rank);
		if (p.containsKey(player))
			perm = p.get(player);
		return level <= perm;
	}

	static public boolean has(PlayerString player, int level) {
		int perm = DEFAULT;
		if (r.containsKey(player.getRank()))
			perm = r.get(player.getRank());
		if (p.containsKey(player.getName()))
			perm = p.get(player.getName());
		return level <= perm;
	}

	static public final int NONE = 0;
	static public final int INFO = 1;
	static public final int PLAYERINFO = 10;
	static public final int REMOTE = 1000;
	static public final int LOCALE = 9999;
	static public final int ALL = Integer.MAX_VALUE;
	static public final String NO_PERMISSION = ChatColor.DARK_RED + "You don't have permission.";

	static public int DEFAULT = INFO;
}
