package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nicka101 on 03/04/2014.
 */
public class EndGenerator extends ChunkGenerator {

    private final double SCALE;
    private final int BASE;
    private final int VARIATION;
    private final double THRESHOLD;
    private final double MULTIPLIER;
    private final boolean SKYLANDS;
    private final List<BlockPopulator> populators;
    private SimplexOctaveGenerator octaveGenerator = null;

    public EndGenerator(ThreeWorlds plugin){
        populators = new ArrayList<>();
        populators.add(new OrePopulator(plugin, PlayerManager.WorldType.END));
        populators.add(new TreePopulator(plugin, PlayerManager.WorldType.END, Material.ENDER_STONE));
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("options.end.generation");
        this.SCALE = sec.getDouble("scale", 91);
        this.BASE = sec.getInt("base", 128);
        this.VARIATION = sec.getInt("variation", 127);
        this.THRESHOLD = sec.getDouble("threshold", 0.075);
        this.MULTIPLIER = sec.getDouble("multiplier", 0.13);
        this.SKYLANDS = sec.getBoolean("skylands", true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes){
        if(octaveGenerator == null){
            octaveGenerator = new SimplexOctaveGenerator(world.getSeed(), 8);
        }
        octaveGenerator.setScale(1/SCALE);
        byte[][] chunk = new byte[world.getMaxHeight() / 16][];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y=BASE - VARIATION; y< BASE + VARIATION; y++) {
                    double noise = octaveGenerator.noise((chunkX * 16) + x, y, (chunkZ * 16) + z, 0.5, 0.5);
                    double a = (y - BASE) / 16.0;
                    if(SKYLANDS){
                        a = Math.abs(a);
                    }
                    a = MULTIPLIER * a;
                    if(noise - a > THRESHOLD)
                        setBlock(x, y, z, chunk, Material.ENDER_STONE);
                }
            }
        }
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world){
        return populators;
    }

    @SuppressWarnings("unused")
    private byte getBlock(int x, int y, int z, byte[][] chunk) {
        if (chunk[y>>4] == null)
            chunk[y>>4] = new byte[16*16*16];
        if (!(y<=256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))
            return 0; //Out of bounds
        try {
            return chunk[y>>4][((y & 0xF) << 8) | (z << 4) | x];
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*
     * Sets a block in the chunk. If the Block section doesn't exist, it allocates it.
     * [y>>4] the section id (y/16)
     * the math for the second offset confuses me
     */
    @SuppressWarnings("deprecation")
    private void setBlock(int x, int y, int z, byte[][] chunk, Material material) {
        if (chunk[y>>4] == null)
            chunk[y>>4] = new byte[16*16*16];
        if (!(y<=256 && y >= 0 && x <= 16 && x >= 0 && z <= 16 && z >= 0))return; //Out of bounds
        try {
            chunk[y>>4][((y & 0xF) << 8) | (z << 4) | x] = (byte)material.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
