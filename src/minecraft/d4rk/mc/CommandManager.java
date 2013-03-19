package d4rk.mc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.List;

import d4rk.mc.event.listener.InventoryHelper;
import d4rk.mc.inventory.Operation;
import d4rk.mc.inventory.SelectionSortChest;
import d4rk.mc.inventory.CloseWindow;
import d4rk.mc.inventory.Deposit;
import d4rk.mc.inventory.OperationList;
import d4rk.mc.inventory.Withdraw;
import d4rk.mc.playerai.ScriptAI;
import d4rk.mc.util.Vec3D;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockSilverfish;
import net.minecraft.src.Chunk;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;

public class CommandManager
{
    static public boolean parseLocal(String str)   // true won't send it to the server
    {
        return parse(str, Hack.mc.thePlayer.getEntityName(), "LOCALE");
    }

    static public boolean parse(String cmd, String sender, String rank)   // returns true if it found the command
    {
        if (cmd.startsWith("/tps") && Permission.has(sender, rank, Permission.INFO))
        {
            String[] args = cmd.split(" ");
            int count = 1;

            if (args != null) if (args.length > 1)
                {
                    count = Integer.parseInt(args[1]);
                }

            String msg = "Current Server TPS: " + round(Hack.lastTPS.getAvg(count), 2);
            ImproveChat.sendMessage(sender, msg);
            return true;
        }

        if (cmd.equals("/ctps") && Permission.has(sender, rank, Permission.LOCALE))
        {
            ImproveChat.sendMessage(sender, "Current Client TPS: " + round(20000.0D / Hack.last20Ticks.getSum(), 2));
            return true;
        }

        if (cmd.startsWith("/sort chest") && Permission.has(sender, rank, Permission.LOCALE))
        {
            InventoryHelper.getInstance(Hack.getPlayerWrapper()).addToQueue(
                    new SelectionSortChest(Hack.getPlayerWrapper(), cmd.contains("up")));
            return true;
        }

        if (cmd.equals("/reset invhelper") && Permission.has(sender, rank, Permission.LOCALE))
        {
            InventoryHelper.clearQueue();
            ImproveChat.addToChatGui("InventoryHelper was cleared");
            return true;
        }

        if (cmd.equals("/close guiscreen") && Permission.has(sender, rank, Permission.LOCALE))
        {
            PlayerWrapper pWrap = Hack.getPlayerWrapper();
            InventoryHelper inv = InventoryHelper.getInstance(pWrap);
            inv.addToQueue(new CloseWindow(pWrap));
            return true;
        }

        if (cmd.startsWith("/deposit") || cmd.startsWith("/withdraw") && Permission.has(sender, rank, Permission.LOCALE))
        {
            String[] args = cmd.split(" ");
            PlayerWrapper pWrap = Hack.getPlayerWrapper();
            InventoryHelper inv = InventoryHelper.getInstance(pWrap);
            OperationList opList = new OperationList();
            Class op = (cmd.startsWith("/deposit")) ? Deposit.class : Withdraw.class;

            if (cmd.contains("all"))
            {
                try
                {
                    opList.add((Operation) op.getConstructor(pWrap.getClass()).newInstance(pWrap));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                for (int i = 1; i < args.length; i++)
                {
                    int id = 0;

                    try
                    {
                        id = Integer.valueOf(args[i]);
                    }
                    catch (Exception e)
                    {
                        for (int j = 0; j < 256; j++)
                        {
                            Block b = Block.blocksList[j];

                            if (b == null)
                            {
                                continue;
                            }

                            if (("tile." + args[i]).equals(b.getUnlocalizedName()))
                            {
                                id = j;
                                break;
                            }
                        }

                        if (id == 0)
                        {
                            for (int j = 0; j < Item.itemsList.length; j++)
                            {
                                Item item = Item.itemsList[j];

                                if (item == null)
                                {
                                    continue;
                                }

                                if (("item." + args[i]).equals(item.getUnlocalizedName()))
                                {
                                    id = item.itemID;
                                    break;
                                }
                            }
                        }
                    }

                    if (id == 0)
                    {
                        PlayerString.ME.send(ChatColor.RED + "This is not a valid item or block: " + args[i]);
                    }
                    else
                    {
                        try
                        {
                            opList.add((Operation) op.getConstructor(pWrap.getClass(), Integer.class).newInstance(pWrap, id));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (opList.size() > 0)
            {
                inv.addToQueue(opList);
            }

            return true;
        }

        if (cmd.equals("/chunkborder") && Permission.has(sender, rank, Permission.LOCALE))
        {
            Chunk c = Hack.getPlayerWrapper().getChunk();
            BlockWrapper chunkBase = new BlockWrapper(c.xPosition * 16, (int)Hack.pWrap.player.posY - 1, c.zPosition * 16);
            int count = 0;

            for (int x = 0; x < 16; x++)
            {
                int id = count++ % 2 == 0 ? Block.fence.blockID : Block.netherFence.blockID;
                int id2 = count % 2 == 0 ? Block.fence.blockID : Block.netherFence.blockID;
                chunkBase.getRelative(x, 0, 0).setID(id);
                chunkBase.getRelative(x, 1, 0).setID(id);
                chunkBase.getRelative(x, 0, 15).setID(id2);
                chunkBase.getRelative(x, 1, 15).setID(id2);
            }

            for (int z = 0; z < 16; z++)
            {
                int id = count++ % 2 == 0 ? Block.fence.blockID : Block.netherFence.blockID;
                int id2 = count % 2 == 0 ? Block.fence.blockID : Block.netherFence.blockID;
                chunkBase.getRelative(0, 0, z).setID(id);
                chunkBase.getRelative(0, 1, z).setID(id);
                chunkBase.getRelative(15, 0, z).setID(id2);
                chunkBase.getRelative(15, 1, z).setID(id2);
            }

            return true;
        }

        if (cmd.startsWith("/status") && Permission.has(sender, rank, Permission.PLAYERINFO))
        {
            String msg = "Health: " + Hack.mc.thePlayer.getHealth() + " | Pos: (" +
                    (int)Math.floor(Hack.mc.thePlayer.posX) + " | " +
                    (int)Math.floor(Hack.mc.thePlayer.posY) + " | " +
                    (int)Math.floor(Hack.mc.thePlayer.posZ) + ")";
            ImproveChat.sendMessage(sender, msg);
            return true;
        }

        if (cmd.startsWith("/setdefault ") && Permission.has(sender, rank, Permission.LOCALE))
        {
            String[] args = cmd.split(" ");
            int perm = Permission.INFO;

            if (args != null) if (args.length > 1)
                {
                    perm = Integer.parseInt(args[1]);
                }

            String msg = "Changed Default Permission to " + perm;
            ImproveChat.sendMessage(sender, msg);
            Permission.DEFAULT = perm;
            return true;
        }

        if (cmd.equalsIgnoreCase("/sheep") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        "usenearestlift",
                        "wait 15",
                        "chunk.shearsheep",
                        "chunk.collectitems",
                        "chunk.shearsheep",
                        "wait 15",
                        "chunk.collectitems",
                        "usenearestlift"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/send ") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.sendChatMessage(cmd.substring(6));
            return true;
        }

        if (cmd.equalsIgnoreCase("/loadconfig") && Permission.has(sender, rank, Permission.LOCALE))
        {
            Hack.cfg.load();
            return true;
        }

        if (cmd.startsWith("/aitoggle") && Permission.has(sender, rank, Permission.REMOTE))
        {
            try
            {
                Hack.myAI.toggle();
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }

        if (cmd.startsWith("/mobhunter") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.myAI = new PlayerAIMobHunter(Hack.mc.thePlayer, new Vec3D(0, 0), Hack.cfg.focusNearestMobMaxDist);
            return true;
        }

        if (cmd.startsWith("/automine") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.myAI = new PlayerAIAutoMine(5);
            return true;
        }

        if (cmd.startsWith("/vartest") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        "send Var test!",
                        "print test",
                        "sleep 20",
                        "set test lOl roflmaO kinQ",
                        "send abroham + $test",
                        "send 20 + 30 = {20+30}",
                        "send 20 ^ 3 = {20^3}"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/gototest") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        ":start",
                        "send GoTo test!",
                        "goto start"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/timeback") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        "set time 12000",
                        ":start",
                        "set time {$time + -20}",
                        "send /time set $time",
                        "sleep 19",
                        "goto start"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/spam ") && Permission.has(sender, rank, Permission.REMOTE))
        {
            String[] args = cmd.split(" ", 3);
            String spamString = "";
            String spamSleepTime = "20";

            if (args.length == 2)
            {
                spamString = args[1];
            }
            else
            {
                if (args[1].startsWith("-t"))
                {
                    spamSleepTime = args[1].substring(2);
                    spamString = args[2];
                }
                else
                {
                    spamString = args[1] + " " + args[2];
                }
            }

            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        ":start",
                        "nolog: send " + spamString,
                        "nolog: sleep " + spamSleepTime,
                        "nolog: goto start"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/scripttest") && Permission.has(sender, rank, Permission.REMOTE))
        {
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), new String[]
                    {
                        "send Hallo Leute!",
                        "lookat -1.0 60 240",
                        "sleep 20",
                        "lookat 90.5 59.5 240.5",
                        "moveto 90.5 59.5 240.5",
                        "wait 10",
                        "lookat 80.5 59.5 210.5",
                        "moveto 80.5 59.5 210.5",
                        "wait 10",
                        "lookat 84.5 58.3 209.5",
                        "wait 25",
                        "leftclick 84.5 58.3 209.5",
                        "wait 10",
                        "rightclick 84 57 209 TOP",
                        "wait 10",
                        "mineblock ~0 ~-2 ~2",
                        "wait 30",
                        "rightclick ~0 ~-3 ~2 TOP"
                    });
            Hack.activeAI.start();
            return true;
        }

        if (cmd.startsWith("/script ") && Permission.has(sender, rank, Permission.REMOTE))
        {
            String[] args = cmd.split(" ", 2);
            Hack.activeAI = new ScriptAI(Hack.getPlayerWrapper(), args[1]);
            Hack.activeAI.start();
            return true;
        }

        return false;
    }

    static public boolean isInputMessageHidden(String str)
    {
        if (str.startsWith("/tps"))
        {
            return true;
        }

        if (str.startsWith("/ctps"))
        {
            return true;
        }

        if (str.startsWith("/status"))
        {
            return true;
        }

        if (str.startsWith("/send"))
        {
            return true;
        }

        if (str.startsWith("/focus"))
        {
            return true;
        }

        if (str.startsWith("/setdefault"))
        {
            return true;
        }

        return false;
    }

    static public boolean isOutputMessageHidden(String str)
    {
        if (str.startsWith("Current Server TPS: "))
        {
            return true;
        }

        if (str.startsWith("Current Client TPS: "))
        {
            return true;
        }

        if (str.startsWith("Health:"))
        {
            return true;
        }

        return false;
    }

    static double round(double d, int n)
    {
        return (double)(((int)(d * Math.pow(10, n) + 0.5)) / Math.pow(10, n));
    }
}
