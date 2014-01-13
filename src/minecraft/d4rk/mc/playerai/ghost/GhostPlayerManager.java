package d4rk.mc.playerai.ghost;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet202PlayerAbilities;
import net.minecraft.src.PlayerCapabilities;
import net.minecraft.src.WorldClient;
import d4rk.mc.Permission;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PreProcessPacketEvent;
import d4rk.mc.event.PreSendPacketEvent;
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
		e.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
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
	        
			event.setDisabled(true);
		} else if((event.getPacket() instanceof Packet10Flying)) {
			Packet10Flying p = (Packet10Flying)event.getPacket();
			
	        double x = e.posX;
	        double y = e.posY;
	        double z = e.posZ;
	        float yaw = e.rotationYaw;
	        float pitch = e.rotationPitch;

	        if (p.moving) {
	            x = p.xPosition;
	            y = p.yPosition;
	            z = p.zPosition;
	        }

	        if (p.rotating) {
	            yaw = p.yaw;
	            pitch = p.pitch;
	        }

	        e.ySize = 0.0F;
	        e.motionX = e.motionY = e.motionZ = 0.0D;
	        e.setPositionAndRotation(x, y, z, yaw, pitch);
	        p.xPosition = e.posX;
	        p.yPosition = e.boundingBox.minY;
	        p.zPosition = e.posZ;
	        p.stance = e.posY;
	        mc.thePlayer.sendQueue.addToSendQueue(p);
	        
	        event.setDisabled(true);
		}
	}
	
	public void preSendPacketEvents(PreSendPacketEvent event) {
		if((event.getPacket() instanceof Packet10Flying)) {
			Packet10Flying p = (Packet10Flying)event.getPacket();
			
			//event.setDisabled(true);
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
		mc.thePlayer.setPositionAndRotation(e.posX, e.posY, e.posZ, e.rotationYaw, e.rotationPitch);
		world.removeEntity(e);
	}

	@Override
	public boolean isDestroyed() {
		return isDestroyed;
	}
}
