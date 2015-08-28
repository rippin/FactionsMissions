package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.Configs.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeMissionLength {

    private Mission m;
    private FactionsMissions plugin;
    private int delay;
    private String broadcastMessage;
    private BukkitTask task;
    private boolean hasEntered = false;

    public TimeMissionLength(FactionsMissions plugin, Mission m, int delay){
    this.plugin = plugin;
        this.m = m;
    this.delay = delay;
     broadcastMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage"));
    }

    public void startCountdown(){

        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {

            public void run() {
                //announce messages soon?
                //cancel if mission is not active aka forceended
                if (!MissionManager.isActiveMission(m.getName())){
                    task.cancel();
                }
                List<String> names = new ArrayList<String>();
                for (Player player : Bukkit.getWorld(MissionManager.getMissionWorld()).getPlayers()){
                if (MissionManager.isPlayerInMissionRegion(m, player.getLocation())){
                        if (!hasEntered){
                            hasEntered = true;
                        }
                        names.add(player.getName());
                    }
                }
                //No one is in the mission region and someone was in the region. So lose.
                if (names.isEmpty() && hasEntered){
                    m.end();
                    Bukkit.broadcastMessage(Utilss.prefix + ChatColor.GOLD +  m.getName() + ChatColor.GRAY +
                            " mission has been failed, no one is in the mission area anymore." );
                    task.cancel();
                }
            if (hasEntered) {
                if (delay % 20 == 0){
                    spawnMobsNearPlayers(m);
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + " A new wave of mobs have been spawned");
                }
                if (delay % 40 == 0){
                    m.spawnImportantEntities();
                }
                if (delay % 30 == 0){
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " seconds left to survive.");

                }
                if (delay <= 5 && delay > 0){
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " seconds left to survive.");
                }

                //WIN Give Everyone that survived a reward.
                if (delay == 0) {
                    List<Player> n = MissionManager.getPlayersInMissionregionObject(m, MissionManager.getMissionWorld());
                    for (Player p : n) {
                    //get random player in region at time of victory
                    String s = p.getName();
                    m.giveRewards(s);
                    Bukkit.broadcastMessage(Utilss.prefix + ChatColor.GOLD +  m.getName() + ChatColor.GRAY +
                            " mission has been completed and the winners have been given the rewards.");
                    }
                    m.end(); //end that ish
                    task.cancel();
                    }

                --delay;
             }
            }
        }, 20L, 20L);
    }

    public void spawnMobsNearPlayers(Mission miss){
        List<Player> players = MissionManager.getPlayersInMissionregionObject(miss, miss.getWorld().getName());
        for (Player p : players){
            for (Location loc : miss.getSpawns()){
                if (p.getLocation().toVector().distance(loc.toVector())  <= 20 ){
                    Random rand = new Random();
                    int x = rand.nextInt(miss.getCustomEntities().size());
                    String mobName = miss.getCustomEntities().get(x);
                    Mob mob = MobsManager.getMob(mobName);
                    mob.spawnMob(loc, miss, "CustomEntity");
                }

            }



        }

    }
}
