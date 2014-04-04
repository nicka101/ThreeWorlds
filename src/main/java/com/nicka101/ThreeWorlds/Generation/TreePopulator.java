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

import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by Nicka101 on 03/04/2014.
 */
public class TreePopulator extends BlockPopulator {

    private final int chance;
    private final Material filter;

    public TreePopulator(ThreeWorlds plugin, WorldType worldType, Material filter){
        this.chance = plugin.getConfig().getInt("options." + worldType.toString().toLowerCase() + ".generation.tree", 500);
        this.filter = filter;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                Block b = world.getHighestBlockAt(chunkX + x, chunkZ + z);
                if(b == null || b.getY() > 240 || b.getType() != filter || random.nextInt(10000) >= chance)continue;
                BlockState old = b.getState();
                b.setType(Material.DIRT);
                world.generateTree(b.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
                old.update(true);
            }
        }
    }
}
