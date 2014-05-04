package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ThreeWorlds;
import com.nicka101.ThreeWorlds.WaterCollisionType;
import org.bukkit.block.Block;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
    @SuppressWarnings("deprecation")
    public void processMoveEvent(PlayerMoveEvent event){
        super.processMoveEvent(event);
        TouchingWater(event.getPlayer(), WaterCollisionType.DAMAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void processTeleportEvent(PlayerTeleportEvent event){
        super.processTeleportEvent(event);
        TouchingWater(event.getPlayer(), WaterCollisionType.DAMAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void processRespawnEvent(PlayerRespawnEvent event){
        super.processRespawnEvent(event);
        TouchingWater(event.getPlayer(), WaterCollisionType.DAMAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void processNearbyWaterMove(Player p){
        super.processNearbyWaterMove(p);
        TouchingWater(p, WaterCollisionType.DAMAGE);
    }

}
