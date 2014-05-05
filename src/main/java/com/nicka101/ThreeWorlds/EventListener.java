package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Generation.OrePopulator;
import com.nicka101.ThreeWorlds.Generation.ShrinePopulator;
import com.nicka101.ThreeWorlds.Generation.TreePopulator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;

import java.util.ArrayList;

/**
 * Created by Nicka101 on 01/04/2014.
 */
@SuppressWarnings("unused")
public class EventListener implements Listener {

    private final ThreeWorlds plugin;
    private boolean netherInitComplete = false;

    protected EventListener(final ThreeWorlds plugin){
        this.plugin = plugin;
        new WaterCollisionTask(plugin).runTaskTimer(plugin, 1, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return; //we handle this in a seperate handler
        if(event.getEntity() instanceof Player){
            plugin.getPlayerManager()
                    .GetHandlerForPlayer((Player) event.getEntity())
                    .processDamageEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player || event.getDamager() instanceof Player))return;
        PlayerManager playerManager = plugin.getPlayerManager();
        if(event.getDamager() instanceof Player) playerManager.GetHandlerForPlayer((Player)event.getDamager()).processGlobalDamageModifiers(event);
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            return;//PvP handled by teams
        }
        if(event.getDamager() instanceof Player){
            playerManager.GetHandlerForPlayer((Player) event.getDamager())
                    .processPlayerAttackEvent(event);
        } else {
            playerManager.GetHandlerForPlayer((Player) event.getEntity())
                    .processPlayerAttackedEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(final EntityTargetEvent event){
        if(!(event.getTarget() instanceof Player))return;
        plugin.getPlayerManager().GetHandlerForPlayer((Player) event.getTarget())
                .processEntityTargetEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldChange(final PlayerChangedWorldEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processWorldChange(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerJoinEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processLoginEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorPlayerLogin(final PlayerJoinEvent event){
        plugin.getPlayerManager().HandlePlayerLogin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorPlayerLogout(final PlayerQuitEvent event){
        plugin.getPlayerManager().HandlePlayerLogout(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldLoad(final WorldInitEvent event){
        if(!netherInitComplete){
            if(event.getWorld().getEnvironment() != World.Environment.NETHER)return;
            World nether = event.getWorld();
            nether.getPopulators().add(new OrePopulator(plugin, PlayerManager.WorldType.NETHER));
            plugin.log("Added Ore BlockPopulator to nether chunks!");
            nether.getPopulators().add(new TreePopulator(plugin, PlayerManager.WorldType.NETHER, Material.SOUL_SAND));
            plugin.log("Added Tree BlockPopulator to nether chunks!");
            nether.getPopulators().add(new ShrinePopulator(plugin, PlayerManager.WorldType.NETHER));
            plugin.log("Added Shrine BlockPopulator to nether chunks!");
            netherInitComplete = true;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))return;
        if(event.getInventory().hashCode() != plugin.executor.allegianceChangeInv.hashCode()){
            return;
        }
        if(!event.isLeftClick()){
            event.setCancelled(true);
            return;
        }
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)return;
        switch (event.getCurrentItem().getType()){
            case GRASS:
                plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.OVERWORLD, (Player)event.getWhoClicked());
                break;
            case NETHERRACK:
                plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.NETHER, (Player)event.getWhoClicked());
                break;
            case ENDER_STONE:
                plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.END, (Player)event.getWhoClicked());
                break;
            default:
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Fatal Error. This Shouldn't be possible, report it to nicka101");
        }
        ((Player) event.getWhoClicked()).sendMessage(ChatColor.GOLD + "Welcome to the Winning Team!");
        event.getWhoClicked().closeInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processPlayerInteractEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processBlockBreak(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processMoveEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processTeleportEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processRespawnEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWaterFlow(final BlockFromToEvent event){
        if(event.getBlock().getType() != Material.WATER && event.getBlock().getType() != Material.STATIONARY_WATER)return;
        ArrayList<Player> playersInRange = getPlayersInRange(event.getToBlock().getLocation(), 2);
        PlayerManager playerManager = plugin.getPlayerManager();
        for(Player p : playersInRange){
            playerManager.GetHandlerForPlayer(p)
                    .processNearbyWaterMove(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getEntity())
                .processDeathEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void teleportBlockHandler(final PlayerInteractEvent event){
        if(event.getAction() != Action.PHYSICAL)return;
        if((event.getClickedBlock().getType() != Material.IRON_PLATE &&
                event.getClickedBlock().getType() != Material.GOLD_PLATE) ||
                event.getClickedBlock().getRelative(BlockFace.DOWN).getType() != Material.SPONGE)return;
        Block target = null;
        switch (event.getClickedBlock().getType()){
            case IRON_PLATE:
                target = event.getClickedBlock().getWorld().getHighestBlockAt(event.getClickedBlock().getLocation());
                break;
            case GOLD_PLATE:
                target = getNextOpenSpace(
                        event.getClickedBlock().getChunk(), event.getClickedBlock().getY() + 1,
                        event.getClickedBlock().getX() - (event.getClickedBlock().getChunk().getX() * 16),
                        event.getClickedBlock().getZ() - (event.getClickedBlock().getChunk().getZ() * 16)
                );
                break;
        }
        if(target == null || target == event.getClickedBlock())return;
        Location targetLocation = target.getLocation().add(0.5, 1, 0.5);
        targetLocation.setPitch(event.getPlayer().getLocation().getPitch());
        targetLocation.setYaw(event.getPlayer().getLocation().getYaw());
        event.getPlayer().teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private Block getNextOpenSpace(Chunk chunk, int startY, int x, int z){
        Block lowestAirAboveBlock = null;
        Block lastSolid = null;
        for(int y = startY; y < chunk.getWorld().getMaxHeight(); y++){

            Block b = chunk.getBlock(x, y, z);
            if(b.getType() == Material.AIR){
                if(lowestAirAboveBlock != null  && lastSolid != null && lowestAirAboveBlock != lastSolid && b.getY() - lowestAirAboveBlock.getY() >= 2){
                    return lastSolid;
                } else if(lowestAirAboveBlock == null || lowestAirAboveBlock.getType() != Material.AIR){
                    lowestAirAboveBlock = b;
                }
            } else {
                lastSolid = b;
                lowestAirAboveBlock = b;
            }
        }
        return chunk.getWorld().getHighestBlockAt((chunk.getX() * 16) + x, (chunk.getZ() * 16) + z);
    }

    private ArrayList<Player> getPlayersInRange(Location loc, double range){
        ArrayList<Player> inRange = new ArrayList<>();
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(loc.distance(p.getLocation()) <= range) inRange.add(p);
        }
        return inRange;
    }
}
