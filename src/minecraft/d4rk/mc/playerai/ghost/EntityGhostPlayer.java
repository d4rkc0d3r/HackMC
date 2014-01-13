package d4rk.mc.playerai.ghost;

import java.util.List;

import net.minecraft.src.AbstractClientPlayer;
import net.minecraft.src.AttributeInstance;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Minecraft;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.WorldClient;

public class EntityGhostPlayer extends AbstractClientPlayer {
	private WorldClient world;
	
	public EntityGhostPlayer(WorldClient world) {
		super(world, "Server Me");
		this.world = world;
		this.noClip = false;
		this.stepHeight = 0.5F;
	}

	@Override
	public void sendChatToPlayer(ChatMessageComponent var1) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(var1.func_111068_a(true));
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
	}

	static public final int ENTITY_ID = 123456789;
}
