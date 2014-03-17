package d4rk.mc.playerai.ghost;

import java.lang.reflect.Field;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.Packet13PlayerLookMove;
import net.minecraft.src.Packet202PlayerAbilities;
import net.minecraft.src.Packet28EntityVelocity;
import net.minecraft.src.Packet29DestroyEntity;
import net.minecraft.src.Packet30Entity;
import net.minecraft.src.Packet31RelEntityMove;
import net.minecraft.src.Packet32EntityLook;
import net.minecraft.src.Packet33RelEntityMoveLook;
import net.minecraft.src.Packet34EntityTeleport;
import net.minecraft.src.Packet35EntityHeadRotation;
import net.minecraft.src.Packet38EntityStatus;
import net.minecraft.src.Packet44UpdateAttributes;
import net.minecraft.src.Packet8UpdateHealth;
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
		} else if((event.getPacket() instanceof Packet33RelEntityMoveLook)) {
			Packet33RelEntityMoveLook p = (Packet33RelEntityMoveLook)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet35EntityHeadRotation)) {
			Packet35EntityHeadRotation p = (Packet35EntityHeadRotation)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet28EntityVelocity)) {
			Packet28EntityVelocity p = (Packet28EntityVelocity)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet31RelEntityMove)) {
			Packet31RelEntityMove p = (Packet31RelEntityMove)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet32EntityLook)) {
			Packet32EntityLook p = (Packet32EntityLook)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet34EntityTeleport)) {
			Packet34EntityTeleport p = (Packet34EntityTeleport)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet38EntityStatus)) {
			Packet38EntityStatus p = (Packet38EntityStatus)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet30Entity)) {
			Packet30Entity p = (Packet30Entity)event.getPacket();
			
			if(p.entityId == mc.thePlayer.entityId) {
				p.entityId = e.entityId;
			}
		} else if((event.getPacket() instanceof Packet8UpdateHealth)) {
			Packet8UpdateHealth p = (Packet8UpdateHealth)event.getPacket();
			
			e.setEntityHealth(p.healthMP);
	        e.getFoodStats().setFoodLevel(p.food);
	        e.getFoodStats().setFoodSaturationLevel(p.foodSaturation);
		} else if((event.getPacket() instanceof Packet44UpdateAttributes)) {
			Packet44UpdateAttributes p = (Packet44UpdateAttributes)event.getPacket();
			
			// complicated way to do p.field_111005_a = e.entityId
			// because field_111005_a is a private member...
			if(p.func_111002_d() == mc.thePlayer.entityId) {
				for(Field f : p.getClass().getDeclaredFields()) {
					if(f.getType().equals(Integer.class)) {
						f.setAccessible(true);
						try {
							f.setInt(p, e.entityId);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						f.setAccessible(false);
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void preSendPacketEvents(PreSendPacketEvent event) {
		//if(true)return;
		if(false)return;
		if((event.getPacket() instanceof Packet11PlayerPosition)) {
			Packet11PlayerPosition p = (Packet11PlayerPosition)event.getPacket();

			p.onGround = e.onGround;
			p.xPosition = e.posX;
			p.zPosition = e.posZ;
			p.yPosition = e.boundingBox.minY;
			p.stance = e.posY;
		} else if((event.getPacket() instanceof Packet12PlayerLook)) {
			Packet12PlayerLook p = (Packet12PlayerLook)event.getPacket();

			p.onGround = e.onGround;
			p.pitch = e.rotationPitch;
			p.yaw = e.rotationYaw;
		} else if((event.getPacket() instanceof Packet13PlayerLookMove)) {
			Packet13PlayerLookMove p = (Packet13PlayerLookMove)event.getPacket();

			p.onGround = e.onGround;
			p.pitch = e.rotationPitch;
			p.yaw = e.rotationYaw;
			p.xPosition = e.posX;
			p.zPosition = e.posZ;
			p.yPosition = e.boundingBox.minY;
			p.stance = e.posY;
		} else if((event.getPacket() instanceof Packet10Flying)) {
			Packet10Flying p = (Packet10Flying)event.getPacket();
			
			p.onGround = e.onGround;
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
