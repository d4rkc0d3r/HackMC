package net.minecraft.src;

import org.lwjgl.opengl.GL11;

import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.inventory.FillChest;
import d4rk.mc.inventory.RowSortChest;

public class GuiChest extends GuiContainer
{
    private static final ResourceLocation field_110421_t = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;

    /**
     * window height is calculated with this values, the more rows, the heigher
     */
    private int inventoryRows;

    public GuiChest(IInventory par1IInventory, IInventory par2IInventory)
    {
        super(new ContainerChest(par1IInventory, par2IInventory));
        this.upperChestInventory = par1IInventory;
        this.lowerChestInventory = par2IInventory;
        this.allowUserInput = false;
        short var3 = 222;
        int var4 = var3 - 108;
        this.inventoryRows = par2IInventory.getSizeInventory() / 9;
        this.ySize = var4 + this.inventoryRows * 18;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        int xPosLeft = this.width / 2 - 110;
        int xPosRight = this.width / 2 + 90;
        this.buttonList.clear(); // yPos is 0 because it is calculated dynamically
        this.buttonList.add(new GuiButton(0, xPosLeft, 0, 20, 20, "X"));
        this.buttonList.add(new GuiButton(1, xPosLeft, 0, 20, 20, "G"));
        this.buttonList.add(new GuiButton(2, xPosLeft, 0, 20, 20, "P"));
        this.buttonList.add(new GuiButton(3, xPosLeft, 0, 20, 20, "F"));
        this.buttonList.add(new GuiButton(4, xPosRight, 0, 20, 20, "<"));
        this.buttonList.add(new GuiButton(5, xPosRight, 0, 20, 20, ">"));
        this.buttonList.add(new GuiButton(6, xPosRight, 0, 20, 20, "="));
        this.buttonList.add(new GuiButton(7, xPosRight, 0, 20, 20, "| |"));
        this.buttonList.add(new GuiButton(8, xPosRight, 0, 20, 20, "U"));
        ((GuiButton)buttonList.get(7)).enabled = false;
        ((GuiButton)buttonList.get(8)).enabled = false;
        InventoryHelper.resetUndo();
    }
    
    @Override
    public void onGuiClosed() {
    	super.onGuiClosed();
    	InventoryHelper.clearQueue();
    	InventoryHelper.resetUndo();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.enabled) {
			switch (par1GuiButton.id) {
			case 0:
				this.mc.thePlayer.sendChatMessage("/close guiscreen");
				break;

			case 1:
				this.mc.thePlayer.sendChatMessage("/withdraw all");
				break;

			case 2:
				this.mc.thePlayer.sendChatMessage("/deposit all");
				break;

			case 3:
				InventoryHelper.getInstance().addToQueue(new FillChest(false));
				break;

			case 4:
				this.mc.thePlayer.sendChatMessage("/sort chest up");
				break;

			case 5:
				this.mc.thePlayer.sendChatMessage("/sort chest down");
				break;

			case 6:
				InventoryHelper.getInstance().addToQueue(new RowSortChest());
				break;

			case 7:
				break;

			case 8:
				InventoryHelper.getInstance().undo();
				break;

			default:
				break;
			}
		}
	}

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        int off = (this.height - this.ySize) / 2;

        for (int i = 0; i < this.buttonList.size(); i++)
        {
            GuiButton btn = (GuiButton)this.buttonList.get(i);
            btn.yPosition = ((i > 3) ? i - 4 : i) * 20 + off;
        }
        
        ((GuiButton)buttonList.get(8)).enabled = InventoryHelper.canUndo();

        super.drawScreen(par1, par2, par3);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(this.lowerChestInventory.isInvNameLocalized() ? this.lowerChestInventory.getInvName() : I18n.func_135053_a(this.lowerChestInventory.getInvName()), 8, 6, 4210752);
        this.fontRenderer.drawString(this.upperChestInventory.isInvNameLocalized() ? this.upperChestInventory.getInvName() : I18n.func_135053_a(this.upperChestInventory.getInvName()), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(field_110421_t);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(var4, var5 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
