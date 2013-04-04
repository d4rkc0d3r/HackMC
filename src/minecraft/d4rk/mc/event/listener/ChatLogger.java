package d4rk.mc.event.listener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import d4rk.mc.ChatColor;
import d4rk.mc.Hack;
import d4rk.mc.event.ChatEvent;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.PlayerChatEvent;

public class ChatLogger implements EventListener {
	public void onChatEvent(ChatEvent event) {
		String str = ChatColor.remove(event.getSrc());
		try {
			Writer output = new BufferedWriter(new FileWriter(Hack.getHackDir() + "/log/chat.log", true));
			output.append(Hack.getCurrentDateAndTime() + " " + str + "\r\n");
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onPlayerChatEvent(PlayerChatEvent event) {
		String str = ChatColor.remove(event.getSrc());
		try {
			Writer output = new BufferedWriter(new FileWriter(Hack.getHackDir() + "/log/send.log", true));
			output.append(Hack.getCurrentDateAndTime() + " " + str + "\r\n");
			output.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
