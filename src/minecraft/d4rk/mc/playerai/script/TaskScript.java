package d4rk.mc.playerai.script;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.minecraft.src.Minecraft;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;

public class TaskScript extends ScriptTask {

	public TaskScript(String[] cmd, PlayerWrapper pWrap) {
		super(cmd, pWrap);
		String scriptName = "SHOULD NOT HAPPEN!!!";
		ArrayList<String> script = new ArrayList<String>();
		try {
			scriptName = cmd[1];
			DataInputStream in = new DataInputStream(new FileInputStream(Hack.getMCDir()+"/hack/script/"+scriptName+".script"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null)   {
				script.add(line);
			}
			in.close();
			this.scriptParser = new ScriptParser(script.toArray(new String[0]), pWrap);
		} catch (FileNotFoundException e) {
			throw new ScriptParserException("Script \"" + scriptName + "\" not found!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TaskScript(String name) {
		super(name);
	}

	public void onTick() {
		if(isStopped) return;
		if(!this.runScriptParser())
			done(scriptParser.getReturn());
	}
}
