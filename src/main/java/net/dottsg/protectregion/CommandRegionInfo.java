package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegionInfo implements CommandExecutor
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
                    region = ProtectRegion.regionManager.regions.get(args[0]);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No region named '" + args[0] + "'.");
                }
            }

            if(region != null)
            {
                sender.sendMessage(region.getInfo());
            }
        }
        else
        {
            return false;
        }

        return true;
    }
}
