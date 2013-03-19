package d4rk.mc.playerai;

import java.util.ArrayList;

import d4rk.mc.BlockWrapper;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntitySign;

public class Quarry
{
    private BlockWrapper from;
    private BlockWrapper to;
    private BlockWrapper[] content = new BlockWrapper[0];
    private BlockWrapper[] stair = new BlockWrapper[0];
    private int current = -1;

    public int[] dangerID = new int[]
    {
        Block.waterMoving.blockID,
        Block.waterStill.blockID,
        Block.lavaMoving.blockID,
        Block.lavaStill.blockID
    };

    /**
     * Creates a quarry between (and including) the two given blocks.
     *
     * @param from
     * @param to
     * @throws QuarryException
     */
    public Quarry(BlockWrapper from, BlockWrapper to)
    {
        this.from = (from.y >= to.y) ? from.clone() : to.clone();
        this.to = (from.y < to.y) ? from.clone() : to.clone();

        if (from.world != to.world)
        {
            throw new QuarryException("Quarry worlds differ!");
        }

        this.start();
    }

    /**
     * The constructor creates a quarry object from the data given on the sign.<br><br>
     * Example:<br>
     * [Quarry]<br>
     * <18<br>
     * 18<br>
     * 5<br><br>
     * Creates a quarry 18x18 blocks down to height y=5 from one block below the sign.
     *
     * @param sign
     * @throws QuarryException
     */
    public Quarry(BlockWrapper sign)
    {
        TileEntitySign s = (TileEntitySign)sign.getTileEntity();

        if (s.blockType.blockID == Block.signPost.blockID)
        {
            throw new QuarryException("Sign must be a wall sign!");
        }

        if (!s.signText[0].equalsIgnoreCase("[Quarry]"))
        {
            throw new QuarryException("Sign is no valid Quarry sign!");
        }

        this.from = sign.clone();
        from.y--;
        this.to = from.clone();
        to.y = s.signText[3].startsWith("-") ? sign.y - Integer.parseInt(s.signText[3].substring(1)) : Integer.parseInt(s.signText[3]);
        int lr = Integer.parseInt(s.signText[1].substring(1)) - 1;
        int out = Integer.parseInt(s.signText[2]) - 1;
        boolean isLeft = s.signText[1].startsWith("<");

        switch (sign.getMetadata())
        {
            case BlockWrapper.NEGX: // this is the player look direction, so the sign looks in positive x direction!
                to.x += out;
                to.z += isLeft ? lr : -lr;
                break;

            case BlockWrapper.POSX:
                to.x -= out;
                to.z -= isLeft ? lr : -lr;
                break;

            case BlockWrapper.NEGZ:
                to.z += out;
                to.x += isLeft ? -lr : lr;
                break;

            case BlockWrapper.POSZ:
                to.z -= out;
                to.x -= isLeft ? -lr : lr;
                break;

            default:
                throw new QuarryException("Sign has unexpected metadata!");
        }

        this.start();
    }

    public void start()
    {
        stair = getStair();
//		for(BlockWrapper b : stair) {
//			b.setID(Block.cloth.blockID); // white wool
//		}
//
//		from.setIDandMetadata(Block.cloth.blockID, 14); // red wool
//		to.setIDandMetadata(Block.cloth.blockID, 5); // lime wool
        content = from.getAllBlocks(to);
        current = -1;
    }

    private boolean isStair(BlockWrapper b)
    {
        for (BlockWrapper t : stair) if (t.equals(b))
            {
                return true;
            }

        return false;
    }

    /**
     * Calculates the stair case blocks and returns them in an array.
     */
    private BlockWrapper[] getStair()
    {
        if (from.x == to.x || from.z == to.z)
        {
            return new BlockWrapper[0];
        }

        ArrayList<BlockWrapper> result = new ArrayList<BlockWrapper>(this.getHeight());
        BlockWrapper last = from.clone();
        int dir = 2;

        if (last.getRelative(dir).isInsideOf(from, to))
        {
            dir = 3;
        }

        result.add(last);

        for (int y = from.y - 1; y >= to.y; y--)
        {
            BlockWrapper tmp = null;

            for (int i = 0;; i++)
            {
                if (dir == BlockWrapper.POSY)
                {
                    tmp = last.getRelative(0, -1, 0);
                    break;
                }

                tmp = last.getRelative(0, -1, 0).getRelative(dir);

                if (tmp.isInsideOf(from, to))
                {
                    break;
                }

                dir += 2;

                if (dir == 6)
                {
                    dir = 3;
                }
                else if (dir == 7)
                {
                    dir = 2;
                }

                if (i > 4)
                {
                    dir = BlockWrapper.POSY;
                }
            }

            last = tmp;
            result.add(tmp);
        }

        return result.toArray(new BlockWrapper[0]);
    }

    /**
     * @return The next BlockWrapper which is mineable, not touching any of
     * the danger blocks and isn't a stair block.
     */
    public BlockWrapper getNext()
    {
        int w = 3;
        int xm = getX();
        int zm = getZ();
        int i = current;

        while (i < content.length)
        {
            if (i == -1)
            {
                i = 0;
            }
            else if (i >= 0 && i < content.length - 1 && content[i + 1].y != content[i].y)
            {
                i += 1;
            }
            else if ((w * xm - 1) == ((i % (xm * zm)) % (w * xm)))
            {
                i += 1;
            }
            else if ((i % (xm * zm)) / xm == zm - 1)
            {
                i -= ((((i % (xm * zm)) / xm) % w) * xm - 1);
            }
            else if (((i % (xm * zm)) / xm) % w < (w - 1))
            {
                i += xm;
            }
            else
            {
                i -= ((w - 1) * xm - 1);
            }

            if (i >= 0 && i < content.length && shouldBeMined(content[i]))
            {
                current = i;
                return content[current];
            }
        }

        current = content.length;
        return null;
    }

    protected boolean shouldBeMined(BlockWrapper b)
    {
        return b.isMineable() && !isStair(b) && !b.isTouching(dangerID);
    }

    public int getX()
    {
        return Math.abs(from.x - to.x) + 1;
    }

    public int getZ()
    {
        return Math.abs(from.z - to.z) + 1;
    }

    public int getHeight()
    {
        return Math.abs(from.y - to.y) + 1;
    }

    public boolean isDone()
    {
        return getCurrent() == null;
    }

    /**
     * Resets the index to -1 (the beginning).
     */
    public void reset()
    {
        current = -1;
    }

    /**
     * Returns the currently selected Block or null.
     */
    public BlockWrapper getCurrent()
    {
        try
        {
            return content[current].clone();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }
    }
}
