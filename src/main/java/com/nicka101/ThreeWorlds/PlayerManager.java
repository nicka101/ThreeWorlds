package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Handlers.EndHandler;
import com.nicka101.ThreeWorlds.Handlers.WorldHandler;
import com.nicka101.ThreeWorlds.Handlers.NetherHandler;
import com.nicka101.ThreeWorlds.Handlers.OverworldHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class PlayerManager {
    private final ThreeWorlds plugin;
    private HashMap<WorldType, HashSet<UUID>> playerMap = new HashMap<>(); //Use a string to save memory
    private HashMap<WorldType, WorldHandler> handlers = new HashMap<>();
    private HashMap<WorldType, Integer> warFunds = new HashMap<>();
    private static WorldHandler unassignedPlayerHandler;
    private Scoreboard scoreboard = null;

    protected PlayerManager(ThreeWorlds plugin){
        this.plugin = plugin;
        for(WorldType w : WorldType.values()){
            playerMap.put(w, new HashSet<UUID>());
        }
        InitHandlers();
    }

    protected void Init(){
        loadPlayerMap();
        loadWarFunds();
    }

    protected void Shutdown(){
        savePlayerMap();
    }

    private void InitHandlers(){
        handlers.put(WorldType.OVERWORLD, new OverworldHandler(plugin));
        handlers.put(WorldType.NETHER, new NetherHandler(plugin));
        handlers.put(WorldType.END, new EndHandler(plugin));
    }

    private void prepareScoreboard(){
        scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective health = scoreboard.registerNewObjective("Health", "health");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        Objective warFund = scoreboard.registerNewObjective("War Fund", "dummy");
        warFund.setDisplaySlot(DisplaySlot.SIDEBAR);
        for(WorldType worldType : WorldType.values()){
            if(scoreboard.getTeam(worldType.toString()) == null){
                Team newTeam = scoreboard.registerNewTeam(worldType.toString());
                newTeam.setAllowFriendlyFire(false);
                newTeam.setCanSeeFriendlyInvisibles(true);
                newTeam.setPrefix(worldType.colourString() + worldType.toString() + " ");
            }
            Score fund = warFund.getScore(worldType.colourString() + worldType.toString());
            try{
                fund.setScore(warFunds.get(worldType));
            } catch (IllegalArgumentException e){}
        }
        for(Player p : plugin.getServer().getOnlinePlayers()){
            HandlePlayerLogin(p);
        }
    }

    public void HandlePlayerLogin(Player player){
        if(scoreboard == null)prepareScoreboard();
        player.setScoreboard(scoreboard);
        if(!IsPlayerInWorld(player))return;
        scoreboard.getTeam(GetPlayerWorld(player).toString()).addPlayer(player);
    }

    public void HandlePlayerLogout(Player player){
        //TODO: Process Last Damage for Combat Log
    }

    public void AddPlayerToWorld(WorldType world, final Player player){
        player.setMaxHealth(plugin.getConfig().getDouble("options." + world.toString().toLowerCase() + ".health", 20));
        player.saveData();
        playerMap.get(world).add(player.getUniqueId());
        scoreboard.getTeam(world.toString()).addPlayer(player);
    }

    public void RemovePlayerFromWorld(WorldType world, Player player){
        playerMap.get(world).remove(player.getUniqueId());
        scoreboard.getTeam(world.toString()).removePlayer(player);
    }

    public void ChangePlayerWorld(WorldType world, Player player){
        WorldType old = GetPlayerWorld(player);
        if(old == world)return;
        RemovePlayerFromWorld(old, player);
        AddPlayerToWorld(world, player);
    }

    public boolean IsPlayerInWorld(Player player){
        for(WorldType w : WorldType.values()){
            if(playerMap.get(w).contains(player.getUniqueId()))return true;
        }
        return false;
    }

    public boolean IsPlayerInWorld(Player player, WorldType world){
        return playerMap.get(world).contains(player.getUniqueId());
    }

    public boolean IsPlayerHostile(Player player, Player player2){
        WorldType a = GetPlayerWorld(player2);
        if(a == null)return true;
        return !IsPlayerInWorld(player, a);
    }

    public WorldType GetPlayerWorld(Player player){
        for(WorldType w : WorldType.values()){
            if(playerMap.get(w).contains(player.getUniqueId()))return w;
        }
        return null;
    }

    protected WorldHandler GetHandlerForPlayer(Player player){
        WorldType w = GetPlayerWorld(player);
        if(w == null){
            if(unassignedPlayerHandler == null){
                unassignedPlayerHandler = new WorldHandler(plugin);
            }
            return unassignedPlayerHandler;
        }
        return handlers.get(w);
    }

    private void loadPlayerMap(){
        FileConfiguration config = plugin.getConfig();
        for(WorldType w : WorldType.values()){
            List<String> players = config.getStringList("players." + w.toString().toLowerCase());
            HashSet<UUID> worldPlayerMap = playerMap.get(w);
            for(String playerUUID : players){
                try {
                    UUID uuid = UUID.fromString(playerUUID);
                    worldPlayerMap.add(uuid);
                }catch (IllegalArgumentException e){
                    plugin.warning("Failed to Convert Player UUID \"" + playerUUID + "\" into a valid UUID!");
                }
            }
        }
    }

    private void savePlayerMap(){
        for(WorldType w : WorldType.values()){
            ArrayList<String> saveList = new ArrayList<>();
            HashSet<UUID> playerUUIDs = playerMap.get(w);
            for(UUID uuid : playerUUIDs){
                saveList.add(uuid.toString());
            }
            plugin.getConfig().set("players." + w.toString().toLowerCase(), saveList);
        }
        plugin.saveConfig();
    }

    private void loadWarFunds(){
        //TODO: Actually Load and Save War Funds
        for(WorldType worldType : WorldType.values()){
            warFunds.put(worldType, 0);
        }
    }

    public static enum WorldType {
        OVERWORLD,
        NETHER,
        END;

        @Override
        public String toString(){
            switch (this){
                case OVERWORLD:
                    return "Overworld";
                case NETHER:
                    return "Nether";
                case END:
                    return "End";
            }
            return "";
        }

        public String colourString(){
            switch (this){
                case OVERWORLD:
                    return ChatColor.GREEN.toString();
                case NETHER:
                    return ChatColor.RED.toString();
                case END:
                    return ChatColor.DARK_PURPLE.toString();
            }
            return "";
        }
    }
}
