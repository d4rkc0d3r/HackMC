package d4rk.mc.util;

public class Unique<T> {
	private T data = null;
	private long id = 0;
	
	public Unique(T t) {
		data = t;
		id = currentID++;
	}
	
	public T get() {
		return data;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Unique) ? ((Unique)o).id == id : false;
	}
	
	private static long currentID = 0;
}
