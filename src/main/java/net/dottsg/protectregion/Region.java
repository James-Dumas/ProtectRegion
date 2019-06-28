package net.dottsg.protectregion;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class Region
{
    private String name;
    private int x1;
    private int z1;
    private int x2;
    private int z2;
    private World world;
    private CombatState combatState;

    public Region(String name, int x1, int y1, int x2, int y2, World world)
    {
        this.name = name;
        this.x1 = Math.min(x1, x2);
        this.z1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.z2 = Math.max(y1, y2);
        this.world = world;
        combatState = CombatState.DISABLED;
    }

    public Region(String name, int x1, int y1, int x2, int y2, World world, CombatState combatState)
    {
        this.name = name;
        this.x1 = Math.min(x1, x2);
        this.z1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.z2 = Math.max(y1, y2);
        this.world = world;
        this.combatState = combatState;
    }

    public boolean contains(Location location)
    {
        return world.equals(location.getWorld())
               && location.getBlockX() >= x1
               && location.getBlockX() <= x2
               && location.getBlockZ() >= z1
               && location.getBlockZ() <= z2;
    }

    public boolean overlaps(Region other)
    {
        return world.equals(other.world) && !(x1 > other.x2 || other.x1 > x2 || z1 > other.z2 || other.z1 > z2);
    }

    public String getName()
    {
        return name;
    }

    public World getWorld()
    {
        return world;
    }

    public String getInfo()
    {
        return String.format(ChatColor.BLUE + "%s" + ChatColor.WHITE + "\n At: (%d, %d) to (%d, %d) in '%s'\n Combat: %s", name, x1, z1, x2, z2, world.getName(), combatState.displayString());
    }

    public int[] getRange()
    {
        return new int[] {x1, z1, x2, z2};
    }

    public CombatState getCombatState()
    {
        return combatState;
    }

    public void setCombatState(CombatState state)
    {
        combatState = state;
    }
}
