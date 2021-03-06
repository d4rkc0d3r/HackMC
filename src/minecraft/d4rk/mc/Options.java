package d4rk.mc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import net.minecraft.src.GuiNewChat;
import net.minecraft.src.TileEntitySignRenderer;

import org.lwjgl.input.Keyboard;

public class Options extends Config {
	public boolean waterCanPushMe = true;
	public int blockHitDelayCreative = 5;
	public float blockReachDistCreative = 5.0F;
	public float blockReachDistSurvival = 4.5F;
	public double focusNearestMobMaxDist = 100.0F;
	public int maxChatLines = 200;
	public String nextName = Hack.getHackDir() + "/config/hack.cfg";
	public int signRenderDistance = 512;

	public Options() {
		name = Hack.getHackDir() + "/config/hack.cfg";
	}

	public Options(String fileName) {
		super(fileName);
	}

	public void load() {
		load(nextName);
	}

	public void load(String fileName) {
		super.load(fileName);

		maxChatLines = getInteger("maxChatLines");
		waterCanPushMe = getBoolean("waterCanPushMe");
		blockHitDelayCreative = getInteger("blockHitDelayCreative");
		blockReachDistCreative = getFloat("blockReachDistCreative");
		blockReachDistSurvival = getFloat("blockReachDistSurvival");
		focusNearestMobMaxDist = getDouble("focusNearestMobMaxDist");
		nextName = Hack.getHackDir() + "/config/" + getString("configName");
		signRenderDistance = getInteger("signRenderDistance");

		GuiNewChat.maxChatLines = maxChatLines;
		TileEntitySignRenderer.setSignRenderDistance(signRenderDistance);

		String keyBind = getString("loadKeyBindings");
		if (!keyBind.isEmpty())
			KeyMakro.load(keyBind);

		ImproveChat.addToChatGui("Loaded " + name.substring(name.lastIndexOf('/') + 1));
	}
}
