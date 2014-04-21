package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Events.EntityDamageByWaterEvent;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Nicka101 on 21/04/2014.
 */
public class WaterDamageTask extends BukkitRunnable {

    private final ThreeWorlds plugin;

    public WaterDamageTask(final ThreeWorlds plugin){
        this.plugin = plugin;
    }

    @Override
    public void run(){
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(!p.hasMetadata("waterDamageBlock")) continue;
            Block source;
            try {
                source = (Block)p.getMetadata("waterDamageBlock").get(0).value();
            } catch (ClassCastException e){
                continue;
            } catch (IndexOutOfBoundsException e){
                continue;
            }
            int damageTicks;
            try {
                if (p.hasMetadata("waterDamageTicks")) damageTicks = p.getMetadata("waterDamageTicks").get(0).asInt();
                else damageTicks = 0;
            } catch (IndexOutOfBoundsException e){
                damageTicks = 0;
            }
            if(damageTicks % 20 != 0){
                updateTicks(p, ++damageTicks);
                continue;
            }
            damageByWater(p, source);
            updateTicks(p, 1);
        }
    }

    private void updateTicks(Player player, int ticks){
        if(player.hasMetadata("waterDamageTicks")) player.removeMetadata("waterDamageTicks", plugin);
        player.setMetadata("waterDamageTicks", new FixedMetadataValue(plugin, ticks));
    }

    private void damageByWater(Player player, Block b){
        if(player.getGameMode() == GameMode.CREATIVE)return;
        EntityDamageByWaterEvent event = new EntityDamageByWaterEvent(b, player, EntityDamageEvent.DamageCause.CUSTOM, 4);
        plugin.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())return;
        player.setLastDamageCause(event);
        player.damage(event.getDamage());
    }
}
