package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager.WorldType;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class OrePopulator extends BlockPopulator {

    private final Material replaceable;
    private final int coalChance;
    private final int ironChance;
    private final int ironMin;
    private final int goldChance;
    private final int goldMin;
    private final int redstoneChance;
    private final int redstoneMin;
    private final int lapisChance;
    private final int lapisMin;
    private final int diamondChance;
    private final int diamondMin;
    private final int coalSize;
    private final int ironSize;
    private final int goldSize;
    private final int redstoneSize;
    private final int lapisSize;
    private final int diamondSize;

    public OrePopulator(ThreeWorlds plugin, WorldType worldType){
        switch (worldType){
            case NETHER:
                replaceable = Material.NETHERRACK;
                break;
            case END:
                replaceable = Material.ENDER_STONE;
                break;
            default:
                replaceable = Material.STONE; //Should never occur but handle it anyway
        }
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("options." + worldType.toString().toLowerCase() + ".generation.ores");
        coalChance = config.getInt("rate.coal", 20);
        ironChance = config.getInt("rate.iron", 8);
        ironMin = coalChance + 1;
        goldChance = config.getInt("rate.gold", 4);
        goldMin = coalChance + ironChance + 1;
        redstoneChance = config.getInt("rate.redstone", 9);
        redstoneMin = goldMin + goldChance + 1;
        lapisChance = config.getInt("rate.lapis", 3);
        lapisMin = redstoneMin + redstoneChance + 1;
        diamondChance = config.getInt("rate.diamond", 1);
        diamondMin = lapisMin + lapisChance + 1;

        coalSize = config.getInt("size.coal", 6);
        ironSize = config.getInt("size.iron", 4);
        goldSize = config.getInt("size.gold", 4);
        redstoneSize = config.getInt("size.redstone", 9);
        lapisSize = config.getInt("size.lapis", 3);
        diamondSize = config.getInt("size.diamond", 4);
    }

    public void populate(World world, Random random, Chunk chunk){
        for(int x = 1; x <= 16; x++){
            for(int y = 1; y <= 256; y++){
                for(int z = 1; z <= 16; z++){
                    Block b = chunk.getBlock(x, y, z);
                    if(b.getType() != replaceable)continue;
                    int rand = random.nextInt(50000);
                    if(rand >= 0 && rand < coalChance){
                        generateResourceChunk(b, random, Material.COAL_ORE, coalSize);
                    } else if(rand >= ironMin && rand < ironMin + ironChance){
                        generateResourceChunk(b, random, Material.IRON_ORE, ironSize);
                    } else if(rand >= goldMin && rand < goldMin + goldChance) {
                        generateResourceChunk(b, random, Material.GOLD_ORE, goldSize);
                    } else if(rand >= redstoneMin && rand < redstoneMin + redstoneChance) {
                        generateResourceChunk(b, random, Material.REDSTONE_ORE, redstoneSize);
                    } else if(rand >= lapisMin && rand < lapisMin + lapisChance){
                        generateResourceChunk(b, random, Material.LAPIS_ORE, lapisSize);
                    } else if(rand >= diamondMin && rand < diamondMin + diamondChance){
                        generateResourceChunk(b, random, Material.DIAMOND_ORE, diamondSize);
                    }
                }
            }
        }
    }

    private void generateResourceChunk(Block center, Random random, Material mat, int count){
        ArrayList<Block> inCircle = new ArrayList<>();
        for(int x = center.getX() - 2; x <= center.getX() + 2; x++){
            for(int y = center.getY() - 2; y <= center.getY() + 2; y++){
                for(int z = center.getZ() - 2; z <= center.getZ(); z++){
                    if(Math.pow(center.getX() - x, 2) + Math.pow(center.getY() - y, 2) + Math.pow(center.getZ() - z, 2) <= 8){
                        if(center.getWorld().getBlockAt(x, y, z).getType() == replaceable){
                            inCircle.add(center.getWorld().getBlockAt(x, y, z));
                        }
                    }
                }
            }
        }
        if(inCircle.size() == 0)return;
        int trueCount = count == 1 ? 1 : random.nextInt(count - 1) + 1;
        while(trueCount > 0){
            Block b = inCircle.get(random.nextInt(inCircle.size()));
            b.setType(mat);
            inCircle.remove(b);
            if(inCircle.size() == 0)return;
            trueCount--;
        }
    }
}
