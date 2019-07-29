package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandRemoveRegion implements TabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length <= 1)
        {
            Region region = null;
            if(args.length == 0)
            {
                if(sender instanceof Player)
                {
                    Player player = (Player) sender;
                    if(ProtectRegion.regionManager.isInRegion(player.getLocation()))
                    {
                        region = ProtectRegion.regionManager.getContainerRegion(player.getLocation());
                        ProtectRegion.regionManager.regions.remove(region.getName());
                    }
                    else
                    {
                        sender.sendMessage(ChatColor.RED + "No region specified.");
                    }
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No region specified.");
                }
            }
            else
            {
                if(ProtectRegion.regionManager.regions.containsKey(args[0]))
                {
                    region = ProtectRegion.regionManager.regions.remove(args[0]);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No region named '" + args[0] + "'.");
                }
            }

            if(region != null)
            {
                int[] range = region.getRange();
                sender.sendMessage(ChatColor.GREEN + String.format("Region from (%d, %d) to (%d, %d) is no longer protected!", range[0], range[1], range[2], range[3]));
                ProtectRegion.regionManager.deleteRegion(region);
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> list = new ArrayList<>();
        if(args.length == 1)
        {
            list.addAll(ProtectRegion.regionManager.regions.keySet());
            list.sort(String::compareToIgnoreCase);
        }

        return list;
    }
}

