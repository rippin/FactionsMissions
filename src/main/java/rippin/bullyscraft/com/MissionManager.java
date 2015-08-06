package rippin.bullyscraft.com;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rippin.bullyscraft.com.Configs.Config;
import rippin.bullyscraft.com.Configs.MissionsConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MissionManager {
    private static List<Mission> missions = new ArrayList<Mission>();
    private static WorldGuardPlugin worldGuard = FactionsMissions.instance.getWorldGuard();
    private static List<Mission> queuedMissions = new ArrayList<Mission>();
    private static List<Mission> activeMissions = new ArrayList<Mission>();
    private static List<String> revertMissions = MissionsConfig.getConfig().getStringList("Active-Missions");
    private static boolean pasteSchematic = Config.getConfig().getBoolean("Paste-Schematics");
    private static String teleportworldMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("World-Teleport-Message"));
    private static String missionWorld = Config.getConfig().getString("Mission-World");

    public static void loadMissions(FactionsMissions plugin){
        missionWorld = Config.getConfig().getString("Mission-World");
        revertMissions = MissionsConfig.getConfig().getStringList("Active-Missions");
        teleportworldMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("World-Teleport-Message"));
        pasteSchematic = Config.getConfig().getBoolean("Paste-Schematics");
        missions.clear();
        queuedMissions.clear();
        activeMissions.clear();

        //    try {
        for (String key : MissionsConfig.getConfig().getConfigurationSection("Missions").getKeys(false)){
            Mission m = new Mission(key);
            getAllMissions().add(m);
            plugin.logger.info(m.getName() + " mission has been loaded from file");

        }
  //     } catch (NullPointerException e){
  //          System.out.println("No missions loaded.");
  //      }

    }
    public static List<String> getPlayersInAnyMissionRegion(){  // by uuid
    List<String> players = new ArrayList<String>();
    for (Player p : Bukkit.getOnlinePlayers()){
        Location l = p.getLocation();
        for (Mission m : getActiveMissions()){
        if (m.isLocationInMissionRegion(l)){
            players.add(p.getUniqueId().toString());
          }
        }
      }
       return players;
    }

    public static List<String> getPlayersInMissionregionUUID(Mission m, String world){  // by uuid
        List<String> players = new ArrayList<String>();
        for (Player p : Bukkit.getWorld(world).getPlayers()){
            Location l = p.getLocation();
                if (m.isLocationInMissionRegion(l)){
                    players.add(p.getUniqueId().toString());
                }
        }
        return players;
    }

    public static List<Player> getPlayersInMissionregionObject(Mission m, String world){  // by uuid
        List<Player> players = new ArrayList<Player>();
        for (Player p : Bukkit.getWorld(world).getPlayers()){
            Location l = p.getLocation();
            if (m.isLocationInMissionRegion(l)){
                players.add(p);
            }
        }
        return players;
    }

    public static List<Mission> getAllMissions(){
        return missions;
    }

    public static List<Mission> getActiveMissions(){
        return activeMissions;
    }


    public static boolean playerInRegion(Location loc, BlockVector min, BlockVector max){
       double minX = min.getX();
       double minY = min.getY();
       double minZ = min.getZ();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();
        if (loc.getX() >= minX && loc.getX() <=maxX && loc.getY() >= minY && loc.getY() <= maxY && loc.getZ() >= minZ && loc.getZ() <= maxZ){
            return true;
        }
        return false;

    }

    public static void createMission(String name, MissionType type){
     Mission m = new Mission(name);
        m.setType(type);
        missions.add(m);
    }

    public static List<Mission> getQueuedMissions(){
        return queuedMissions;
    }

    public static Mission getRandomQueuedMission(){
        Mission m;
        do {
        int min = 0;
        int max = (queuedMissions.size() - 1);
        Random r = new Random();
        int randomInt = r.nextInt((max - min) + 1) + min;
           m = queuedMissions.get(randomInt);
        } while (m != null && !isMissionRegionActive(m));

        return  m;
    }

    public static void messagePlayersInMission(Mission m, String message){
        for (Player p : Bukkit.getOnlinePlayers()){
            if (playerInRegion(p.getLocation(), m.getMissionRegion().getMinimumPoint(), m.getMissionRegion().getMaximumPoint())){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public static boolean isMission(String s){
        for (Mission m : getAllMissions()){
            if (m.getName().equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
    public static boolean isQueuedMission(String s){
        for (Mission m : getQueuedMissions()){
            if (m.getName().equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
    public static boolean isActiveMission(String s){
        for (Mission m : getActiveMissions()){
            if (m.getName().equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
    public static Mission getMission(String s){
        for (Mission m : getAllMissions()){
            if (m.getName().equalsIgnoreCase(s)){
                return  m;
            }
        }
        return null;
    }
    public static boolean isMissionRegionActive(Mission m){
        for (Mission a : activeMissions){
            if (a.getMainPoint().getX() == m.getMainPoint().getX() && a.getMainPoint().getY() == m.getMainPoint().getY() &&
                a.getMainPoint().getZ() == m.getMainPoint().getZ()){
                return true;
            }
        }
        return false;
    }
    public static void setMissionSpawnsToConfig(Mission m){
        List<String> strings = new ArrayList<String>();
        for (Location loc : m.getSpawns()){
            strings.add(Utilss.serializeLoc(loc));
        }
        MissionsConfig.getConfig().set("Missions." + m.getName() + ".Spawns", strings);
        MissionsConfig.saveFile();
    }
    public static void setImportantEntityToConfig(Mission m, String key){
        m.getImportantEntities();
        MissionsConfig.getConfig().set("Missions." + m.getName() + ".Important-Entities." + key + ".Location", Utilss.serializeLoc(m.getImportantEntities().get(key)));
        MissionsConfig.saveFile();
    }

    public static void endActiveMissions(){
        for (Mission m : getActiveMissions()){
            m.end();
        }
    }
    public static boolean isPlayerInMissionRegion(Mission m, Location loc){
            if (m.isLocationInMissionRegion(loc)) {
                return true;
            }
        return false;
    }

    public static Mission isPlayerInActiveRegion(Location loc){
        for (Mission m : getActiveMissions()){
            if (m.isLocationInMissionRegion(loc)) {
                return m;
            }
        }
        return null;
    }


    public static Mission isPlayerInAnyMisionRegion(Location loc){
        for (Mission m : getAllMissions()){
            if (m.isLocationInMissionRegion(loc)) {
                if (m.getStatus() == MissionStatus.ACTIVE){
                    return m;
                }
            }
        }
               for (Mission m : getAllMissions()){
               if (m.isLocationInMissionRegion(loc)) {
                return m;
               }
        }

        return null;
    }

    public static boolean containsMission(List<Mission> missions, String mission){
       for (Mission m : missions){
           if (m.getName().equalsIgnoreCase(mission)){
               return true;
           }
       }
        return false;
    }

    public static void removeMission(List<Mission> missions, String mission){
        Iterator<Mission> it = missions.iterator();
        while (it.hasNext()){
            if (it.next().getName().equalsIgnoreCase(mission)){
                it.remove();
            }
        }
    }


    public static void addActiveToConfig(Mission m){
       List<String> active = MissionsConfig.getConfig().getStringList("Active-Missions");
       if (active == null){
       active = new ArrayList<String>();
       }
       if (!active.contains(m.getName())){
           active.add(m.getName());
       }
        MissionsConfig.getConfig().set("Active-Missions", active);
        MissionsConfig.saveFile();
    }

    public static void removeActiveToConfig(Mission m){
        List<String> active = MissionsConfig.getConfig().getStringList("Active-Missions");
        if (active == null){
            active = new ArrayList<String>();
        }
        if (active.contains(m.getName())){
            active.remove(m.getName());
        }
        MissionsConfig.getConfig().set("Active-Missions", active);
        MissionsConfig.saveFile();
    }
    public static void revertMissionsIfCrashed(){
           for (String s : revertMissions){
               Mission m = getMission(s);
               if (m != null) {
                m.end();
               System.out.println("Force reverting mission after crash. Mission:  " + m.getName());
               }
        }
    }

    public static Mission getMobMission(String uuid){
        for (Mission m : getActiveMissions()){
            if (m.getCustomEntitiesUUID().contains(uuid) || m.getImportantEntitiesUUID().contains(uuid)){
                return m;
            }
        }
        return null;
    }
    public static List<String> getRevertMissions(){
        return  revertMissions;
    }

    public static  boolean pasteSchematic(){
        return pasteSchematic;
    }

    public static void loadChunksinRegion(Mission m){
            int minX = m.getMissionRegion().getMinimumPoint().getBlockX();
            int minZ = m.getMissionRegion().getMinimumPoint().getBlockZ();

            int maxX = m.getMissionRegion().getMaximumPoint().getBlockX();
            int maxZ = m.getMissionRegion().getMaximumPoint().getBlockZ();

            for (int x = minX; x <=maxX; x+=16){
                for (int z = minZ; z <= maxZ; z+=16){
                    Location loc = new Location(m.getWorld(), x, 0.0, z);
                    Chunk chunk = m.getWorld().getChunkAt(loc);
                    chunk.load();
                }
            }
    }

    public static void printActiveMissionsInfo(CommandSender sender){
        sender.sendMessage(ChatColor.GOLD + "" +
                ChatColor.STRIKETHROUGH+ "-----------" + ChatColor.DARK_RED + " Active Missions "
                + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH+ "-----------");
        for (Mission m : getActiveMissions()){
            if (getActiveMissions().isEmpty()){
                sender.sendMessage(ChatColor.DARK_RED + "There are no active missions at the moment, check back later.");
            }
            else {
                int x = ((Double) m.getMainPoint().getX()).intValue();
                int y = ((Double) m.getMainPoint().getY()).intValue();
                int z = ((Double) m.getMainPoint().getZ()).intValue();

                sender.sendMessage(ChatColor.GRAY + "Mission: " +
                        ChatColor.GREEN + m.getName() + ChatColor.GRAY + " Type: " + ChatColor.GREEN + m.getType().getValue()
                        + ChatColor.GRAY + " Coords: " + "X " + ChatColor.GREEN + x + "Y: " + y + "Z: " + z);
            }

        }
    }

    public static String getTeleportworldMessage() {
        return teleportworldMessage;
    }

    public static void setTeleportworldMessage(String teleportworldMessage) {
        MissionManager.teleportworldMessage = teleportworldMessage;
    }

    public static String getMissionWorld() {
        return missionWorld;
    }

    public static void setMissionWorld(String missionWorld) {
        MissionManager.missionWorld = missionWorld;
    }
}
