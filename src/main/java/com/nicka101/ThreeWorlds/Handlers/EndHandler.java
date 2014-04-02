package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.PlayerManager;
import org.bukkit.entity.Enderman;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class EndHandler extends WorldHandler {

    public EndHandler(PlayerManager playerManager){
        super(playerManager);
    }

    @Override
    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Enderman)event.setCancelled(true);
    }

    @Override
    public void processEntityTargetEvent(EntityTargetEvent event){
        if(event.getEntity() instanceof Enderman)event.setCancelled(true);
    }
}
