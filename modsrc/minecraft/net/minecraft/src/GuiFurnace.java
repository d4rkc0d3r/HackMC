package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import d4rk.mc.event.listener.InventoryHelper;

public class GuiFurnace extends GuiContainer
{
    private TileEntityFurnace furnaceInventory;

    public GuiFurnace(InventoryPlayer par1InventoryPlayer, TileEntityFurnace par2TileEntityFurnace)
    {
        super(new ContainerFurnace(par1InventoryPlayer, par2TileEntityFurnace));
        this.furnaceInventory = par2TileEntityFurnace;
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
        this.buttonList.add(new GuiButton(1, xPosLeft, 0, 20, 20, "F"));
        ((GuiButton)buttonList.get(1)).enabled = false;
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id)
            {
                case 0:
                    this.mc.thePlayer.sendChatMessage("/close guiscreen");
                    break;

                case 1:
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
            btn.yPosition = ((i >= 2) ? i - 2 : i) * 20 + off;
        }

        super.drawScreen(par1, par2, par3);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String var3 = this.furnaceInventory.isInvNameLocalized() ? this.furnaceInventory.getInvName() : StatCollector.translateToLocal(this.furnaceInventory.getInvName());
        this.fontRenderer.drawString(var3, this.xSize / 2 - this.fontRenderer.getStringWidth(var3) / 2, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/furnace.png");
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
        int var6;

        if (this.furnaceInventory.isBurning())
        {
            var6 = this.furnaceInventory.getBurnTimeRemainingScaled(12);
            this.drawTexturedModalRect(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 2);
        }

        var6 = this.furnaceInventory.getCookProgressScaled(24);
        this.drawTexturedModalRect(var4 + 79, var5 + 34, 176, 14, var6 + 1, 16);
    }
}
