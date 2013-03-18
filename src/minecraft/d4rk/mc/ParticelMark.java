package d4rk.mc;

import d4rk.mc.util.Vec3D;
import net.minecraft.client.Minecraft;
import net.minecraft.src.World;

public class ParticelMark {
	public Vec3D from;
	public Vec3D to;
	public String style;
	
	private Minecraft mc;
	
	public ParticelMark(Vec3D from, Vec3D to) {
		this(from, to, "lineto");
	}
	
	public ParticelMark(Vec3D from, Vec3D to, String style) {
		this.from = from;
		this.to = to;
		this.style = style.toLowerCase();
		this.mc = Minecraft.getMinecraft();
	}

	public void onTick() {
		if(style.equals("lineto")) {
			mc.theWorld.spawnParticle("smoke", to.x, to.y, to.z, 0, 0, 0);
			mc.theWorld.spawnParticle("smoke", to.x, to.y + 0.1, to.z, 0, 0, 0);
			Vec3D dir = new Vec3D(to).sub(new Vec3D(from));
			double dist = dir.getLen();
			for(int i = 0; i < 5; i++) {
				Vec3D pos = new Vec3D(from).add(dir.setLen(Math.min(1 + Math.random() * 9, dist)));
				
				dir.setLen(0.1 * (Math.min(pos.dist(to)/ 10, 1)));
				mc.theWorld.spawnParticle("flame", pos.x, pos.y, pos.z, dir.x, dir.y, dir.z);
			}
		}
	}
}
