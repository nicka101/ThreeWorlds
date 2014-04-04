package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Generation.OrePopulator;
import com.nicka101.ThreeWorlds.Generation.TreePopulator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
@SuppressWarnings("unused")
public class EventListener implements Listener {

    private final ThreeWorlds plugin;
    private boolean netherInitComplete = false;

    protected EventListener(ThreeWorlds plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return; //we handle this in a seperate handler
        if(event.getEntity() instanceof Player){
            plugin.getPlayerManager()
                    .GetHandlerForPlayer((Player) event.getEntity())
                    .processDamageEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player || event.getDamager() instanceof Player))return;
        PlayerManager playerManager = plugin.getPlayerManager();
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            playerManager.GetHandlerForPlayer((Player) event.getDamager())
                    .processPvpEvent(event);
        } else if(event.getDamager() instanceof Player){
            playerManager.GetHandlerForPlayer((Player) event.getDamager())
                    .processPlayerAttackEvent(event);
        } else {
            playerManager.GetHandlerForPlayer((Player) event.getEntity())
                    .processPlayerAttackedEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerSwitchItem(PlayerItemHeldEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processSwitchItemEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event){
        if(!(event.getTarget() instanceof Player))return;
        plugin.getPlayerManager().GetHandlerForPlayer((Player) event.getTarget())
                .processEntityTargetEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processWorldChange(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent event){
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processLoginEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldLoad(WorldInitEvent event){
        if(!netherInitComplete){
            if(event.getWorld().getEnvironment() != World.Environment.NETHER)return;
            World nether = event.getWorld();
            nether.getPopulators().add(new OrePopulator(plugin, PlayerManager.WorldType.NETHER));
            plugin.log("Added Ore BlockPopulator to nether chunks!");
            nether.getPopulators().add(new TreePopulator(plugin, PlayerManager.WorldType.NETHER, Material.SOUL_SAND));
            plugin.log("Added Tree BlockPopulator to nether chunks!");
            netherInitComplete = true;
        }
    }
}
