package d4rk.mc.playerai.ghost;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet202PlayerAbilities;
import net.minecraft.src.PlayerCapabilities;
import net.minecraft.src.WorldClient;
import d4rk.mc.Permission;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PreProcessPacketEvent;
import d4rk.mc.util.Vec3D;

public class GhostPlayerManager implements EventListener {
	private boolean isDestroyed = false;
	private WorldClient world;
	private EntityGhostPlayer e;
	private Minecraft mc;
	private int playerEntityID;

	public GhostPlayerManager() {
		mc = Minecraft.getMinecraft();
		this.world = mc.theWorld;
		e = new EntityGhostPlayer(world);
		e.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY - 1.6, mc.thePlayer.posZ,
				mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
		e.rotationYawHead = mc.thePlayer.rotationYawHead;
		world.addEntityToWorld(EntityGhostPlayer.ENTITY_ID, e);
		playerEntityID = mc.thePlayer.entityId;
		EventManager.registerEvents(this);
	}
	
	public void onPreProcessPacketEvent(PreProcessPacketEvent event) {
		if((event.getPacket() instanceof Packet202PlayerAbilities)) {
			Packet202PlayerAbilities packet = (Packet202PlayerAbilities)event.getPacket();
			
			e.capabilities.isFlying = packet.getFlying();
			e.capabilities.isCreativeMode = packet.isCreativeMode();
			e.capabilities.disableDamage = packet.getDisableDamage();
			e.capabilities.allowFlying = packet.getAllowFlying();
			e.capabilities.setFlySpeed(packet.getFlySpeed());
			e.capabilities.setPlayerWalkSpeed(packet.getWalkSpeed());
			
			mc.thePlayer.addChatMessage("Capabilities Packet");
	        
			event.setDisabled(true);
		}
	}
	
	public void onCommand(CommandEvent event) {
		if(!event.getCommand().equalsIgnoreCase("nofake") || !event.getSender().hasPermission(Permission.LOCALE)) {
			return;
		}
		destroy();
		event.setDisabled(true);
	}
	
	public void destroy() {
		this.isDestroyed = true;
		mc.thePlayer.capabilities = e.capabilities;
		mc.thePlayer.setPositionAndRotation(e.posX, e.posY + 1.62, e.posZ, e.rotationYaw, e.rotationPitch);
		world.removeEntity(e);
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
