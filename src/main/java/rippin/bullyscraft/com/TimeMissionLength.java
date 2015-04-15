package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import rippin.bullyscraft.com.Configs.Config;

public class TimeMissionLength {

    private Mission m;
    private FactionsMissions plugin;
    private int delay;
    private String broadcastMessage;
    private BukkitTask task;

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
                if (delay % 20 == 0){
                    m.spawnCustomEntities();
                }
                if (delay % 40 == 0){
                    m.spawnImportantEntities();
                }


                if (delay == 0) {
                    m.end(); //end that ish
                    task.cancel();
                }
                --delay;
            }
        }, 20L, 20L);
    }
}
