package d4rk.mc.event.listener;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet;

import d4rk.mc.Permission;
import d4rk.mc.event.CommandEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PostProcessPacketEvent;
import d4rk.mc.event.PostSendPacketEvent;

public class PacketDisplay implements EventListener {
	private boolean isActive = false;
	private boolean displaySpecificPacketFields = true;
	private boolean displayBasePacketFields = false;
	private boolean displaySendedPackets = true;
	private boolean displayProcessedPackets = false;
	
	private boolean isWhitelist = false;
	private HashSet<Integer> blacklist = new HashSet<Integer>();
	
	private HashSet<String> basePacketFieldNames = new HashSet<String>();

	public PacketDisplay() {
		Class<?> basePacket = Packet.class;
		for(Field f : Packet.class.getDeclaredFields()) {
			basePacketFieldNames.add(f.getName());
		}

		blacklist.add(0);
		blacklist.add(10);
		blacklist.add(11);
		blacklist.add(12);
		blacklist.add(13);
		blacklist.add(18);
	}
	
	public void postProcessPacketEvent(PostProcessPacketEvent event) {
		if(displayProcessedPackets) {
			displayPacket(event.getPacket(), "Processed ");
		}
	}
	
	public void postSendPacketEvent(PostSendPacketEvent event) {
		if(displaySendedPackets) {
			displayPacket(event.getPacket(), "Sended ");
		}
	}
	
	private void displayPacket(Packet p, String prefix) {
		if(!isActive || isWhitelist ^ blacklist.contains(p.getPacketId())) {
			return;
		}
		LinkedList<String> display = new LinkedList<String>();
		display.add(prefix + p.getClass().getSimpleName());
		
		if(displaySpecificPacketFields) {
			for(Field f : p.getClass().getDeclaredFields()) {
				if(!displayBasePacketFields && basePacketFieldNames.contains(f.getName())) {
					continue;
				}
				try {
					f.setAccessible(true);
					display.add(" " + f.getName() + ": " + f.get(p));
					f.setAccessible(false);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		for(String s : display) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(s);
		}
	}
	
	public void onCommand(CommandEvent event) {
		if(!event.getSender().hasPermission(Permission.LOCALE) || !event.getCommand().equalsIgnoreCase("packet")) {
			return;
		}
		
		if(event.getArg(1).equalsIgnoreCase("blacklist")) {
			
		} else if(event.getArg(1).equalsIgnoreCase("activate")) {
			isActive = true;
		} else if(event.getArg(1).equalsIgnoreCase("deactivate")) {
			isActive = false;
		} else {
			
		}
		
		event.setDisabled(true);
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}

}
