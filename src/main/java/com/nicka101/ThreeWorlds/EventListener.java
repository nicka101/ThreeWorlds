package com.nicka101.ThreeWorlds;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
@SuppressWarnings("unused")
public class EventListener implements Listener {

    private final ThreeWorlds plugin;

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
    public void onEntityTarget(EntityTargetEvent event){
        if(!(event.getTarget() instanceof Player))return;
        plugin.getPlayerManager().GetHandlerForPlayer((Player) event.getTarget())
                .processEntityTargetEvent(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if(event.getFrom().getWorld() == event.getTo().getWorld())return;
        plugin.getPlayerManager().GetHandlerForPlayer(event.getPlayer())
                .processWorldChange(event);
    }
}
