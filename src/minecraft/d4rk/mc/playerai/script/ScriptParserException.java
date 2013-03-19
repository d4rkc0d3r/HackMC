package d4rk.mc.playerai.script;

public class ScriptParserException extends RuntimeException
{
    public ScriptParserException() {}

    public ScriptParserException(String arg0)
    {
        super(arg0);
    }

    public ScriptParserException(Throwable arg0)
    {
        super(arg0);
    }

    public ScriptParserException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    public ScriptParserException(String arg0, Throwable arg1, boolean arg2,
            boolean arg3)
    {
        super(arg0, arg1, arg2, arg3);
    }
}
