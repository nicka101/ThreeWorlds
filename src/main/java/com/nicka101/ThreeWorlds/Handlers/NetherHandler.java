package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.PlayerManager;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class NetherHandler extends WorldHandler {

    public NetherHandler(PlayerManager playerManager){
        super(playerManager);
    }

    @Override
    public void processDamageEvent(EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA)event.setCancelled(true);
    }

    @Override
    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof PigZombie)event.setCancelled(true);
    }

    @Override
    public void processPlayerAttackedEvent(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof PigZombie)event.setCancelled(true);
    }

    @Override
    public void processEntityTargetEvent(EntityTargetEvent event){
        if(event.getEntity() instanceof PigZombie)event.setCancelled(true);
    }
}
