package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandListRegions implements TabExecutor
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        return new ArrayList<>();
    }
}
