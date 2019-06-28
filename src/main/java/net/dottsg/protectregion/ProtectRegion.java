package net.dottsg.protectregion;

import org.bukkit.plugin.java.JavaPlugin;

public final class ProtectRegion extends JavaPlugin
{
    public static RegionManager regionManager;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        log("Starting up");
        regionManager = new RegionManager();
        if(!RegionManager.SAVE_LOCATION.exists())
        {
            RegionManager.SAVE_LOCATION.mkdirs();
        }

        log("Loading regions");
        if(regionManager.loadRegions())
        {
            if(!regionManager.regions.isEmpty())
            {
                log(String.format("Loaded %d region%s successfully", regionManager.regions.size(), regionManager.regions.size() == 1 ? "" : "s"));
            }
        }
        else
        {
            log("Failed to load one or more regions!");
        }

        log("Registering events");
        getServer().getPluginManager().registerEvents(new RegionEventListener(),this);
        log("Registering commands");
        this.getCommand("protectregion").setExecutor(new CommandProtectRegion());
        this.getCommand("removeregion").setExecutor(new CommandRemoveRegion());
        this.getCommand("listregions").setExecutor(new CommandListRegions());
        this.getCommand("regioninfo").setExecutor(new CommandRegionInfo());
        this.getCommand("setregioncombat").setExecutor(new CommandRegionCombat());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        log("Saving regions");
        regionManager.saveAllRegions();
        log("Shutting down");
    }

    public static void log(String msg)
    {
        System.out.println("[ProtectRegion]: " + msg);
    }
}
