package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.Configs.Config;

import java.util.ArrayList;
import java.util.List;

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
        final int saveDelay = delay;

        task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                //announce messages soon?
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
                    m.spawnCustomEntities();
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " A new wave of mobs have been spawned");
                }
                if (delay % 40 == 0){
                    m.spawnImportantEntities();
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " A new wave of mobs have been spawned");
                }
                if (delay % 30 == 0){
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " seconds left to survive.");

                }
                if (delay <= 5 && delay > 0){
                    MissionManager.messagePlayersInMission(m, Utilss.prefix + delay + " seconds left to survive.");
                }

                //WIN Pick a random person to give them the reward.
                if (delay == 0) {
                    List<String> n = MissionManager.getPlayersInMissionregion(m, MissionManager.getMissionWorld());
                    int r = Utilss.randInt(0, n.size() -1);
                    //get random player in region at time of victory
                    String s = n.get(r);
                    m.giveRewards(s);
                    m.end(); //end that ish
                    Bukkit.broadcastMessage(Utilss.prefix + ChatColor.GOLD +  m.getName() + ChatColor.GRAY +
                            " mission has been completed and " +  ChatColor.RED + s + " has been given the rewards.");

                    task.cancel();
                }
                --delay;
             }
            }
        }, 20L, 20L);
    }
}
