package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class OverworldHandler extends WorldHandler {

    public OverworldHandler(ThreeWorlds plugin){
        super(plugin);
    }

    @Override
    public void processDamageEvent(EntityDamageEvent event){
        if(event.getCause() == DamageCause.DROWNING || event.getCause() == DamageCause.FALL)event.setCancelled(true);
    }

    @Override
    public void processEntityTargetEvent(EntityTargetEvent event){
        //Do Nothing
    }
}
