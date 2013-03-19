package d4rk.mc.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.src.ItemStack;
import d4rk.mc.PlayerWrapper;

/**
 * Shift left click operation(s) on all items that are already in the chest.
 */
public class FillChest extends Operation
{
    private Set<Integer> ids = new HashSet();
    private boolean ignoreHotbar;

    public FillChest()
    {
        this(null);
    }

    public FillChest(PlayerWrapper pWrap)
    {
        this(pWrap, true);
    }

    public FillChest(boolean ignoreHotbar)
    {
        this(null, ignoreHotbar);
    }

    public FillChest(PlayerWrapper pWrap, boolean ignoreHotbar)
    {
        super(pWrap, 0);
        this.ignoreHotbar = ignoreHotbar;
    }

    @Override
    public void doOperation()
    {
        List<ItemStack> inv = pWrap.player.openContainer.getInventory();

        for (int i = 0; i < inv.size() - 36; i++)
        {
            ItemStack item = inv.get(i);
            ids.add((item == null) ? 0 : item.itemID);
        }

        int index = inv.size() - ((ignoreHotbar) ? 10 : 1);

        for (; index >= inv.size() - 36; index--)
        {
            ItemStack item = inv.get(index);

            if (item == null || (!ids.contains(item.itemID)))
            {
                continue;
            }

            pWrap.windowClick(index, true);
        }

        done();
    }
}
