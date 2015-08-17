package rippin.bullyscraft.com.Events;


import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.FactionsMissions;
import rippin.bullyscraft.com.Mission;
import rippin.bullyscraft.com.MissionManager;

import java.util.List;

public class WinFireWorksCountdown {
    private BukkitTask task;
    private FactionsMissions plugin;
    private Mission m;
    public WinFireWorksCountdown(FactionsMissions plugin, Mission m ){
        this.plugin = plugin;
        this.m = m;
    }

    public void StartCountdown(){
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            int i = 0;
            public void run() {
                if (i > 6) {
                    getTask().cancel();
                }
                List<Player> players = MissionManager.getPlayersInMissionregionObject(getMission(), getMission().getWorld().getName());
                for (Player player : players){
                    getMission().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                }
                i++;
            }
        },20L,35L);
    }
    public Mission getMission() {return this.m; }
    public BukkitTask getTask() {return this.task; }
}
