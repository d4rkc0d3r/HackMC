package d4rk.mc;

public class McmmoSkill {
	private String name;
	private int level;
	private int currentExp;
	private int lvlUpExp;
	
	private McmmoSkill() {}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getCurrentExp() {
		return currentExp;
	}

	public int getLvlUpExp() {
		return lvlUpExp;
	}

	public double getExpBarValue() {
		return currentExp / (double)lvlUpExp;
	}
	
	public static McmmoSkill parseMcStats(String str) {
		if(str.startsWith("[")) {
			return null;
		}
		
		try {
			String[] split = str.split(":");
			if(split.length != 2 || !split[1].contains(" XP(")) {
				throw new Exception();
			}
			McmmoSkill s = new McmmoSkill();
			s.name = split[0];
			s.level = Integer.valueOf(split[1].split(" ", 2)[0]);
			s.currentExp = Integer.valueOf(str.substring(str.indexOf('(') + 1, str.indexOf('/')).replace(",", ""));
			s.lvlUpExp = Integer.valueOf(str.substring(str.indexOf('/') + 1, str.indexOf(')')).replace(",", ""));
			return s;
		} catch(Exception e) {
			return null;
		}
	}
	
	public static McmmoSkill parseLevelUp(String str) {
		if(str.startsWith("[")) {
			return null;
		}
		
		try {
			String[] split = str.split(" ", 2);
			if(split[1].startsWith("Skill um 1 gestiegen. Gesamt (") && !split[0].contains(":")) {
				McmmoSkill s = new McmmoSkill();
				s.name = split[0];
				s.level = Integer.valueOf(str.substring(str.indexOf('(') + 1, str.length()));
				s.currentExp = 0;
				s.lvlUpExp = s.level * 20 + 1020;
				return s;
			}
		} catch(Exception e) {}
		
		return null;
	}
	
	public McmmoSkill setValues(McmmoSkill s) {
		this.currentExp = s.currentExp;
		this.name = s.name;
		this.lvlUpExp = s.lvlUpExp;
		this.level = s.level;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof McmmoSkill) ? ((McmmoSkill)obj).name.equals(name) : false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
