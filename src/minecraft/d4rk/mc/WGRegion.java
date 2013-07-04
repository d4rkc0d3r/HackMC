package d4rk.mc;

import java.util.ArrayList;

public class WGRegion {
	public String name;
	public String type = "cuboid";
	public Integer priority = 0;

	public ArrayList<String> owners = new ArrayList<String>();
	public ArrayList<String> coowners = new ArrayList<String>();
	public ArrayList<String> members = new ArrayList<String>();

	public Integer lowX;
	public Integer lowY;
	public Integer lowZ;

	public Integer highX;
	public Integer highY;
	public Integer highZ;

	public WGRegion() {

	}

	public boolean parseLine(String line) {
		try {
			line = ChatColor.remove(line);
			if (line.startsWith("Region: ")) {
				String[] split = line.substring(8).split(", ");
				name = split[0];
				type = split[1].substring(6);
				priority = Integer.valueOf(split[2].substring(10));
				return true;
			} else if (line.startsWith("Owners:")) {
				String[] split = line.substring(8).split(", ");
				for(String s : split) {
					owners.add(s);
				}
				return true;
			} else if (line.startsWith("CoOwners:")) {
				String[] split = line.substring(10).split(", ");
				for(String s : split) {
					coowners.add(s);
				}
				return true;
			} else if (line.startsWith("Members: ")) {
				String[] split = line.substring(9).split(", ");
				for(String s : split) {
					members.add(s);
				}
				return true;
			} else if (line.startsWith("Bounds: ")) {
				String[] split = line.substring(8).replaceAll("\\(| |\\)", "").split(",");
				lowX = Integer.valueOf(split[0]);
				lowY = Integer.valueOf(split[1]);
				lowZ = Integer.valueOf(split[2]);
				highX = Integer.valueOf(split[3]);
				highY = Integer.valueOf(split[4]);
				highZ = Integer.valueOf(split[5]);
				return true;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	public boolean isOk() {
		return name != null && lowX != null && highZ != null;
	}
	
	public String getBounds() {
		return (!isOk()) ? "(?x?x?)" : 
			"(" + (highX - lowX + 1) + "x" + (highY - lowY + 1) + "x"
			+ (highZ - lowZ + 1) + ")";
	}
}
