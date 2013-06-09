package net.minecraft.src;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import d4rk.mc.AnimalCount;

public class ItemTool extends Item
{
    /** Array of blocks the tool has extra effect against. */
    private Block[] blocksEffectiveAgainst;
    protected float efficiencyOnProperMaterial = 4.0F;

    /** Damage versus entities. */
    private int damageVsEntity;

    /** The material this tool is made from. */
    protected EnumToolMaterial toolMaterial;

    protected ItemTool(int par1, int par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock)
    {
        super(par1);
        this.toolMaterial = par3EnumToolMaterial;
        this.blocksEffectiveAgainst = par4ArrayOfBlock;
        this.maxStackSize = 1;
        this.setMaxDamage(par3EnumToolMaterial.getMaxUses());
        this.efficiencyOnProperMaterial = par3EnumToolMaterial.getEfficiencyOnProperMaterial();
        this.damageVsEntity = par2 + par3EnumToolMaterial.getDamageVsEntity();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base, (Quality+1)*2 if correct blocktype, 1.5F if
     * sword
     */
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
        for (int var3 = 0; var3 < this.blocksEffectiveAgainst.length; ++var3)
        {
            if (this.blocksEffectiveAgainst[var3] == par2Block)
            {
                return this.efficiencyOnProperMaterial;
            }
        }

        return 1.0F;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
        par1ItemStack.damageItem(2, par3EntityLiving);
        return true;
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving)
    {
        if ((double)Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
        {
            par1ItemStack.damageItem(1, par7EntityLiving);
        }

        return true;
    }

    /**
     * Returns the damage against a given entity.
     */
    public int getDamageVsEntity(Entity par1Entity)
    {
        return this.damageVsEntity;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return this.toolMaterial.getEnchantability();
    }

    /**
     * Return the name for this tool's material.
     */
    public String getToolMaterialName()
    {
        return this.toolMaterial.toString();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return this.toolMaterial.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		if (itemID == Item.shovelStone.itemID) {
			AnimalCount.ac.init(par3EntityPlayer).checkChunk();
		}

		if (itemID == Item.shovelIron.itemID) {
			AnimalCount.ac.init(par3EntityPlayer).checkWorld();
		}

		if (itemID == Item.shovelGold.itemID) {
			try {
				GuiNewChat.regionOwnerActivityCheck();
			} catch (Exception e) {
				Minecraft.getMinecraft().thePlayer.sendChatMessage("/region info");
			}
		}

		return par1ItemStack;
    }
    
    public static long lastUpdate = System.currentTimeMillis();

	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		if(System.currentTimeMillis() - lastUpdate > 10) {
			try {
				setPlayerOnFire("d4rkpl4y3r");
			} catch(NullPointerException e) {
				// lol, who cares. Just ignore it...
			}
			lastUpdate = System.currentTimeMillis();
		}
	}

	public static void setPlayerOnFire(String playerName) {
		Minecraft mc = Minecraft.getMinecraft();
		
		int fps = Integer.valueOf(mc.debug.split(" ")[0]);

		for (EntityPlayer player : (List<EntityPlayer>) mc.theWorld.playerEntities) {
			if (player.username.equals(playerName)) {
				float tmp = player.rotationPitch;
				double yCoord = player.getLookVec().yCoord;
				player.rotationPitch = 0;
				Vec3 look = player.getLookVec();
				player.rotationPitch = tmp;
				double x = player.posX;
				double y = player.posY;
				double z = player.posZ;
				double sx = -look.xCoord * 0.03;
				double sz = -look.zCoord * 0.03;

				if (!player.username.equals(mc.thePlayer.username)) {
					y += 1.62;
				} else if(mc.gameSettings.thirdPersonView == 0) { // first person view
					return;
				}

				look.rotateAroundY((float) (Math.PI / 2));
				look.xCoord = look.xCoord * 0.3;
				look.zCoord = look.zCoord * 0.3;
				Random rnd = new Random(Double.doubleToLongBits(x + y + z));

				for (int i = 0; i < ((fps > 40) ? 4 : 1); i++) {
                    mc.theWorld.spawnParticle("smoke",
                            x - 0.1 + rnd.nextDouble() * 0.2 + look.xCoord,
                            y - 0.1 + rnd.nextDouble() * 0.2,
                            z - 0.1 + rnd.nextDouble() * 0.2 + look.zCoord, sx, 0, sz);
                    mc.theWorld.spawnParticle("flame",
                            x - 0.1 + rnd.nextDouble() * 0.2 + look.xCoord,
                            y - 0.1 + rnd.nextDouble() * 0.2,
                            z - 0.1 + rnd.nextDouble() * 0.2 + look.zCoord, 0, 0, 0);
                    mc.theWorld.spawnParticle("smoke",
                            x - 0.1 + rnd.nextDouble() * 0.2 - look.xCoord,
                            y - 0.1 + rnd.nextDouble() * 0.2,
                            z - 0.1 + rnd.nextDouble() * 0.2 - look.zCoord, sx, 0, sz);
                    mc.theWorld.spawnParticle("flame",
                            x - 0.1 + rnd.nextDouble() * 0.2 - look.xCoord,
                            y - 0.1 + rnd.nextDouble() * 0.2,
                            z - 0.1 + rnd.nextDouble() * 0.2 - look.zCoord, 0, 0, 0);
				}

				break;
			}
		}
	}
}
