package d4rk.mc.gui.overlay;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.RenderItem;
import net.minecraft.src.World;
import d4rk.mc.event.EventListener;
import d4rk.mc.event.EventManager;
import d4rk.mc.event.PostSendPacketEvent;
import d4rk.mc.gui.BasicGuiOverlay;

public class OreLevelIndicator extends BasicGuiOverlay implements EventListener {
	public List<ItemStack> oreList = new ArrayList();
    protected static RenderItem itemRenderer = new RenderItem();
    private ItemStack quarz;
    private ItemStack emerald;
    private ItemStack diamond;
    private ItemStack redstone;
    private ItemStack lapis;
    private ItemStack gold;
    private ItemStack iron;
	
	public OreLevelIndicator() {
		super();
		quarz = new ItemStack(Item.netherQuartz);
		emerald = new ItemStack(Item.emerald);
		diamond = new ItemStack(Item.diamond);
		redstone = new ItemStack(Item.redstone);
		lapis = new ItemStack(Item.dyePowder);
		lapis.setItemDamage(4);
		gold = new ItemStack(Item.ingotGold);
		iron = new ItemStack(Item.ingotIron);
		EventManager.registerEvents(this);
	}
	
	public void onPlayerMoveEvent(PostSendPacketEvent event) {
		if(!(event.getPacket() instanceof Packet10Flying)) {
			return;
		}
		BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(
				(int)Math.floor(mc.thePlayer.posX), (int)Math.floor(mc.thePlayer.posZ));
		
		oreList.clear();
		
		if(biome == BiomeGenBase.sky) {
			
		} else if(biome == BiomeGenBase.hell) {
			if(mc.thePlayer.posY <= 128) {
				oreList.add(quarz);
			}
		} else {
			if(mc.thePlayer.posY <= 30 && (biome == BiomeGenBase.extremeHills || biome == BiomeGenBase.extremeHills.extremeHillsEdge)) {
				oreList.add(emerald);
			}
			if(mc.thePlayer.posY <= 15) {
				oreList.add(diamond);
				oreList.add(redstone);
			}
			if(mc.thePlayer.posY <= 23) {
				oreList.add(lapis);
			}
			if(mc.thePlayer.posY <= 32) {
				oreList.add(gold);
			}
			if(mc.thePlayer.posY <= 64) {
				oreList.add(iron);
			}
		}
		
		width = 16 + 20 * oreList.size();
		height = 32;
	}
	
	@Override
	public String getName() {
		return "Ore level indicator";
	}

	@Override
	public void draw() {
		for(int i = 0; i < oreList.size(); ++i) {
			ItemStack item = oreList.get(i);
	        zLevel = 200.0F;
	        itemRenderer.zLevel = 200.0F;
	        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, item, i * 20 + 8 , 8);
	        zLevel = 0.0F;
	        itemRenderer.zLevel = 0.0F;
		}
        GL11.glDisable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}
}
