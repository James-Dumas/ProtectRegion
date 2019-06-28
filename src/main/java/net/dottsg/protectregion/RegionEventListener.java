package net.dottsg.protectregion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.function.Predicate;

public class RegionEventListener implements Listener
{
    private static void cancelEventInRegion(Cancellable e, Location l)
    {
        if(ProtectRegion.regionManager.isInRegion(l))
        {
            e.setCancelled(true);
        }
    }

    // returns true if the entity (which caused damage to a player) is a result of another player's actions
    private static boolean isPvP(Entity damaged, Entity damager)
    {
        if(damaged instanceof Player)
        {
            if(damager instanceof Player)
            {
                return true;
            }

            if(damager instanceof Projectile)
            {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if(shooter instanceof Player || shooter instanceof BlockProjectileSource)
                {
                    return true;
                }
            }
        }

        return false;
    }

    // returns true if this is an action of the player hitting a non-living object (e.g. armor stand)
    private static boolean isPvO(Entity damaged, Entity damager)
    {
        return (damager instanceof Player || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)) && (damaged instanceof ItemFrame || damaged instanceof ArmorStand || damaged instanceof Painting || damaged instanceof EnderCrystal || damaged instanceof Minecart || damaged instanceof Boat);
    }

    // returns true for negative potion effects
    private static boolean isBadEffect(PotionEffectType effectType)
    {
        return effectType.equals(PotionEffectType.HARM)
            || effectType.equals(PotionEffectType.POISON)
            || effectType.equals(PotionEffectType.HUNGER)
            || effectType.equals(PotionEffectType.LEVITATION)
            || effectType.equals(PotionEffectType.SLOW)
            || effectType.equals(PotionEffectType.SLOW_DIGGING)
            || effectType.equals(PotionEffectType.UNLUCK)
            || effectType.equals(PotionEffectType.WEAKNESS)
            || effectType.equals(PotionEffectType.WITHER);
    }

    // Block events

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockBurnEvent(BlockBurnEvent e)
    {
        cancelEventInRegion(e, e.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent e)
    {
        List<Block> blockList = e.blockList();
        blockList.removeIf(new Predicate<Block>()
        {
            @Override
            public boolean test(Block block)
            {
                if(ProtectRegion.regionManager.isInRegion(block.getLocation()))
                {
                    Region region = ProtectRegion.regionManager.getContainerRegion(block.getLocation());
                    return region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP;
                }

                return false;
            }
        });
    }

    // Player events

    @EventHandler
    public void onArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getRightClicked().getLocation());
        }
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getBlockClicked().getLocation());
        }
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getBlockClicked().getLocation());
        }
    }

    @EventHandler
    public void onPlayerTakeLecternBookEvent(PlayerTakeLecternBookEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()))
        {
            cancelEventInRegion(e, e.getLectern().getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        if(ProtectRegion.regionManager.isInRegion(e.getPlayer().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(player.getLocation());
            if(region.getCombatState() == CombatState.DISABLED && e.getItem() != null && (e.getItem().getType().equals(Material.SPLASH_POTION) || e.getItem().getType().equals(Material.LINGERING_POTION)))
            {
            }
        }

        if(!(player.hasPermission("region.edit") || player.isOp()) && e.hasBlock() && (
                  e.getClickedBlock().getType().equals(Material.TNT)
               || e.getClickedBlock().getType().equals(Material.REPEATER)
               || e.getClickedBlock().getType().equals(Material.COMPARATOR)
               || e.getClickedBlock().getType().equals(Material.DAYLIGHT_DETECTOR)
               || e.getClickedBlock().getType().equals(Material.END_PORTAL_FRAME)
               || e.getClickedBlock().getType().equals(Material.PUMPKIN)
               || e.getClickedBlock().getType().equals(Material.COMPOSTER)
               || e.getClickedBlock().getType().equals(Material.SWEET_BERRY_BUSH)
               || Tag.FLOWER_POTS.isTagged(e.getClickedBlock().getType())
               || e.getClickedBlock().getType().equals(Material.CAKE)
               || (e.getClickedBlock().getType().equals(Material.NOTE_BLOCK) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        ))
        {
            cancelEventInRegion(e, e.getClickedBlock().getLocation());
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e)
    {
        Player player = e.getPlayer();
        if(!(player.hasPermission("region.edit") || player.isOp()) && (
                  e.getRightClicked() instanceof Sheep
               || e.getRightClicked() instanceof ItemFrame
               || e.getRightClicked() instanceof ArmorStand
        ))
        {
            cancelEventInRegion(e, e.getRightClicked().getLocation());
        }
    }

    // Entity Events

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getLocation());
            if((region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP) && e.getEntity() instanceof Monster && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityBreakDoorEvent(EntityBreakDoorEvent e)
    {
        cancelEventInRegion(e, e.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent e)
    {
        cancelEventInRegion(e, e.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(region.getCombatState() == CombatState.DISABLED || isPvP(e.getEntity(), e.getCombuster()) ? region.getCombatState() == CombatState.PVE : region.getCombatState() == CombatState.PVP)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            boolean ispvp = isPvP(e.getEntity(), e.getDamager());
            boolean ispvo = isPvO(e.getEntity(), e.getDamager());
            if(region.getCombatState() == CombatState.DISABLED && (ispvp || !((ispvo && (e.getDamager().hasPermission("region.edit") || e.getDamager().isOp())) || (!ispvo && (e.getDamager().hasPermission("region.attackentity") || e.getDamager().isOp())))))
            {
                e.setCancelled(true);
            }

            if(region.getCombatState() == CombatState.PVE && (isPvP(e.getEntity(), e.getDamager()) || (ispvo && !(e.getDamager().hasPermission("region.edit") || e.getDamager().isOp()))))
            {
                e.setCancelled(true);
            }

            if(region.getCombatState() == CombatState.PVP && !(ispvp || ((ispvo || (e.getDamager().hasPermission("region.attackentity") || e.getDamager().isOp())) && (!ispvo || (e.getDamager().hasPermission("region.edit") || e.getDamager().isOp())))))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(region.getCombatState() == CombatState.DISABLED && !(
                    e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                 || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
                 || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                 || e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)
            ))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent e)
    {
        List<Block> blockList = e.blockList();
        blockList.removeIf(new Predicate<Block>()
        {
            @Override
            public boolean test(Block block)
            {
                if(ProtectRegion.regionManager.isInRegion(block.getLocation()))
                {
                    Region region = ProtectRegion.regionManager.getContainerRegion(block.getLocation());
                    return region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP;
                }

                return false;
            }
        });
    }

    @EventHandler
    public void onEntityPlaceEvent(EntityPlaceEvent e)
    {
        if(!(e.getPlayer() != null && (e.getPlayer().hasPermission("region.edit") || e.getPlayer().isOp())))
        {
            cancelEventInRegion(e, e.getEntity().getLocation());
        }
    }

    @EventHandler
    public void onEntityPotionEffectEvent(EntityPotionEffectEvent e)
    {
        if((e.getCause() == EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD || e.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH) && ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(isBadEffect(e.getModifiedType()) && (region.getCombatState() == CombatState.DISABLED || (region.getCombatState() == CombatState.PVE && e.getEntity() instanceof Player) || (region.getCombatState() == CombatState.PVP && !(e.getEntity() instanceof Player))))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTargetEvent(EntityTargetEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(e.getEntity() instanceof Monster && (region.getCombatState() == CombatState.DISABLED || region.getCombatState() == CombatState.PVP))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e)
    {
        if(ProtectRegion.regionManager.isInRegion(e.getEntity().getLocation()))
        {
            Region region = ProtectRegion.regionManager.getContainerRegion(e.getEntity().getLocation());
            if(region.getCombatState() == CombatState.DISABLED)
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSheepDyeWoolEvent(SheepDyeWoolEvent e)
    {
        cancelEventInRegion(e, e.getEntity().getLocation());
    }

    // Vehicle Events

    @EventHandler
    public void onVehicleDestroyEvent(VehicleDestroyEvent e)
    {
        if(!(e.getVehicle() instanceof Boat) && e.getAttacker() != null && !(e.getAttacker().hasPermission("region.edit") || e.getAttacker().isOp()))
        {
            cancelEventInRegion(e, e.getVehicle().getLocation());
        }
    }

    // Hanging Entity Events

    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent e)
    {
        if(e.getRemover() instanceof Player)
        {
            Player player = (Player) e.getRemover();
            if(!(player.hasPermission("region.edit") || player.isOp()))
            {
                cancelEventInRegion(e, e.getEntity().getLocation());
            }
        }
        else
        {
            cancelEventInRegion(e, e.getEntity().getLocation());
        }
    }

    @EventHandler
    public void onHangingBreakEvent(HangingBreakEvent e)
    {
        if(e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION || e.getCause() == HangingBreakEvent.RemoveCause.OBSTRUCTION)
        {
            cancelEventInRegion(e, e.getEntity().getLocation());
        }
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent e)
    {
        Player player = e.getPlayer();
        if(!(player != null && (player.hasPermission("region.edit") || player.isOp())))
        {
            cancelEventInRegion(e, e.getEntity().getLocation());
        }
    }
}
