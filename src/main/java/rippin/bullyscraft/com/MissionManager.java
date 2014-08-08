package rippin.bullyscraft.com;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rippin.bullyscraft.com.Configs.MissionsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionManager {
    private static List<Mission> missions = new ArrayList<Mission>();
    private static WorldGuardPlugin worldGuard = FactionsMissions.instance.getWorldGuard();
    private static List<Mission> queuedMissions = new ArrayList<Mission>();

    public static void loadMissions(FactionsMissions plugin){
        for (String key : MissionsConfig.getConfig().getConfigurationSection("Missions").getKeys(false)){
            Mission m = new Mission(key);
            m.loadMissionData();
            getAllMissions().add(m);
            plugin.logger.info(m.getName() + " mission has been loaded from file");

        }
    }
    public static List<String> getPlayersInAnyMissionRegion(){  // by uuid
    List<String> players = new ArrayList<String>();
    for (Player p : Bukkit.getOnlinePlayers()){
        Location l = p.getLocation();
        for (Mission m : getAllMissions()){
        if (m.isLocationInMissionRegion(l)){
            players.add(p.getUniqueId().toString());
          }
        }
      }
       return players;
    }

    public static List<Mission> getAllMissions(){
        return missions;
    }

    public static List<Mission> getActiveMissions(){
        List<Mission> active = new ArrayList<Mission>();
        for (Mission m : missions){
            if (m.getStatus() == MissionStatus.ACTIVE){
              active.add(m);
            }
        }
        return active;
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
        int min = 0;
        int max = (queuedMissions.size() - 1);
        Random r = new Random();
        int randomInt = r.nextInt((max - min) + 1) + min;

       return queuedMissions.get(randomInt);
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
    public static Mission getMission(String s){
        for (Mission m : getAllMissions()){
            if (m.getName().equalsIgnoreCase(s)){
                return  m;
            }
        }
        return null;
    }
    public static void setMissionSpawnsToConfig(Mission m){
        List<String> strings = new ArrayList<String>();
        for (Location loc : m.getSpawns()){
            strings.add(Utils.serializeLoc(loc));
        }
        MissionsConfig.getConfig().set("Missions." + m.getName() + ".Spawns", strings);
        MissionsConfig.saveFile();
    }
    public static void setImportantEntityToConfig(Mission m, String key){

    }

    public static void endActiveMissions(){
        for (Mission m : getActiveMissions()){
            m.end();
        }
    }
}
