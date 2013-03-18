package d4rk.mc.util;

import java.util.ArrayList;
import java.util.List;

public class RingNumber<T extends Number> {
	private ArrayList<T> data;
	private int pos;
	
	public RingNumber(int size) {
		create(size);
	}
	
	public void create(int size) {
		pos = 0;
		data = new ArrayList<T>();
		for(int i = 0;i<size;i++)
			data.add(null);
	}
	
	public void flush() {
		int sz = data.size();
		data.clear();
		create(sz);
	}
	
	public void add(T obj) {
		if(++pos>=data.size())
	        pos=0;
		data.set(pos, obj);
	}
	
	private int translate(int n) {
		n = pos-n;
	    if(n<0)
	        n+=data.size();
	    return n;
	}
	
	public T get(int n) {
	    return data.get(translate(n));
	}
	
	public List getList() {
		List list = new ArrayList<T>();
		int n = 0;
		for(int i=data.size()-1;i>=0;i--)
			if(data.get(translate(i))!=null)
				list.add(data.get(translate(i)));
		return list;
	}
	
	public double getSum() {
		double sum = 0;
		for(int i=0;i<data.size();i++) {
			Number c = get(i);
			if(c == null) continue;
			sum += c.doubleValue();
		}
		return sum;
	}
	
	public double getAvg(int n) {
		double avg = 0.0;
		if(n>data.size())
			n = data.size();
		int count = 0;
		for(int i=0;i<n;i++) {
			Number c = get(i);
			if(c == null) continue;
			avg += c.doubleValue();
			count++;
		}
		avg /= count;
		return avg;
	}
	
	public double getAvg() {
		return getAvg(data.size());
	}
}
