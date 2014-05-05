package com.nicka101.ThreeWorlds.Util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;

/**
 * Created by Nicka101 on 05/05/2014.
 */
public class BlockUtil {
    private BlockUtil(){}

    public static boolean isEmptyOrLiquid(World world, int x, int y, int z){
        net.minecraft.server.v1_7_R3.Material mat = getTypeUnsafe(world, x, y, z);
        return !mat.isSolid() || mat.isLiquid();
    }

    public static void setBlockFastUnsafe(World world, int x, int y, int z, Material material){
        setBlockFastUnsafe(world, x, y, z, material, (byte) 0);
    }

    public static int getTypeIdUnsafe(World world, int x, int y, int z){
        return ((CraftWorld)world).getHandle().getTypeId(x, y, z);
    }

    public static net.minecraft.server.v1_7_R3.Material getTypeUnsafe(World world, int x, int y, int z){
        return ((CraftWorld)world).getHandle().getType(x, y, z).getMaterial();
    }

    @SuppressWarnings("deprecation")
    public static void setBlockFastUnsafe(World world, int x, int y, int z, Material material, byte data){
        ((CraftWorld)world).getHandle().getChunkAt(x >> 4, z >> 4).a(x & 0x0f, y, z & 0x0f, net.minecraft.server.v1_7_R3.Block.e(material.getId()), data);
    }
}
