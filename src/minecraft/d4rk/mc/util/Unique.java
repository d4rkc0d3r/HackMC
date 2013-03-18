package d4rk.mc.util;

public class Unique<T> {
	private T data = null;
	private int id = 0;
	
	public Unique(T t) {
		data = t;
		id = (int)(currentID++ & 0xFFFFFFFF);
	}
	
	public T get() {
		return data;
	}
	
	public int hashCode() {
		return id;
	}
	
	private static long currentID = 0;
}
