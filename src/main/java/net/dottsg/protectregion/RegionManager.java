package net.dottsg.protectregion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager
{
    public static final File SAVE_LOCATION = new File(ProtectRegion.getPlugin(ProtectRegion.class).getDataFolder(), "regions");

    private static File getDatabaseFile(Region region)
    {
        return new File(SAVE_LOCATION, region.getName() + ".json");
    }

    public Map<String, Region> regions = new HashMap<>();

    // Returns true if there is a region containing the given location
    public boolean isInRegion(Location location)
    {
        for(String name : regions.keySet())
        {
            Region region = regions.get(name);
            if(region.contains(location))
            {
                return true;
            }
        }

        return false;
    }

    // Get the region containing the given location
    public Region getContainerRegion(Location location)
    {
        for(String name : regions.keySet())
        {
            Region region = regions.get(name);
            if(region.contains(location))
            {
                return region;
            }
        }

        return null;
    }

    public boolean overlapsRegion(Region region)
    {
        for(String name : regions.keySet())
        {
            Region other = regions.get(name);
            if(region.overlaps(other))
            {
                return true;
            }
        }

        return false;
    }

    // Save a region to the database
    public void saveRegion(Region region)
    {
        // convert region to json data
        JsonObject regionObj = new JsonObject();
        regionObj.addProperty("name", region.getName());
        JsonArray rangeObj = new JsonArray();
        int[] range = region.getRange();
        for(int i = 0; i < 4; i++)
        {
            rangeObj.add(range[i]);
        }

        regionObj.add("coords", rangeObj);
        regionObj.addProperty("world", region.getWorld().getUID().toString());
        regionObj.addProperty("combatState", region.getCombatState().toString());
        Gson gson = new Gson();
        String data = gson.toJson(regionObj);

        // write to file
        File file = getDatabaseFile(region);
        try
        {
            if(!file.exists())
            {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        }
        catch(IOException e)
        {
            ProtectRegion.log("Error saving region file!");
        }
    }

    // shortcut for saving every region
    public void saveAllRegions()
    {
        for(String name : regions.keySet())
        {
            saveRegion(regions.get(name));
        }
    }

    // delete a region from the database
    public void deleteRegion(Region region)
    {
        File file = getDatabaseFile(region);
        if(!file.delete())
        {
            ProtectRegion.log("Error deleting region file!");
        }
    }

    public boolean loadRegions()
    {
        boolean ok = true;
        File[] files = SAVE_LOCATION.listFiles((f, s) -> s.endsWith(".json"));
        for(File file : files)
        {
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder data = new StringBuilder();
                int chr;
                while((chr = bufferedReader.read()) != -1)
                {
                    data.append((char) chr);
                }

                JsonParser jsonParser = new JsonParser();
                JsonObject regionObj = jsonParser.parse(data.toString()).getAsJsonObject();
                int[] range = new int[4];
                JsonArray coordsObj = regionObj.get("coords").getAsJsonArray();
                for(int i = 0; i < 4; i++)
                {
                    range[i] = coordsObj.get(i).getAsInt();
                }

                Server server = ProtectRegion.getPlugin(ProtectRegion.class).getServer();
                String name = regionObj.get("name").getAsString();
                Region newRegion = new Region(name, range[0], range[1], range[2], range[3],
                        server.getWorld(UUID.fromString(regionObj.get("world").getAsString())),
                        CombatState.valueOf(regionObj.get("combatState").getAsString()));

                regions.put(name, newRegion);
            }
            catch(Exception e)
            {
                ProtectRegion.log("Error loading region file '" + file.getName() + "'!");
                ok = false;
            }
        }

        return ok;
    }
}
