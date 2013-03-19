package d4rk.mc.playerai;

import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;
import d4rk.mc.playerai.script.ScriptParser;

public abstract class BaseAI
{
    protected PlayerWrapper pWrap = null;
    protected ScriptParser scriptParser = null;
    protected boolean isStopped = true;

    /**
     * For other classes that do not want the PlayerWrapper check to be done.
     * Like the ScriptTask when it is registered.
     */
    protected BaseAI()
    {
        super();
    }

    public BaseAI(PlayerWrapper player)
    {
        this.pWrap = player;

        if (pWrap == null)
        {
            Hack.logInfo("pWrap == null");
            Hack.logInfo("pWrap = Hack.getPlayerWrapper()");
            pWrap = Hack.getPlayerWrapper();
        }

        if (!pWrap.isOk())
        {
            Hack.updatePlayerWrapper();
        }
    }

    public void toggle()
    {
        if (isStopped)
        {
            resume();
        }
        else
        {
            stop();
        }
    }

    public void stop()
    {
        isStopped = true;
    }

    public void start()
    {
        resume();
    }

    public void resume()
    {
        isStopped = false;
    }

    /**
     * <code>scriptParser = new ScriptParser(str, pWrap);</code>
     */
    public void startScript(String... str)
    {
        scriptParser = new ScriptParser(str, pWrap);
    }

    /**
     * write if(this.runScriptParser()) return; at the first line of onTick();
     *
     * @return true if the ScriptAI isn't finished yet, false otherwise.
     */
    protected boolean runScriptParser()
    {
        if (scriptParser == null)
        {
            return false;
        }

        scriptParser.onTick();

        if (scriptParser.isDone())
        {
            scriptParser = null;
            return false;
        }

        return true;
    }

    /**
     * write if(this.runScriptParser()) return; at the first line of onTick();
     */
    public abstract void onTick();
}
