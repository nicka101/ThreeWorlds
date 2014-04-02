package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class WorldHandler {

    private final PlayerManager playerManager;

    public WorldHandler(PlayerManager playerManager){
        this.playerManager = playerManager;
    }

    public void processDamageEvent(EntityDamageEvent event){
        //Do nothing
    }

    public void processPvpEvent(EntityDamageByEntityEvent event){
        if(!playerManager.IsPlayerHostile((Player)event.getDamager(), (Player)event.getEntity())){
            event.setCancelled(true);
        }
    }

    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        //Do Nothing
    }

    public void processPlayerAttackedEvent(EntityDamageByEntityEvent event){
        //Do Nothing
    }

    public void processEntityTargetEvent(EntityTargetEvent event){
        event.setCancelled(true);
    }

    public void processWorldChange(PlayerTeleportEvent event){
        //Do Nothing
    }
}
