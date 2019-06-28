package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegionCombat implements CommandExecutor
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
                if(ProtectRegion.regionManager.regions.containsKey(args[0]))
                {
                    region = ProtectRegion.regionManager.regions.get(args[0]);
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "No region named '" + args[0] + "'.");
                }

                if(CombatState.hasValueOf(args[1]))
                {
                    state = CombatState.valueOf(args[1].toUpperCase());
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
}
