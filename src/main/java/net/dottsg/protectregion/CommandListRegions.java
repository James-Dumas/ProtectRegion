package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;

public class CommandListRegions implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length == 0)
        {
            if(ProtectRegion.regionManager.regions.size() == 0)
            {
                sender.sendMessage(ChatColor.RED + "There are no protected regions.");
            }
            else
            {
                LinkedList<String> regionInfoList = new LinkedList<>();
                for(String name : ProtectRegion.regionManager.regions.keySet())
                {
                    regionInfoList.add(ProtectRegion.regionManager.regions.get(name).getInfo());
                }

                sender.sendMessage(String.join("\n", regionInfoList));
            }
        }
        else
        {
            return false;
        }

        return true;
    }
}
