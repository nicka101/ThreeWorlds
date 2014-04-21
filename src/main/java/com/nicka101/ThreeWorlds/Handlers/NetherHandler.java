package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.block.Block;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class NetherHandler extends WorldHandler {

    public NetherHandler(ThreeWorlds plugin){
        super(plugin);
    }

    @Override
    public void processDamageEvent(EntityDamageEvent event){
        super.processDamageEvent(event);
        if(event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA){
            event.getEntity().setFireTicks(0); //Extinguish it
            event.setCancelled(true);
        }
    }

    @Override
    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        super.processPlayerAttackEvent(event);
        if(event.getEntity() instanceof PigZombie)event.setCancelled(true);
    }

    @Override
    public void processPlayerAttackedEvent(EntityDamageByEntityEvent event){
        super.processPlayerAttackedEvent(event);
        if(event.getDamager() instanceof PigZombie)event.setCancelled(true);
    }

    @Override
    public void processEntityTargetEvent(EntityTargetEvent event){
        if(event.getEntity() instanceof PigZombie)event.setCancelled(true);
    }

    @Override
    public void processMoveEvent(PlayerMoveEvent event){
        TouchingWater(event.getPlayer());
    }
}
