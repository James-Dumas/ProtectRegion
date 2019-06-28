package net.dottsg.protectregion;

public enum CombatState
{
    DISABLED,   // disables PvP, PvE, and hostile mob spawning
    PVE,        // disables PvP
    PVP,        // disables PvE and hostile mob spawning
    ENABLED;    // disables nothing, obviously

    public String displayString()
    {
        switch(this)
        {
            case DISABLED:
                return "Disabled";
            case PVE:
                return "PvE Only";
            case PVP:
                return "PvP Only";
            case ENABLED:
                return "Enabled";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static boolean hasValueOf(String string)
    {
        for(CombatState value : CombatState.values())
        {
            if(string.toUpperCase().equals(value.toString()))
            {
                return true;
            }
        }

        return false;
    }
}
