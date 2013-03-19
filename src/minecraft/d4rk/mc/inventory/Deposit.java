package d4rk.mc.inventory;

import java.util.List;

import net.minecraft.src.ItemStack;

import d4rk.mc.PlayerWrapper;

/**
 * Shift left click operation(s) in the inventory when an chest is opened.
 */
public class Deposit extends Operation
{
    /** The number of stacks to be shift clicked */
    private int count;
    private boolean ignoreHotbar;

    private int id;

    /**
     * Deposit's everything that is in the players inventory except for the
     * hotbar.
     */
    public Deposit(PlayerWrapper pWrap)
    {
        this(pWrap, -1, 27, true);
    }

    /**
     * Deposit's everything with the specified id that is in the players
     * inventory except for the hotbar.
     */
    public Deposit(PlayerWrapper pWrap, Integer id)
    {
        this(pWrap, id, 27, true);
    }

    public Deposit(PlayerWrapper pWrap, Integer id, Integer count)
    {
        this(pWrap, id, count, true);
    }

    /**
     * Deposit items in the next chest or double chest.
     *
     * @param pWrap
     * @param id
     *            The item id to be clicked. -1 for everything.
     * @param count
     *            The number of stacks to be shift clicked.
     * @param ignoreHotbar
     */
    public Deposit(PlayerWrapper pWrap, Integer id, Integer count, boolean ignoreHotbar)
    {
        super(pWrap, 0);
        this.id = id;
        this.count = count;
        this.ignoreHotbar = ignoreHotbar;
    }

    @Override
    public boolean canDoOperation(int currentInventoryType)
    {
        switch (currentInventoryType)
        {
            case 0: // Chest
            case 3: // Dispenser
                return pWrap.hasOpenInventoryGUI();

            default:
                return false;
        }
    }

    @Override
    public void doOperation()
    {
        List inv = pWrap.player.openContainer.getInventory();
        int index = inv.size() - ((ignoreHotbar) ? 10 : 1);
        int alreadyRemoved = 0;

        for (; index >= inv.size() - 36; index--)
        {
            ItemStack item = (ItemStack) inv.get(index);

            if (item == null || (item.itemID != id && id != -1))
            {
                continue;
            }

            pWrap.windowClick(index, true);

            if (++alreadyRemoved == count)
            {
                break;
            }
        }

        done();
    }
}
