package com.nicka101.ThreeWorlds.Events;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

/**
 * Created by Nicka101 on 21/04/2014.
 */
public class EntityDamageByWaterEvent extends EntityDamageByBlockEvent {

    public EntityDamageByWaterEvent(Block damager, Entity damagee, DamageCause cause, double damage){
        super(damager, damagee, cause, damage);
    }
}
