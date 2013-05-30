package d4rk.mc.inventory;

import net.minecraft.src.ItemStack;

public class ItemGroup implements Comparable<ItemGroup> {
	private final int hashCode;
	private final String toString;
	
	private final ItemStack ref;
	
	private int count;

	public ItemGroup(ItemStack ref) {
		this(ref, 0);
	}

	public ItemGroup(ItemStack ref, int count) {
		this.ref = ref;
		this.hashCode = 0xc0d3b4b3 ^ ((ref == null) ? 0 : ref.itemID * 0xb00b);
		this.count = count;
		this.toString = (ref == null) ? "Air" : ref.getDisplayName();
	}
	
	public ItemGroup setCount(int count) {
		this.count = count;
		return this;
	}
	
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return "[" + count + " x " + toString + "]";
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ItemGroup)) {
			return false;
		}
		return ItemCompare.sameItemGroup(ref, ((ItemGroup)obj).ref);
	}

	@Override
	public int compareTo(ItemGroup o) {
		return (count < o.count) ? 1 : (this.equals(o)) ? 0 : -1;
	}
}
