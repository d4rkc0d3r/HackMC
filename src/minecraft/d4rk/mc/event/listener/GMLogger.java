package d4rk.mc.event.listener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.PlayerString;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.GlobalMessageEvent;

public class GMLogger implements EventListener {
	private boolean show = false;

	public GMLogger() {
		lastInstance = this;
	}

	public void onGM(GlobalMessageEvent e) {
		String str = ChatColor.remove(e.toString());
		if (show) {
			System.out.println("[Global] " + str);
		}
		try {
			Writer output = new BufferedWriter(new FileWriter(Hack.getHackDir() + "/log/global.log", true));
			output.append(Hack.getCurrentDateAndTime() + " " + str + "\r\n");
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void showOnConsole(boolean show) {
		lastInstance.show = show;
	}

	private static GMLogger lastInstance = new GMLogger();

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
