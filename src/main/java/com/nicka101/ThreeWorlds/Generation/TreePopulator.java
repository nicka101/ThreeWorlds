package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager.WorldType;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.generator.BlockPopulator;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Nicka101 on 03/04/2014.
 */
public class TreePopulator extends BlockPopulator {

    private final int chance;
    private final Material filter;
    private final ThreeWorlds plugin;

    public TreePopulator(ThreeWorlds plugin, WorldType worldType, Material filter){
        this.chance = plugin.getConfig().getInt("options." + worldType.toString().toLowerCase() + ".generation.tree", 500);
        this.filter = filter;
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(Block b : getOpenSpaceBlocks(chunk, x, z)){
                    if(b == null || b.getY() > world.getMaxHeight() - 6 || b.getType() != filter || random.nextInt(10000) >= chance)continue;
                    BlockState old = b.getState();
                    b.setType(Material.DIRT);
                    world.generateTree(b.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
                    old.update(true);
                }
            }
        }
    }

    private HashSet<Block> getOpenSpaceBlocks(Chunk chunk, int x, int z){
        HashSet<Block> blocks = new HashSet<>();
        Block lowestAirAboveBlock = null;
        Block lastSolid = null;
        for(int y = 0; y < chunk.getWorld().getMaxHeight(); y++){

            Block b = chunk.getBlock(x, y, z);
            if(b.getType() == Material.AIR){
                if(lowestAirAboveBlock != null  && lastSolid != null && lowestAirAboveBlock != lastSolid && b.getY() - lowestAirAboveBlock.getY() >= 5){
                    blocks.add(lastSolid);
                } else if(lowestAirAboveBlock == null || lowestAirAboveBlock.getType() != Material.AIR){
                    lowestAirAboveBlock = b;
                }
            } else {
                lastSolid = b;
                lowestAirAboveBlock = b;
            }
        }
        return blocks;
    }
}
