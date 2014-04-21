package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.HashSet;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class OverworldHandler extends WorldHandler {

    public OverworldHandler(ThreeWorlds plugin){
        super(plugin);
    }

    @Override
    public void processDamageEvent(EntityDamageEvent event){
        super.processDamageEvent(event);
        if(event.getCause() == DamageCause.DROWNING || event.getCause() == DamageCause.FALL)event.setCancelled(true);
    }

    public void processEntityTargetEvent(EntityTargetEvent event){
        //Do Nothing
    }

    @Override
    @SuppressWarnings("deprecation")
    public void processBlockBreak(BlockBreakEvent event){
        super.processBlockBreak(event);
        if(event.getPlayer().isSneaking() || event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.DIAMOND_PICKAXE)return;
        HashSet<Block> toBreak = getBlocksToBreak(event.getBlock(), event.getBlock().getFace(event.getPlayer().getLastTwoTargetBlocks(null, 5).get(0)));
        Material blockType = event.getBlock().getType();
        for(Block b : toBreak){
            if(b.getType() == blockType)b.breakNaturally(event.getPlayer().getItemInHand());
        }
    }

    private HashSet<Block> getBlocksToBreak(Block block, BlockFace targetFace){ //Target face is estimated
        if(targetFace == null)return new HashSet<>();
        HashSet<Block> blocks = new HashSet<>();
        if(targetFace == BlockFace.UP || targetFace == BlockFace.DOWN) {
            Block left = block.getRelative(BlockFace.WEST);
            blocks.add(left);
            blocks.add(left.getRelative(BlockFace.NORTH));
            blocks.add(left.getRelative(BlockFace.SOUTH));
            Block right = block.getRelative(BlockFace.EAST);
            blocks.add(right);
            blocks.add(right.getRelative(BlockFace.NORTH));
            blocks.add(right.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
        } else if(targetFace == BlockFace.NORTH || targetFace == BlockFace.SOUTH) {
            Block left = block.getRelative(BlockFace.WEST);
            blocks.add(left);
            blocks.add(left.getRelative(BlockFace.UP));
            blocks.add(left.getRelative(BlockFace.DOWN));
            Block right = block.getRelative(BlockFace.EAST);
            blocks.add(right);
            blocks.add(right.getRelative(BlockFace.UP));
            blocks.add(right.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
        } else if(targetFace == BlockFace.EAST || targetFace == BlockFace.WEST){
            Block left = block.getRelative(BlockFace.NORTH);
            blocks.add(left);
            blocks.add(left.getRelative(BlockFace.UP));
            blocks.add(left.getRelative(BlockFace.DOWN));
            Block right = block.getRelative(BlockFace.SOUTH);
            blocks.add(right);
            blocks.add(right.getRelative(BlockFace.UP));
            blocks.add(right.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
        }
        return blocks;
    }
}