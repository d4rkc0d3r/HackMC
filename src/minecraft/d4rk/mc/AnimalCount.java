package d4rk.mc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.Chunk;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class AnimalCount {
	static public AnimalCount ac = new AnimalCount();
	
    private Map<String,Integer> result = new HashMap<String,Integer>();
    private EntityPlayer player = null;
    private World w;
    private int x;
    private int z;
    private boolean animalOnly = false;
    
    private AnimalCount() {
    	
    }

    public AnimalCount(EntityPlayer player) {
        this.player = player;
        w = player.worldObj;
        x = player.chunkCoordX;
        z = player.chunkCoordZ;
    }
    
    public AnimalCount init(EntityPlayer player) {
    	this.player = player;
        w = player.worldObj;
        x = player.chunkCoordX;
        z = player.chunkCoordZ;
        return this;
    }
    
	private static void getChunkResult(Map<String, Integer> res, Chunk c) {
		for (List<Entity> list : c.entityLists) {
			for (Entity e : list) {
				if (e != null) {
					addOne(res, getKey(e));
				}
			}
		}
	}
    
	public static Map<String, Integer> getChunkResult(Chunk c) {
		Map<String, Integer> res = new HashMap<String, Integer>();
		getChunkResult(res, c);
		return res;
	}
    
    public static Map<String,Integer> getWorldResult(World w) {
		Map<String, Integer> res = new HashMap<String, Integer>();
    	for(Object obj : w.loadedEntityList) {
    		if(obj != null) {
    			addOne(res, getKey((Entity)obj));
    		}
    	}
    	return res;
    }
    
    public void showChunkResult(Chunk c) {
		player.addChatMessage("§5Chunk"+c.getChunkCoordIntPair().toString()+" has "+getCount()+" entities");
		showVerboseResult();
		return;
    }
    
    public void showWorldResult() {
		player.addChatMessage("§5This world has "+getCount()+" loaded entities");
		showResult();
		return;
    }
    
    public void showVerboseResult() {
		String keySet[] = result.keySet().toArray(new String[0]);
		for(int i=0;i<keySet.length;i++)
			player.addChatMessage(keySet[i]+": "+result.get(keySet[i]));
    }
    
    public void showResult() {
		String keySet[] = result.keySet().toArray(new String[0]);
		for(int i=0;i<keySet.length;i++) {
	    	if(animalOnly)
	    		if(!isAnimal(keySet[i]))
	    			break;
			player.addChatMessage(keySet[i]+": "+result.get(keySet[i]));
		}
    }
    
    public void checkWorld() {
    	result.clear();
    	Chunk c = w.getChunkFromChunkCoords(x,z);
    	if(countWorld(w))
    		showWorldResult();
    }
    
    public void checkChunk() {
    	result.clear();
    	Chunk c = w.getChunkFromChunkCoords(x,z);
    	if(countChunk(c))
    		showChunkResult(c);
    }
    
    private boolean countChunk(Chunk c) {
    	for (int k = 0; k < c.entityLists.length; k++)
		{
			List list = c.entityLists[k];
			for (int l = 0; l < list.size(); l++)
			{
				Entity e = (Entity)list.get(l);
				if(e!=null)
					addOne(getKey(e));
			}
		}
    	return true;
    }
    
    private boolean countWorld(World w) {
    	List e = w.loadedEntityList;
    	for (int k = 0; k < e.size(); k++)
			if(e.get(k)!=null)
				addOne(getKey((Entity)e.get(k)));
    	return true;
    }
    
    private static String getKey(Entity e) {
    	if(e instanceof EntityPlayer) return "Player";
    	return EntityList.getEntityString(e);
    }
    
    private boolean isAnimal(String e) {
    	if(e == "Sheep") return true;
    	if(e == "Cow") return true;
    	if(e == "Pig") return true;
    	if(e == "Chicken") return true;
    	return false;
    }
    
    private void addOne(String entity) {
    	addOne(result, entity);
    }
    
	private static void addOne(Map<String, Integer> map, String entity) {
		Integer val = map.get(entity);
		map.put(entity, val == null ? 1 : 1 + val);
	}
    
    private long getCount() {
    	long res = 0;
    	Integer[] values = new Integer[1];
    	values = result.values().toArray(values);
    	for(int i = 0;i<values.length;i++)
    		res += values[i].longValue();
    	return res;
    }
    
    private void clear() {
        result.clear();
        result = new HashMap<String,Integer>();
    }
}
