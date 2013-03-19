package d4rk.mc.playerai.script;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import d4rk.mc.Hack;
import d4rk.mc.PlayerWrapper;

public class ScriptParser {
	private PlayerWrapper pWrap = null;
	private String[] script = new String[0];
	private ScriptTask currentTask = null;
	private int currentLine = 0;
	private boolean isDone = false;
	
	/** The name of the variable, which stores the last return value. */
	private String retVar = RETURN_VARIABLE;
	
	/** The actual return value of the ScriptParser. */
	private String retVal = ScriptTask.NONE;
	
	private HashMap<String, String> variables = new HashMap();
	private HashMap<String, Integer> jumpPoints = new HashMap();
	
	public ScriptParser(String scriptName, PlayerWrapper pWrap) {
		this.pWrap = pWrap;
		ArrayList<String> script = new ArrayList<String>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(Hack.getHackDir()+"/script/"+scriptName+".script"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null)   {
				script.add(line);
			}
			in.close();
			this.script = script.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Script \"" + scriptName + "\" not found!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ScriptParser(String[] script, PlayerWrapper pWrap) {
		this.pWrap = pWrap;
		this.script = script;
	}
	
	public String getReturn() {
		return retVal;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	private void done(String retVal) {
		this.retVal = retVal;
		this.isDone = true;
	}
	
	public void onTick() {
		if(isDone) return;
		while(currentTask == null || currentTask.isDone()) {
			try {
				if(currentLine >= script.length) {
					done(retVal);
					return;
				}
				if(script[currentLine].startsWith("#")) {
					currentLine++;
					continue;
				}
				if(script[currentLine].startsWith(":")) {
					String l = script[currentLine].substring(1).toLowerCase();
					if(!jumpPoints.containsKey(l))
						jumpPoints.put(l, currentLine++);
					continue;
				}
				
				String line = script[currentLine];
				boolean isNoLog = script[currentLine].startsWith("nolog: ");
				if(isNoLog) {
					line = line.substring(7);
				} else {
					log("[In] " + line);
				}
				parseLine(line);
			} catch(ScriptParserException e) {
				log("[Error] " + (currentLine + 1) + ": " + e.getMessage());
			} catch(TooFewArgumentsException e) {
				log("[Error] To few arguments for " + e.getMessage());
			}
			currentLine++;
		}
		currentTask.onTick();
	}
	
	private void parseLine(String line) throws ScriptParserException {
		line = preProcessLine(line);
		String first = line.toLowerCase();
		try {
			first = first.substring(0, line.indexOf(' '));
		} catch(IndexOutOfBoundsException e) {}
		
		if(first.equals("set")) {
			String[] split = line.split(" ", 3);
			if(split.length < 3) {
				throw new ScriptParserException("You have to assign a value to a variable!");
			}
			setVariableValue(split[1], split[2]);
		} else if(line.startsWith("print ")) {
			log("[Out]" + line.split(" ", 2)[1]);
		} else if(line.startsWith("if")) {
			
		} else if(line.startsWith("goto ")) {
			String label = line.split(" ", 2)[1].toLowerCase();
			if(label.isEmpty())
				throw new ScriptParserException("You have to specifie a jump label");
			Integer lineNumber = jumpPoints.get(label);
			if(lineNumber == null) {
				for(int i = currentLine + 1; i < script.length; i++) {
					if(script[i].startsWith(":")) {
						String l = script[i].substring(1).toLowerCase();
						if(!jumpPoints.containsKey(l))
							jumpPoints.put(l, i);
						if(label.equals(l)) {
							lineNumber = i;
							break;
						}
					}
				}
			}
			if(lineNumber == null) {
				throw new ScriptParserException("Jump label '" + label + "' not found");
			}
			currentLine = lineNumber;
		} else {
			currentTask = ScriptTask.get(line, pWrap);
		}
	}
	
	private String preProcessLine(String str) throws ScriptParserException {
		str = fillInVariables(str);
		int last = 0;
		int first = 0;
		StringBuilder sb = new StringBuilder();
		boolean isOpen = false;
		for(int i = 0;i < str.length();i++) {
			if(str.charAt(i) == '{' && !isOpen) {
				sb.append(str.substring(last, i));
				isOpen = true;
				first = i+1;
			} else if(str.charAt(i) == '}' && isOpen) {
				sb.append(valueOf(str.substring(first, i).replace(" ", "")));
				isOpen = false;
				last = i+1;
			}
		}
		sb.append(str.substring(last));
		return sb.toString();
	}
	
	private String fillInVariables(String str) throws ScriptParserException {
		StringBuilder sb = new StringBuilder();
		int last = 0;
		for(int start=0;start<str.length();start++) {
			if(str.charAt(start) != '$')
				continue;
			int end = start+1;
			for(;end<str.length();end++)
				if(!isAllowedVariableCharacter(str.charAt(end)))
					break;
			sb.append(str.substring(last, start));
			sb.append(getVariableValue(str.substring(start, end)));
			start = end;
			last = end;
		}
		sb.append(str.substring(last, str.length()));
		return sb.toString();
	}
	
	/**
	 * Only to be called by method {@link #preProcessLine(String)}<br>
	 * All variables must already be replaced in this form.
	 * @param str
	 * @return
	 */
	private String valueOf(String str) throws ScriptParserException {
		String value = getFirstExpression(str);
		int end = value.length();
		if(value.startsWith("(")) {
			value = valueOf(getFirstParentheses(value));
		}
		String second = null;
		int start = 0;
		for(;end < str.length()-1;end++) {
			char c = str.charAt(end);
			char nextC = str.charAt(end+1);
			
			switch(c) {
			case '=': case '+': case '&': case '|': case '^': case '*': case '/':
				second = getFirstExpression(str.substring(end+1));
				end += second.length();
				if(nextC == '(') {
					second = valueOf(getFirstParentheses(second));
				}
				break;
			default:
				break;
			}
			
			switch(c) {
			case '=':
				value = equal(value, second);
				break;
			case '+':
				value = add(value, second);
				break;
			case '&':
				value = and(value, second);
				break;
			case '|':
				value = or(value, second);
				break;
			case '^':
				value = xor(value, second);
				break;
			case '*':
				value = mul(value, second);
				break;
			case '/':
				value = div(value, second);
				break;
			default:
				break;
			}
		}
		return value;
	}
	
	private String getFirstExpression(String str) throws ScriptParserException {
		if(str.length() == 0)
			return str;
		if(str.charAt(0) == '(')
			return '(' + getFirstParentheses(str) + ')';
		int end = 0;
		outside:
		for(;end < str.length();end++) {
			switch(str.charAt(end)) {
			case '=': case '+': case '&': case '|': case '^': case '*': case '/':
				break outside;
			default:
				break;
			}
		}
		return str.substring(0, end);
	}
	
	private String or(String s1, String s2) {
		return ""+(Boolean.valueOf(s1) || Boolean.valueOf(s1));
	}
	
	private String and(String s1, String s2) {
		return ""+(Boolean.valueOf(s1) && Boolean.valueOf(s1));
	}
	
	private String xor(String s1, String s2) {
		try {
			return ""+Math.pow(Double.valueOf(s1), Double.valueOf(s2));
		} catch(Exception e) {
			return ""+(Boolean.valueOf(s1) ^ Boolean.valueOf(s1));
		}
	}
	
	private String equal(String s1, String s2) {
		try {
			return ""+(Double.valueOf(s1).equals(Double.valueOf(s2)));
		} catch(Exception e) {
			return ""+(s1.equals(s2));
		}
	}
	
	private String add(String s1, String s2) throws ScriptParserException {
		try {
			return ""+(Double.valueOf(s1) + Double.valueOf(s2));
		} catch(Exception e) {
			throw new ScriptParserException("'" + s1 + "' or '" + s2 + "' is not a valid number");
		}
	}
	
	private String mul(String s1, String s2) throws ScriptParserException {
		try {
			return ""+(Double.valueOf(s1) * Double.valueOf(s2));
		} catch(Exception e) {
			throw new ScriptParserException("'" + s1 + "' or '" + s2 + "' is not a valid number");
		}
	}
	
	private String div(String s1, String s2) throws ScriptParserException {
		try {
			return ""+(Double.valueOf(s1) / Double.valueOf(s2));
		} catch(ArithmeticException e) { 
			throw new ScriptParserException("You should not divide by zero");
		} catch(Exception e) {
			throw new ScriptParserException("'" + s1 + "' or '" + s2 + "' is not a valid number");
		}
	}
	
	private void setVariableValue(String var, String val) {
		val = val.toUpperCase().replace(' ', '_');
		if(val.endsWith(".0"))
			val = val.substring(0, val.length() - 2);
		variables.put(var, val);
	}
	
	private String getVariableValue(String str) throws ScriptParserException {
		checkVariable(str);
		String value = variables.get(str.substring(1));
		if(value == null)
			throw new ScriptParserException("Unknown variable '" + str + "'");
		return value;
	}
	
	private void checkVariable(String str) throws ScriptParserException {
		if(str.startsWith("$")) {
			for(int i=1;i<str.length();i++) {
				char c = str.charAt(i);
				if(!isAllowedVariableCharacter(c))
					throw new ScriptParserException("Illegal variable name (caused by char '" + c + "')");
			}
			return;
		}
		throw new ScriptParserException("A variable must start with a '$'");
	}
	
	private boolean isAllowedVariableCharacter(char c) {
		return Character.isLetter(c) || Character.isDigit(c) || c == '_';
	}

	private String getFirstParentheses(String str) throws ScriptParserException {
		return getFirstSubstrEnclosedBy(str, '(', ')');
	}
	
	private String getFirstSubstrEnclosedBy(String str, char a, char b) throws ScriptParserException {
		int first = str.indexOf(a);
		if(first == -1)
			throw new ScriptParserException("No '" + a + "' char found");
		int openCount = 0;
		for(int i = first+1;i < str.length();i++) {
			if(str.charAt(i) == a)
				openCount++;
			if(str.charAt(i) == b) {
				if(--openCount < 0) {
					return str.substring(first+1, i);
				}
			}
		}
		throw new ScriptParserException("Not enought '" + b + "' chars found");
	}
	
	private boolean hasParentheses(String str) {
		return str.indexOf('(') != -1 && str.indexOf(')') != -1;
	}
	
	private void log(String str) {
		Hack.log("[Script] " + str);
	}
	
	private static final String RETURN_VARIABLE = "RETURN_VARIABLE";
}
