package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandProtectRegion implements TabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length == 5)
        {
            if(!ProtectRegion.regionManager.regions.containsKey(args[4]))
            {
                int[] coords = new int[4];
                for(int i = 0; i < 4; i++)
                {
                    try
                    {
                        coords[i] = Integer.parseInt(args[i]);
                    }
                    catch(NumberFormatException e)
                    {
                        return false;
                    }
                }

                if(sender instanceof Player)
                {
                    Player player = (Player) sender;
                    Region newRegion = new Region(args[4], coords[0], coords[1], coords[2], coords[3], player.getWorld());
                    if(ProtectRegion.regionManager.overlapsRegion(newRegion))
                    {
                        sender.sendMessage(ChatColor.RED + "This would overlap an existing region!");
                    }
                    else
                    {
                        ProtectRegion.regionManager.regions.put(args[4], newRegion);
                        int[] range = newRegion.getRange();
                        sender.sendMessage(ChatColor.GREEN + String.format("Region from (%d, %d) to (%d, %d) protected!", range[0], range[1], range[2], range[3]));
                        ProtectRegion.regionManager.saveRegion(newRegion);
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "This command must be run by a player.");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "There is already a region named '" + args[4] + "'!");
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> list = new ArrayList<>();
        Player player = (Player) sender;
        switch(args.length)
        {
            case 1:
                list.add(Integer.toString(player.getLocation().getBlockX()));
                break;

            case 2:
                list.add(Integer.toString(player.getLocation().getBlockZ()));
                break;

            case 3:
                list.add(Integer.toString(player.getLocation().getBlockX()));
                break;

            case 4:
                list.add(Integer.toString(player.getLocation().getBlockZ()));
        }

        return list;
    }
}
