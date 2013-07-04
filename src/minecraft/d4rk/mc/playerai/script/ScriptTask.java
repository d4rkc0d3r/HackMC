package d4rk.mc.playerai.script;

import java.util.HashMap;

import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.playerai.BaseAI;
import net.minecraft.src.EntityPlayer;

public abstract class ScriptTask extends BaseAI {
	private boolean isDone = false;
	private String retValue = "null";
	private String commandUsed = null;
	private String error = NONE;
	
	protected ScriptTask(String[] cmd, PlayerWrapper pWrap) {
		super(pWrap);
		commandUsed = new String(cmd[0]);
		start();
	}
	
	/**
	 * Every ScriptTask has to implement this constructor with:<br>
	 * scriptMap.put(name.toLowerCase(), this);
	 */
	public ScriptTask(String name) {
		super();
		scriptMap.put(name.toLowerCase(), this);
	}
	
	public boolean isDone() {
		return scriptParser == null ? isDone : false;
	}
	
	/**
	 * Write at the first line of onTick():<br>
	 * <code>if(isStopped || isDone()) return;<br>
	 * if(runScriptParser()) return;</code>
	 */
	public abstract void onTick();
	
	protected void error(String err) {
		done(ERROR);
		error = err;
	}
	
	protected void done() {
		this.done("");
	}
	
	protected void done(String ret) {
		isDone = true;
		retValue = ret;
	}
	
	public String getReturn() {
		return retValue;
	}
	
	public String getError() {
		return error;
	}
	
	static public ScriptTask get(String cmd, PlayerWrapper pWrap) {
		String[] args = cmd.split(" ");
		try {
			return scriptMap.get(args[0].toLowerCase()).getClass().
				   getDeclaredConstructor(String[].class, PlayerWrapper.class).newInstance(args, pWrap);
		} catch(TooFewArgumentsException argEx) {
			throw argEx;
		} catch (Exception e) {
			return null;
		}
	}
	
	static private HashMap<String, ScriptTask> scriptMap = new HashMap<String, ScriptTask>();
	
	static {
		// this will register the different script commands
		// do NOT give arguments right here
		new TaskWait("wait");
		new TaskWait("idle");
		new TaskWait("sleep");
		new TaskMoveTo("moveto");
		new TaskLookAt("lookat");
		new TaskLeftClick("leftclick");
		new TaskRightClick("rightclick");
		new TaskSend("send");
		new TaskScript("script");
		new TaskSelectItem("selectitem");
		new TaskShearSheepOnChunk("chunk.shearsheep");
		new TaskCollectItemsOnChunk("chunk.collectitems");
		new TaskWaitForMove("waitformove");
		new TaskActivateNearestLift("usenearestlift");
		new TaskMineBlock("mineblock");
		new TaskPMineBlock("pmineblock");
		new TaskPathTo("pathto");
		new TaskDeposit("deposit");
		new TaskSneak("sneak");
		new TaskPlaceBlock("placeblock");
	}

	static public final String ERROR = "ERROR";
	
	static public final String NONE = "NONE";
	static public final String BAD = "BAD";
	static public final String BAD_ARGS = "BAD_ARGS";
	static public final String ITEM_NOT_FOUND = "ITEM_NOT_FOUND";
	static public final String BLOCK_NOT_FOUND = "BLOCK_NOT_FOUND";
	static public final String LIFT_NOT_FOUND = "LIFT_NOT_FOUND";
	static public final String OUT_OF_RANGE = "OUT_OF_RANGE";
	static public final String BLOCK_NOT_MINEABLE = "BLOCK_NOT_MINEABLE";
	static public final String NO_PATH_FOUND = "NO_PATH_FOUND";
}
