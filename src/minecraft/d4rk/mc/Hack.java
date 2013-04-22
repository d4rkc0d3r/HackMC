package d4rk.mc;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.TickEvent;
import d4rk.mc.event.listener.CommandParser;
import d4rk.mc.event.listener.secretcraft.PMParser;
import d4rk.mc.playerai.AntiAfkAI;
import d4rk.mc.playerai.BaseAI;
import d4rk.mc.playerai.Quarry;
import d4rk.mc.playerai.QuarryAI;
import d4rk.mc.playerai.QuarryException;
import d4rk.mc.playerai.script.ScriptTask;
import d4rk.mc.util.RingNumber;
import d4rk.mc.util.UniquePacket;
import d4rk.mc.util.Vec3D;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.Item;
import net.minecraft.src.ItemSign;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet9Respawn;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.TileEntitySign;

public class Hack {
	static public Hack hack = null;
	static public Minecraft mc = null;
	static public PlayerControllerMP PCMP = null;
	
	static public Options cfg = new Options();

	static public int antiAFKtime = 20 * 60 * 7; // 7 minutes
	static private int antiAFKticks = antiAFKtime;
	
	static private long lastTime = 0;
	static private long lastTick = 0;
	static public long tickDiff = 50;
	static public float tps = 20.0F;
	static public RingNumber<Float> lastTPS = new RingNumber<Float>(100);
	static public RingNumber<Long> last20Ticks = new RingNumber<Long>(20);
	
	static public boolean isAutoMining = false;
	
	static private HashMap<UniquePacket, Integer> delayedSendQueue = new HashMap<UniquePacket, Integer>();
	
	static public PlayerAI myAI = null;
	static public BaseAI antiAFK = null;
	static public BaseAI activeAI = null;
	
	static public PlayerWrapper pWrap = null;
	
	static public Quarry quarry = null;
	
	public Hack(Minecraft mc) {
		this.mc = mc;
		this.PCMP = mc.playerController;
		this.hack = this;
		Permission.reload();
		KeyMakro.load("default");
		ImproveChat.setMSGFormat("[? -> mir] ");
		ImproveChat.setSendFormat("[mir -> ?] ");
		EventManager.loadBasicListeners();
	}
	
	/**
	 * Adds p after n ticks to the send queue.
	 */
	public static void addDelayedPacket(Packet p, int n) {
		delayedSendQueue.put(new UniquePacket(p), Integer.valueOf(n));
	}
	
	/**
	 * Adds p to the send queue.
	 */
	static public void addPacket(Packet p) {
		mc.getNetHandler().addToSendQueue(p);
	}
	
	/**
	 * This is only called on the actual click, its not a block damage event.
	 * 
	 * @return true if the event should be cancelled, false otherwise.
	 */
	public static boolean onBlockLeftClick(int x, int y, int z, int side) {
		EntityPlayer p = mc.thePlayer;
		ItemStack item = p.inventory.getCurrentItem();
		BlockWrapper block = new BlockWrapper(x, y, z);
		return false;
	}

	/**
	 * @return true if the event should be cancelled, false otherwise.
	 */
	public static boolean onBlockRightClick(int x, int y, int z, int side) {
		EntityPlayer p = mc.thePlayer;
		ItemStack item = p.inventory.getCurrentItem();
		BlockWrapper block = new BlockWrapper(x, y, z);
		
		if(block.getSignLine(0).equalsIgnoreCase("[Quarry]")) {
			try {
				activeAI = new QuarryAI(getPlayerWrapper(), block);
				activeAI.start();
				addChatMessage("Initialized quarry");
			}
			catch(QuarryException e) {
				mc.thePlayer.addChatMessage(new String(e.getMessage()));
			}
			return true;
		}
		if(block.getSignLine(0).equalsIgnoreCase("[Next]")) {
			try {
				quarry.getNext().setIDandMetadata(Block.cloth.blockID, 4); // yellow wool
			} catch(NullPointerException e) {}
			return true;
		}
		if(block.getSignLine(0).equalsIgnoreCase("[SCS]")) {
			String[] commands = null;
			String[] lines = new String[] {
				block.getSignLine(1),
				block.getSignLine(2),
				block.getSignLine(3)
			};
			if(lines[0].endsWith("\\") && lines[1].endsWith("\\"))
				commands = new String[] {
					lines[0].substring(0, lines[0].length()-1)+
					lines[1].substring(0, lines[1].length()-1)+
					lines[2]
				};
			else if(lines[0].endsWith("\\"))
				commands = new String[] {
					lines[0].substring(0, lines[0].length()-1) + lines[1],
					lines[2]
				};
			else if(lines[1].endsWith("\\"))
				commands = new String[] {
					lines[0],
					lines[1].substring(0, lines[1].length()-1) + lines[2]
				};
			else commands = new String[] {lines[0], lines[1], lines[2]};
			for(String cmd : commands) if(!cmd.isEmpty()) sendChatMessage(cmd);
			return true;
		}
		if(block.getSignLine(0).startsWith("Hier wohn")) {
			Hack.sendChatMessage("/seen " + block.getSignLine(1));
			return true;
		}
		
		try {
			int dist = Integer.valueOf(block.getSignLine(2));

			if(block.getSignLine(1).equalsIgnoreCase("[Lift Down]")) {
				sendChatMessage("/tp "+p.getEntityName()+" "+(int)Math.floor(p.posX)+(" "+
			                    (int)(p.posY-dist))+" "+(int)Math.floor(p.posZ));
				return true;
			}
			if(block.getSignLine(1).equalsIgnoreCase("[Lift Up]")) {
				sendChatMessage("/tp "+p.getEntityName()+" "+(int)Math.floor(p.posX)+(" "+
	                    	    (int)(p.posY+dist))+" "+(int)Math.floor(p.posZ));
				return true;
			}
		} catch(Exception e) {}
		
		return false;
	}
	
	/**
	 * Calls the KeyMakro's
	 */
	public static void onKeyPressed(int keyCode) {
		KeyMakro.onKeyPressed(keyCode);
	}
	
	/**
	 * Calculates server-side lags.
	 */
	public static void handleUpdateTime(long time) {
		long diffServer = lastTime - time;
		long diffClient = lastTime - mc.theWorld.getWorldTime();
        mc.theWorld.setWorldTime(time);
        if (diffClient == 0 || diffServer == 0) return;
		tps = (float)(diffServer / (double)diffClient) * (20.0F * 1000.0F / (float)last20Ticks.getSum());
		lastTime = time;
		if(tps>30 || tps<0) return;
		lastTPS.add(tps);
	}
	
	/**
	 * Called when a Packet13PlayerLookMove is send to the server and prevents the anti afk move
	 */
	public static void somePlayerAction() {
		antiAFKticks = antiAFKtime;
	}
	
	public static void updatePlayerWrapper() {
		if(pWrap == null)
			pWrap = new PlayerWrapper(mc.thePlayer, mc.playerController);
		else
			pWrap.update(mc.thePlayer, mc.playerController);
	}
	
	public static PlayerWrapper getPlayerWrapper() {
		if(pWrap == null)
			pWrap = new PlayerWrapper(mc.thePlayer, mc.playerController);
		if(!pWrap.isOk())
			pWrap.update(mc.thePlayer, mc.playerController);
		return pWrap;
	}

	public static void handleRespawn(Packet9Respawn p) {
		mc = Minecraft.getMinecraft();
		PCMP = mc.playerController;
		updatePlayerWrapper();
		if(myAI != null)
			myAI.update();
	}
	
	/**
	 * Should be called every 50ms from within the Minecraft tick.
	 * It manages the anti afk feature as well as the player AI system.
	 * Also client side lag is calculated here.
	 */
	public static void onTick() {
		mc.mcProfiler.startSection("hack");
		mc.mcProfiler.startSection("event");
		EventManager.fireEvent(new TickEvent());
		mc.mcProfiler.endSection(); // end section "event"
		mc.mcProfiler.startSection("antiafk");
		if(antiAFK != null)
			antiAFK.onTick();
		if(--antiAFKticks<0) {
			EntityClientPlayerMP P = mc.thePlayer;
			antiAFK = new AntiAfkAI(getPlayerWrapper());
			logInfo("antiafk move");
			antiAFKticks = antiAFKtime;
		}
		mc.mcProfiler.endSection(); // end section "antiafk"
		mc.mcProfiler.startSection("playerai");
		if(activeAI != null)
			activeAI.onTick();
		if(myAI != null)
			myAI.onTick();
		mc.mcProfiler.endSection(); // end section "playerai"
		mc.mcProfiler.startSection("sendqueue");
		UniquePacket[] keys = delayedSendQueue.keySet().toArray(new UniquePacket[0]);
		for(int i=0;i<keys.length;i++) {
			UniquePacket p = keys[i];
			if(p==null) continue;
			int ticks = delayedSendQueue.get(p)-1;
			if(ticks <= 0) {
				mc.getNetHandler().addToSendQueue(p.getPacket());
				delayedSendQueue.remove(p);
			}
			else
				delayedSendQueue.put(p, new Integer(ticks));
		}
		mc.mcProfiler.endSection();// end section "sendqueue"
		mc.mcProfiler.startSection("lagcalc");
		tickDiff = mc.getSystemTime() - lastTick;
		lastTick = mc.getSystemTime();
		last20Ticks.add(tickDiff);
		mc.mcProfiler.endSection();// end section "lagcalc"
		mc.mcProfiler.endSection(); // end section "hack"
	}
	
	static public void toggleFocusNearestMob(EntityPlayer player, double rad) {
		if(myAI == null)
			myAI = new PlayerAIMobHunter(mc.thePlayer,new Vec3D(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ),rad);
		myAI.toggle();
	}
	
	static public String getHackDir() {
		return getMCDir() + "/hack";
	}
	
	static public String getMCDir() {
		return mc.getMinecraftDir().getPath();
	}
	
	static public String getPlayerName() {
		try {
			return mc.session.username;
		} catch(NullPointerException e) {
			return "null";
		}
	}
	
	static public String getCurrentDateAndTime() {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-hh:mm:ss");
	    return sdf.format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Logs the String given as parameter to STDOUT.<br>
	 * If a prefix like "[prefix] " exists, then it will also be saved to a file
	 * (prefix.log) with the current time stamp.
	 */
	static public void log(String str) {
		System.out.println(str);
		logToFile(str);
	}
	
	/**
	 * Does the same as log(str); just without putting str on STDOUT.
	 */
	static public void logToFile(String str) {
		if(str.startsWith("[") && str.indexOf("] ")!=-1) {
			try {
				String type = str.substring(1, str.indexOf("] ")).toLowerCase();
				String content = str.substring(str.indexOf("] ")+2);
				Writer output = new BufferedWriter(new FileWriter(getHackDir()+"/log/"+type+".log", true));
				output.append(getCurrentDateAndTime()+" "+content+"\r\n");
				output.close();
			} catch (IOException e) {}
		}
	}
	
	static public void logInfo(String str) {
		log("[Info] " + str);
	}
	
	static public void logWarning(String str) {
		System.err.println(str);
		logToFile("[Warning] " + str);
	}
	
	static public void logSevere(String str) {
		System.err.println(str);
		logToFile("[Severe] " + str);
	}
	
	/**
	 * Adds a String to the local chat GUI.
	 * 
	 * @deprecated Use {@link ImproveChat#addToChatGui(String)} instead.
	 */
	static public void addChatMessage(String str) {
		if(mc.thePlayer != null && str != null)
			mc.thePlayer.addChatMessage(str);
	}
	
	/**
	 * This will send the specified String to the server.
	 */
	static public void sendChatMessage(String str) {
		if(mc.thePlayer != null)
			mc.thePlayer.sendChatMessage(str);
	}
}
