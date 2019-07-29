package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandRegionCombat implements TabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length > 0 && args.length <= 2)
        {
            Region region = null;
            CombatState state = null;
            if(args.length == 1)
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

                if(CombatState.hasValueOf(args[0]))
                {
                    state = CombatState.valueOf(args[0].toUpperCase());
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Unrecognized combat setting. Options are " + ChatColor.BLUE + "enabled" + ChatColor.RED + "," + ChatColor.BLUE + "pve" + ChatColor.RED + "," + ChatColor.BLUE + "pvp" + ChatColor.RED + "," + ChatColor.BLUE + "disabled" + ChatColor.RED + ".");
                }
            }
            else // args.length == 2
            {
                if(ProtectRegion.regionManager.regions.containsKey(args[1]))
                {
                    region = ProtectRegion.regionManager.regions.get(args[1]);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No region named '" + args[1] + "'.");
                }

                if(CombatState.hasValueOf(args[0]))
                {
                    state = CombatState.valueOf(args[0].toUpperCase());
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "Unrecognized combat setting. Options are " + ChatColor.BLUE + "enabled" + ChatColor.RED + "," + ChatColor.BLUE + "pve" + ChatColor.RED + "," + ChatColor.BLUE + "pvp" + ChatColor.RED + "," + ChatColor.BLUE + "disabled" + ChatColor.RED + ".");
                }
            }

            if(region != null && state != null)
            {
                region.setCombatState(state);
                sender.sendMessage(ChatColor.GREEN + "Combat settings updated!");
                ProtectRegion.regionManager.saveRegion(region);
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
       switch(args.length)
       {
           case 1:
               list.add("disabled");
               list.add("pvp");
               list.add("pve");
               list.add("enabled");
               break;

           case 2:
               list.addAll(ProtectRegion.regionManager.regions.keySet());
               list.sort(String::compareToIgnoreCase);
       }

       return list;
    }
}
